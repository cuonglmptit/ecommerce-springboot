package com.cuonglm.ecommerce.backend.user.entity;

import com.cuonglm.ecommerce.backend.core.entity.AuditMetadata;
import com.cuonglm.ecommerce.backend.location.entity.snapshot.AddressSnapshot;
import com.cuonglm.ecommerce.backend.user.enums.UserAddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * UserAddress – Lớp lưu thông tin địa chỉ của user.
 *
 * <p>
 * Lưu thông tin các địa chỉ của User, địa chỉ mặc định,...
 * </p>
 *
 * @author cuonglmptit
 * @since Friday, 25 July 2025
 */
@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address", columnDefinition = "jsonb", nullable = false)
    private AddressSnapshot address;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    @NotNull
    @Size(min = 10, max = 15)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserAddressType type = UserAddressType.HOME;

    @Column(length = 255)
    private String note;

    @Embedded
    private AuditMetadata audit = new AuditMetadata();

    //<editor-fold desc="Getters/Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public UserAddressType getType() {
        return type;
    }

    public void setType(UserAddressType type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AddressSnapshot getAddress() {
        return address;
    }

    public void setAddress(AddressSnapshot address) {
        this.address = address;
    }

    public AuditMetadata getAudit() {
        return audit;
    }
    //</editor-fold>
}
