package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateRequestDTO;
import com.cuonglm.ecommerce.backend.shop.dto.external.ShopCreateResponseDTO;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.repository.ShopRepository;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
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
        // 1. Lấy User đang thực hiện request (Admin hoặc User thường)
        UserInfoDTO requestUser = userService.getCurrentAuthenticatedUserInfo();

        // 2. XÁC ĐỊNH OWNER ID CUỐI CÙNG (LOGIC ADMIN/PHÂN QUYỀN)
        Long finalOwnerId = resolveShopOwnerId(request.getOwnerId(), requestUser);

        // 3. VALIDATION CHÍNH XÁC: Kiểm tra xem Owner đích đã có Shop chưa
        if (shopRepository.findByOwnerId(finalOwnerId).isPresent()) {
            // Dùng finalOwnerId trong thông báo lỗi để rõ ràng hơn
            throw new ConflictException("User ID " + finalOwnerId + " đã sở hữu một cửa hàng. Mỗi người dùng chỉ được sở hữu 1 shop.");
        }

        // 4. Mapping DTO to Entity
        Shop shop = new Shop();
        shop.setName(request.getName());
        shop.setDescription(request.getDescription());

        // Entity User để gán vào Shop: Dùng finalOwnerId đã xác định
        User ownerRef = new User();
        ownerRef.setId(finalOwnerId);
        shop.setOwner(ownerRef);

        // 5. Lưu Shop
        Shop savedShop = shopRepository.save(shop);

        // 6. Trả về
        return mapToShopResponse(savedShop);
    }

    private Long resolveShopOwnerId(Long requestedOwnerId, UserInfoDTO requestUser) {
        // Trường hợp 1: Không có ownerId trong request (User tự tạo)
        if (requestedOwnerId == null) {
            return requestUser.id(); // Owner là chính người đang request
        }

        // Trường hợp 2: Có ownerId và requestUser là Admin/Moderator
        // Giả định UserService có method kiểm tra quyền dựa trên DTO
        if (requestUser.isAdmin()) {
            // Admin có thể tạo Shop hộ bất kỳ ai
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
        return response;
    }
}
