# 🛒 Ecommerce

## 🔹 Hướng tiếp cận Backend  
- **Tech:** JPA + Hibernate  
- **Tổ chức code:** `Package-by-feature` (chia theo tính năng)  
  - Không dùng `Package-by-layer` truyền thống (chỉ áp dụng layer cho những phần rất nhỏ nếu cần)  

---

## 📅 Timeline

### **27/05**
- Phân chia ban đầu:  
  - `User`: Các feature bình thường  
  - `Seller`: có entity Shop, thống kê,...  
  - `Admin`: quản lý user,...  
- Admin & Seller **không cần entity riêng**, chỉ phân tách logic  
- Product có `ProductVariant`, giá theo từng variant  
- Variant liên kết `AttributeValue` để định nghĩa tổ hợp  
- Attribute liên kết với `AttributeGroup` và `Category` để phục vụ filter  

---

### **28/05**
- Chuyển hướng: **thiết kế nhỏ gọn nhưng mở rộng dễ dàng**  
- **Seller**: chưa cần, Admin làm tất cả → sau có thể thêm Seller  
- **Shop**: bắt buộc có dù hiện tại chỉ có 1 Shop (chủ Shop là Admin)  
- **Product**:  
  - Có `ProductVariant`, giá phụ thuộc Variant  
  - Variant gồm 1 bảng liên kết với `AttributeValue` tạo ra biến thể có giá khác  
  - `Attribute` liên kết với `Category` để filter khi tìm kiếm  
- Ý tưởng sẽ mở rộng cho: coupon, sale price, ...  

---

### **18/07/2025 – Đề xuất mô hình xác thực định danh**
1. **Tài khoản tạo bằng:**  
   - OAuth2 (Google, Facebook, ...) → lấy email đã xác minh  
   - SĐT → xác minh OTP ngay khi đăng ký  

2. **Tài khoản ban đầu có 1 định danh đã xác minh:**  
   - Email (qua OAuth2) **hoặc**  
   - SĐT (qua OTP)  

3. **Thêm định danh mới:**  
   - Phải xác minh OTP hoặc link  
   - Nếu định danh đã gắn với user khác → không cho thêm (tránh chiếm tài khoản)  

---

### **20/07/2025 – Logic OAuth2**
- User tạo tài khoản bằng provider nào đầu tiên → lấy email gắn với user local (VD: `cuonglm1@gmail.com`)  
- Nếu đăng nhập bằng provider khác có cùng email & đã xác minh chính chủ → vào cùng tài khoản  
- Nếu provider đổi email chính (`cuonglm1@gmail.com` → `cuonglm2@gmail.com`) → vẫn login vào user local cũ nhờ `providerUserId`, email trong local **không thay đổi**  
20/07/2025:  
Thinking?: Thấy nửa vời quá => làm luôn toàn hệ thống luôn cho đỡ mệt?, kệ tính sau  
---

### **20/07/2025 – Catalog Design**
#### 1️⃣ 🚧**Product**  
- `Product`: sản phẩm chung, không có số lượng  
- `ProductVariant`: tổ hợp của `ProductAttributeValue`, có quantity, sku, price  
- `ProductOption`: có thể đồng nghĩa `Attribute` (như Shopee)  
- `ProductAttributeValue`: cặp (attribute + option) của một variant  
- `ProductImage`, `ProductMedia`  

#### 2️⃣ **Attribute**  
- `Attribute`: thuộc tính (VD: Màu sắc, Kích thước)  
- `AttributeOption`: giá trị cho thuộc tính (Đỏ, Đen, Trắng,...)  
- `AttributeScope`: GLOBAL / SHOP  
- `AttributeGroup`: tập các attribute  
- `AttributeGroupAttribute`: bảng mapping  

#### 3️⃣ **Category**  
- Category phân cấp, chỉ GLOBAL  
- Dùng để điều hướng & lọc  

#### 4️⃣ **Collection**  
- Shop tự tạo, chứa danh sách sản phẩm (phẳng, không có collection con)  
- Landing page: “Hàng Mới Về”, “Sale Cuối Tuần”  

