package com.cuonglm.ecommerce.backend.shop.service;

import com.cuonglm.ecommerce.backend.core.exception.ConflictException;
import com.cuonglm.ecommerce.backend.core.exception.PermissionDeniedException;
import com.cuonglm.ecommerce.backend.core.exception.ResourceNotFoundException;
import com.cuonglm.ecommerce.backend.core.utils.SecurityUtils;
import com.cuonglm.ecommerce.backend.shop.dto.external.*;
import com.cuonglm.ecommerce.backend.shop.dto.internal.ShopInfoDTO;
import com.cuonglm.ecommerce.backend.shop.projection.ShopInfoView;
import com.cuonglm.ecommerce.backend.shop.entity.Shop;
import com.cuonglm.ecommerce.backend.shop.enums.ShopPermission;
import com.cuonglm.ecommerce.backend.shop.enums.ShopStatus;
import com.cuonglm.ecommerce.backend.shop.repository.ShopRepository;
import com.cuonglm.ecommerce.backend.user.dto.internal.UserInfoDTO;
import com.cuonglm.ecommerce.backend.user.entity.User;
import com.cuonglm.ecommerce.backend.user.enums.UserRole;
import com.cuonglm.ecommerce.backend.user.enums.UserStatus;
import com.cuonglm.ecommerce.backend.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public ShopResponse createShop(CreateShopRequest request) {
        // 1. Xác định User thực hiện và ID chủ sở hữu
        UserInfoDTO requestUser = userService.getCurrentAuthenticatedUserInfo();
        Long finalOwnerId = resolveShopOwnerId(request.ownerId(), requestUser);

        // 2. Kiểm tra thông tin chủ Shop
        UserInfoDTO ownerInfo = finalOwnerId.equals(requestUser.id())
                ? requestUser
                : userService.findUserInfoById(finalOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", finalOwnerId));

        // 3. Mỗi User chỉ được sở hữu tối đa 1 Shop
        if (shopRepository.existsByOwnerId(ownerInfo.id())) {
            throw new ConflictException("User ID " + ownerInfo.id() + " đã sở hữu một cửa hàng. Mỗi người dùng chỉ được tạo 1 shop.");
        }

        // 4. Tài khoản phải ở trạng thái ACTIVE
        if (ownerInfo.status() != UserStatus.ACTIVE) {
            throw new ConflictException("Tài khoản chủ shop đang ở trạng thái " + ownerInfo.status() + ", không thể tạo shop.");
        }

        // 5. Kiểm tra duyệt tự động
        ShopStatus finalStatus = runAutoApprovalChecks(request, ownerInfo);
        if (requestUser.isAdmin() && finalStatus == ShopStatus.PENDING) {
            finalStatus = ShopStatus.ACTIVE;
        }

        // 6. Lưu Entity Shop
        Shop shop = new Shop();
        shop.setName(request.name().trim());
        shop.setDescription(request.description() != null ? request.description().trim() : null);
        shop.setStatus(finalStatus);

        User ownerRef = userService.getUserReference(finalOwnerId);
        shop.setOwner(ownerRef);

        Shop savedShop = shopRepository.save(shop);

        // 7. Cấp Role SELLER cho chủ shop nếu được Active ngay
        if (finalStatus == ShopStatus.ACTIVE) {
            userService.grantRoleToUser(finalOwnerId, UserRole.SELLER);
        }

        // 8. Trả về Response chuẩn qua Static Factory Method (Rule #2)
        return ShopResponse.fromEntity(savedShop);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getCurrentShop() {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        ShopInfoView view = shopRepository.findShopInfoByOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa mở cửa hàng nào trên hệ thống."));
        return ShopResponse.fromView(view);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopResponse getShopById(Long shopId) {
        ShopInfoView view = shopRepository.findShopInfoById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", shopId));
        return ShopResponse.fromView(view);
    }

    @Override
    public ShopResponse updateCurrentShop(UpdateShopRequest request) {
        Long currentUserId = SecurityUtils.getRequiredCurrentUserId();
        Shop shop = shopRepository.findByOwnerId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Bạn chưa có cửa hàng để cập nhật."));

        shop.setName(request.name().trim());
        if (request.description() != null) {
            shop.setDescription(request.description().trim());
        }

        Shop savedShop = shopRepository.save(shop);
        return ShopResponse.fromEntity(savedShop);
    }

    @Override
    public void approveShop(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", shopId));

        if (shop.getStatus() == ShopStatus.ACTIVE) {
            return;
        }

        if (shop.getStatus() != ShopStatus.PENDING) {
            throw new ConflictException("Shop đang ở trạng thái " + shop.getStatus() + ", không thể duyệt.");
        }

        shop.setStatus(ShopStatus.ACTIVE);
        shopRepository.save(shop);

        Long ownerId = shop.getOwner().getId();
        userService.grantRoleToUser(ownerId, UserRole.SELLER);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShopInfoDTO> findShopInfoById(Long shopId) {
        return shopRepository.findShopInfoById(shopId)
                .map(ShopInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShopInfoDTO> findShopInfoByOwnerId(Long ownerId) {
        return shopRepository.findShopInfoByOwnerId(ownerId)
                .map(ShopInfoDTO::fromView);
    }

    @Override
    @Transactional(readOnly = true)
    public Shop getShopReference(Long shopId) {
        return shopRepository.getReferenceById(shopId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long shopId, ShopPermission requiredPermission) {
        UserInfoDTO currentUser = userService.getCurrentAuthenticatedUserInfo();

        if (currentUser.isAdmin()) return true;

        ShopInfoDTO shopInfo = findShopInfoById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", shopId));

        // Nhân viên / Cộng tác viên (LOGIC MỞ RỘNG SAU NÀY):
        // Lấy danh sách Quyền của Nhân viên này trong ShopID từ bảng ShopStaff/ShopRole
        // VD Pseudo code:
        // Set<ShopPermission> staffPermissions = shopStaffRepository.findPermissions(shopId, currentUser.id());
        // return staffPermissions.contains(requiredPermission);

        return shopInfo.ownerId().equals(currentUser.id());
    }

    @Override
    @Transactional(readOnly = true)
    public void validatePermission(Long shopId, ShopPermission requiredPermission) {
        if (!hasPermission(shopId, requiredPermission)) {
            throw new PermissionDeniedException(
                    String.format("Bạn không có quyền [%s] trên Shop ID: %d", requiredPermission, shopId)
            );
        }
    }

    private Long resolveShopOwnerId(Long requestedOwnerId, UserInfoDTO requestUser) {
        if (requestedOwnerId == null) {
            return requestUser.id();
        }
        if (requestUser.isAdmin()) {
            return requestedOwnerId;
        }
        if (!requestedOwnerId.equals(requestUser.id())) {
            throw new PermissionDeniedException("Bạn không có quyền tạo cửa hàng cho người dùng khác.");
        }
        return requestUser.id();
    }

    private ShopStatus runAutoApprovalChecks(CreateShopRequest request, UserInfoDTO owner) {
        if (!owner.isEmailVerified() && !owner.isPhoneVerified()) {
            return ShopStatus.PENDING;
        }
        if (request.name() == null || request.name().trim().length() < 3) {
            return ShopStatus.PENDING;
        }
        return ShopStatus.ACTIVE;
    }
}
