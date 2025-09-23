package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateRequestDTO;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;
import com.cuonglm.ecommerce.backend.shop.repository.ShopRepository;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ShopServiceImpl – Cài đặt logic của {@link ShopService}.
 *
 * @author cuonglmptit
 * @since Wednesday, 19 November 2025
 */
@Service
@Transactional
public class ShopServiceImpl implements ShopService {
    private final UserService userService;
    private final ShopRepository shopRepository;

    public ShopServiceImpl(UserService userService, ShopRepository shopRepository) {
        this.userService = userService;
        this.shopRepository = shopRepository;
    }

    @Override
    public ShopCreateResponseDTO createShop(ShopCreateRequestDTO request) {
        // 1. Lấy User đang thực hiện request và xác định owner
        UserInfoDTO requestUser = userService.getCurrentAuthenticatedUserInfo();
        Long finalOwnerId = resolveShopOwnerId(request.getOwnerId(), requestUser);

        // 2. Xác định thông tin chủ Shop (ownerInfo)
        UserInfoDTO ownerInfo = finalOwnerId.equals(requestUser.id())
                // Nếu Chủ Shop là chính người đang request (User tự tạo Shop) -> Tái sử dụng requestUser
                ? requestUser
                // Nếu Chủ Shop là người khác (Admin tạo hộ)
                // Chỉ select khi Admin tạo Shop hộ cho User khác
                : userService.findUserInfoById(finalOwnerId);

        // 3. Kiểm tra có thể tạo shop hay không
        if (shopRepository.findByOwnerId(ownerInfo.id()).isPresent()) {
            throw new ConflictException("User ID " + ownerInfo.id() + " đã sở hữu một cửa hàng. Mỗi người dùng chỉ được sở hữu 1 shop.");
        }

        // 2. Kiểm tra cơ bản User phải là ACTIVE
        if (ownerInfo.status() != UserStatus.ACTIVE) {
            // Ném lỗi và dừng transaction ngay lập tức, không tạo Shop Entity
            throw new ConflictException("Không thể tạo cửa hàng vì tài khoản chủ shop (" + ownerInfo.id() + ") đang ở trạng thái: " + ownerInfo.status());
        }


        ShopStatus finalStatus = runAutoApprovalChecks(request, ownerInfo);

        if (requestUser.isAdmin() && finalStatus == ShopStatus.PENDING) {
            finalStatus = ShopStatus.ACTIVE;
        }

        // 5. Mapping DTO to Entity
        Shop shop = new Shop();
        shop.setName(request.getName());
        shop.setDescription(request.getDescription());
        shop.setStatus(finalStatus);
        // Entity User để gán vào Shop: Dùng finalOwnerId đã xác định
        User ownerRef = new User();
        ownerRef.setId(finalOwnerId);
        shop.setOwner(ownerRef);

        // 6. Lưu Shop
        Shop savedShop = shopRepository.save(shop);

        // 7. Cấp role cho user nếu được duyệt tự động
        if (finalStatus == ShopStatus.ACTIVE) {
            userService.grantRoleToUser(finalOwnerId, UserRole.SELLER);
        }

        // 8. Trả về kết quả
        return mapToShopResponse(savedShop);
    }

    /**
     * Xác định xem owner của shop sẽ là ai
     *
     * @param requestedOwnerId id của owner gửi kèm shop
     * @param requestUser      thông tin của người gửi request
     * @return nếu như là admin gửi request thì sẽ trả về requestedOwnerId,
     * nếu mà người gửi request trùng id với id trong requestedOwnerId thì sẽ trả về requestedOwnerId.
     * Còn nếu như mà user id=A lại gửi request để tạo shop với ownerId=B thì sẽ ném lỗi
     */
    private Long resolveShopOwnerId(Long requestedOwnerId, UserInfoDTO requestUser) {
        // Trường hợp 1: Không có ownerId trong request (User tự tạo)
        if (requestedOwnerId == null) {
            return requestUser.id(); // Owner là chính người đang request
        }

        // Trường hợp 2: Có ownerId và requestUser là Admin/Moderator
        if (requestUser.isAdmin()) {
            return requestedOwnerId;
        }

        // Trường hợp 3: User thường cố gắng tạo Shop cho người khác
        if (!requestedOwnerId.equals(requestUser.id())) {
            throw new PermissionDeniedException("Bạn không có quyền tạo cửa hàng cho người dùng khác.");
        }

        // Trường hợp 4: User tự tạo Shop và chỉ rõ ID của chính mình (requestedOwnerId == requestUser.id())
        return requestUser.id();
    }

    // Helper mapper
    private ShopCreateResponseDTO mapToShopResponse(Shop shop) {
        ShopCreateResponseDTO response = new ShopCreateResponseDTO();
        response.setId(shop.getId());
        response.setName(shop.getName());
        response.setDescription(shop.getDescription());
        // Lưu ý: shop.getOwner() có thể là Lazy Proxy, chỉ lấy ID là an toàn nhất
        response.setOwnerId(shop.getOwner().getId());
        response.setStatus(shop.getStatus());
        return response;
    }

    /**
     * Chạy để kiểm tra và trả về trạng thái ban đầu của shop
     *
     * @param request Thông tin về request tạo shop
     * @param owner   Thông tin chủ shop
     * @return Trạng thái ban đầu của shop
     */
    private ShopStatus runAutoApprovalChecks(ShopCreateRequestDTO request, UserInfoDTO owner) {
        // 1. Kiểm tra xác minh: Phải xác minh Email hoặc SĐT
        if (!owner.isEmailVerified() && !owner.isPhoneVerified()) {
            // Nếu chưa xác minh cả hai, cần Admin duyệt thủ công
            return ShopStatus.PENDING;
        }

        // 2. Kiểm tra dữ liệu: Tên Shop phải hợp lệ
        if (request.getName() == null || request.getName().trim().length() < 6) {
            // Yêu cầu tên Shop dài hơn 6 ký tự
            return ShopStatus.PENDING;
        }

        // 3. Nếu vượt qua các kiểm tra tự động
        return ShopStatus.ACTIVE;
    }


    /**
     * Logic chấp nhận một Shop. Phương thức này yêu cầu quyền ADMIN/MODERATOR.
     *
     * @param shopId ID của Shop cần duyệt
     */
    @Override
    public void approveShop(Long shopId) {
        // 1. Tìm và kiểm tra Shop
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop ID " + shopId + " không tồn tại."));

        if (shop.getStatus() == ShopStatus.ACTIVE) {
            // Đã duyệt rồi thì không cần làm gì
            return;
        }

        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new ConflictException("Shop đang ở trạng thái " + shop.getStatus() + ", không thể duyệt.");
        }

        // 2. Chuyển trạng thái Shop thành APPROVED
        shop.setStatus(ShopStatus.ACTIVE);
        shopRepository.save(shop);

        // 3. CẤP ROLE SELLER cho User sở hữu Shop
        Long ownerId = shop.getOwner().getId();
        userService.grantRoleToUser(ownerId, UserRole.SELLER);
    }
}