#### 5️⃣ **Shop**  
- Thực thể đại diện 1 cửa hàng  
- User có 1 Shop (hiện tại 1:1, sau mở rộng)  
- Shop có thể tạo Collection, Attribute (scope = SHOP), local brand  

#### 6️⃣🚧 **Brand**  
- GLOBAL hoặc SHOP  
- Gắn vào Product  
- Có cơ chế admin duyệt brand từ shop thành global  

#### 7️⃣🚧 **Admin**  
- Cách 1: Feature riêng `admin` (gom UI/API)  
- Cách 2: Mỗi feature có `AdminController` riêng (cùng logic user, khác quyền)  

---

### **21/07/2025**
- Category dùng **Materialized Path** (mở rộng của Parent-Child/Adjacency) để lưu nested  

---

### **25/07/2025 – TODO 🚧Shop**
- Xây dựng `Shop`, `Collection`, `Brand`  

---

## 📦 Address & Location Design

### **Location**
- Chứa: `province`, `district`, `ward`, `addressLine`  
- Dùng cho: `UserAddress`, `ShopAddress`, `Warehouse`...  
- Luôn tạo mới khi user thêm địa chỉ (**snapshot**)  
- Có thể mở rộng: `latitude`, `longitude`, `placeId`, `formattedAddress`  
- Tích hợp Google Maps hoặc hệ thống bản đồ bên ngoài  

### **UserAddress**
- Chứa: `user`, `location`, `fullName`, `phone`, `note`, `type`, `isDefault`  
- Cá nhân hóa theo user, liên kết với `Location`  

### **AddressType**
- Enum định nghĩa riêng cho từng feature (user/shop)  
- Không để trong `Location`  

### **Ưu điểm**
- Độc lập data địa lý & metadata  
- Dễ tái sử dụng, mở rộng cho nhiều ngữ cảnh (`shop`, `return`, `pickup`)  
- Hợp lý với modular monolith, sẵn sàng tách microservice  

---

## **29/07/2025 – Attribute System**

### 1️⃣ **AttributeScope (Enum)**
- `GLOBAL`:  
  - Dùng toàn hệ thống, hiển thị khi tạo sản phẩm, dùng để search filter ở marketplace  
- `SHOP`:  
  - Thuộc về 1 shop, chỉ filter trong shop  

### 2️⃣ **Attribute (Entity)**  
- Đại diện cho thuộc tính sản phẩm: *Màu sắc, Kích thước, Chất liệu*  
- Trường chính:  
  - `id` UUID.  
  - `name` tên hiển thị (VD: "Màu sắc").  
  - `code` tên kỹ thuật dùng nội bộ (VD: "COLOR").  
  - `scope` (`AttributeScope`) → GLOBAL hoặc SHOP.  
  - `shop` shop sở hữu (nếu scope = SHOP, GLOBAL thì null).  
  - `status` (ACTIVE/INACTIVE – lấy từ `BasicStatus`)  

➡ Ý nghĩa: Một Attribute chứa nhiều lựa chọn (option)  

### 3️⃣ **AttributeOption (Entity)**  
- Giá trị cụ thể của một Attribute: *Đỏ, Xanh, Size M*  
- Trường chính:  
  - `id` (UUID)  
  - `attribute` (ManyToOne liên kết về Attribute cha)  
  - `value` giá trị cụ thể (VD: "Đỏ", "Size M").  
  - `scope` GLOBAL hoặc SHOP (giống Attribute).  
  - `shop` (nullable nếu GLOBAL), shop sở hữu (nếu SHOP).  

➡ Một Attribute có thể có nhiều AttributeOption  

### 4️⃣ **Quan hệ tổng quan**
- Attribute (1) ── (n) AttributeOption  
- `scope` quyết định phạm vi sử dụng (GLOBAL/SHOP)  
- Shop chỉ quản lý các Attribute/Option GLOBAL hoặc của shop  