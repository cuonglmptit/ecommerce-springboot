package com.cuonglm.ecommerce.backend.auth.repository;

import com.cuonglm.ecommerce.backend.auth.entity.OtpToken;
import com.cuonglm.ecommerce.backend.auth.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * OtpTokenRepository – Interface truy cập dữ liệu thực thể {@link OtpToken}
 *
 * <p>
 * Hỗ trợ các thao tác CRUD cơ bản và các truy vấn tùy chỉnh
 * để quản lý vòng đời của mã OTP (tìm kiếm, vô hiệu hóa, dọn dẹp).
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 05 November 2025
 */
@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    /**
     * <b>
     * Tìm mã OTP hợp lệ duy nhất để xác thực (ƯU TIÊN MÃ ĐƯỢC TẠO GẦN NHẤT).
     * </b>
     * <br>
     * <b>Cách hoạt động (Derived Query):</b> Spring Data JPA sẽ tự động "dịch" tên phương thức này thành một câu lệnh SQL
     * với các điều kiện sau:
     * <ul>
     * <li>{@code findFirstBy} "chỉ lấy 1 kết quả đầu tiên" tương đương với {@code LIMIT 1} (chỉ lấy 1 kết quả)</li>
     * <li>{@code Target} tìm bản ghi có cột {@code target} bằng với giá trị của tham số {@code String target} được truyền vào.
     * Tương đương {@code WHERE target = ?} (đúng người dùng)</li>
     * <li>{@code AndPurpose} tìm bản ghi có cột {@code purpose} bằng với giá trị của tham số {@link OtpPurpose} {@code purpose}.
     * Tương đương {@code AND purpose = ?} (đúng mục đích)</li>
     * <li>{@code AndExpiresAtAfter} tìm bản ghi có cột {@code expiresAt} sau thời điểm tham số {@link Instant} {@code now}.
     * Tương đương {@code AND expires_at > ?} (chưa hết hạn)</li>
     * <li>{@code AndUsedIsFalse} tìm bản ghi có cột {@code used} bằng {@code false}.
     * Tương đương {@code AND used = false} (chưa được sử dụng)</li>
     * <li> {@code OrderByCreatedAtDesc} sắp xếp kết quả theo cột {@code createdAt} giảm dần.
     * Tương đương {@code ORDER BY created_at DESC} (ưu tiên mã được tạo ra sau cùng)</li>
     * </ul>
     * <p>
     * <b>Sắp xếp theo {@code createdAt} là tốt nhất vì nó luôn đảm bảo chọn mã mà người dùng vừa nhận được.
     * Hiệu năng của truy vấn này sẽ cao khi có Index trên tổ hợp các cột ({@code target}, {@code purpose},{@code createdAt}).
     * </p>
     *
     * @param target  Mã định danh người nhận (ví dụ: email, số điện thoại).
     * @param purpose Mục đích sử dụng OTP (ví dụ: RESET_PASSWORD, ACCOUNT_VERIFICATION).
     * @param now     Thời điểm hiện tại, dùng để so sánh với **expiresAt**.
     * @return Một {@link Optional} chứa {@link OtpToken} nếu tìm thấy, ngược lại trả về {@link Optional#empty()}.
     */
    Optional<OtpToken> findFirstByTargetAndPurposeAndExpiresAtAfterAndUsedIsFalseOrderByCreatedAtDesc(
            String target,
            OtpPurpose purpose,
            Instant now
    );

    /**
     * <b>Vô hiệu hóa tất cả các mã OTP cũ còn hiệu lực</b>
     * <p>
     * <b>Mục đích (Bảo mật):</b> Khi người dùng yêu cầu một mã OTP mới (ví dụ: nhấn "Gửi lại OTP"), phương thức này
     * sẽ được gọi <b>trước khi</b> tạo mã mới. Nó đảm bảo rằng tất cả các mã OTP cũ đã gửi trước đó (nhưng vẫn còn hạn)
     * sẽ bị vô hiệu hóa ngay lập tức. Điều này ngăn chặn người dùng vô tình hoặc kẻ tấn công cố ý sử dụng một mã OTP cũ.
     * </p>
     * <p>
     * <b>Cách hoạt động (@Query):</b>
     * <ul>
     * <li>{@code @Modifying}: Báo cho Spring biết đây là một câu lệnh thay đổi dữ liệu ({@code UPDATE}/{@code DELETE}).</li>
     * <li>{@code @Transactional}: Đảm bảo toàn bộ thao tác được thực hiện trong một giao dịch an toàn.</li>
     * <li>Câu lệnh {@code UPDATE} sẽ tìm tất cả các mã OTP của người dùng cho mục đích cụ thể mà <b>vẫn còn hiệu lực</b>
     * (chưa hết hạn và chưa sử dụng) và đánh dấu chúng là đã sử dụng ({@code used = true}).</li>
     * </ul>
     * </p>
     *
     * @param target         Mã định danh người nhận (email/SĐT).
     * @param purpose        Mục đích sử dụng OTP (ví dụ: REGISTER_VERIFICATION).
     * @param invalidateTime Thời điểm hiện tại, dùng để xác định các mã còn hiệu lực.
     * @return Số lượng bản ghi (mã OTP) đã được vô hiệu hóa.
     */
    @Modifying
    @Transactional
    @Query("UPDATE OtpToken o SET o.used = true, o.attemptsLeft = 0 " +
            "WHERE o.target = :target AND o.purpose = :purpose AND o.expiresAt > :invalidateTime AND o.used = false")
    int invalidateActiveTokens(
            @Param("target") String target,
            @Param("purpose") OtpPurpose purpose,
            @Param("invalidateTime") Instant invalidateTime
    );

    /**
     * <b>Dọn dẹp các mã OTP đã hết hạn</b>
     * <p>
     * <b>Mục đích (Bảo trì):</b> Theo thời gian, bảng {@code otp_tokens} sẽ chứa rất nhiều bản ghi OTP đã hết hạn
     * và không còn giá trị. Phương thức này dùng để dọn dẹp chúng, giúp giữ cho bảng dữ liệu gọn gàng,
     * tiết kiệm dung lượng và cải thiện hiệu năng cho các tác vụ bảo trì, backup.
     * </p>
     * <p>
     * <b>Cách hoạt động:</b> Đây là một phương thức "derived query {@code deleteByExpiresAtBefore}" sẽ được dịch thành câu lệnh {@code DELETE}.
     * Nó sẽ xóa tất cả các bản ghi có cột {@code expiresAt} nhỏ hơn thời điểm {@code now} được cung cấp.
     * Phương thức này nên được gọi định kỳ bởi một tác vụ nền (ví dụ: {@code @Scheduled} của Spring).
     * </p>
     *
     * @param now Thời điểm hiện tại, dùng để xác định các mã đã hết hạn.
     */
    @Modifying
    @Transactional
    void deleteByExpiresAtBefore(Instant now);
}