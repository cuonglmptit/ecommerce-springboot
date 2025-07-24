# Ecommerce
- Hướng tiếp cận Backend:  
JPA+Hibernate  
Phân chia package: Package-by-feature (Phân chia backend theo tính năng) chứ không dùng cách Package-by-layer, Có thể dùng cả package by layer phần nhỏ nào đấy

27/05:  
Phân chia: User -> Các feature bình thường  
Seller feature -> có entity shop, các thống kê...  
Admin feature -> quản lý các user...  
Admin và seller không cần entity riêng, chỉ phân tách các logic  
  
28/05:  
Thay đổi ý tưởng  
Hiện tại làm hệ thống nhỏ sau đó có thể mở rộng -> thiết kế theo hướng có thể mở rộng  
- Seller -> chưa cần, Admin có thể làm mọi thứ. Sau này có thể mở rộng thêm seller  
- Shop -> cần dù chỉ có 1 shop duy nhất và chủ shop là Admin
- Product thì có product variant, giá sẽ tùy thuộc vào ProductVariant  
ProductVariant thì sẽ gồm 1 bảng liên kết với AttributeValue tạo ra 1 variant có giá khác  
Attribute thì sẽ liên kết với AttributeGroup cho dễ tìm, liên kết với cả Category để khi lọc thì xuất hiện
nay đến đây thôi, Ý tưởng tiếp tục khi coupoun, sale price....

18/07/2025:
Mô hình xác thực định danh đề xuất
1. Tài khoản có thể được tạo bằng:
OAuth2 (Google, Facebook...) → lấy email đã xác minh

Số điện thoại → OTP xác minh ngay khi đăng ký

2. Tài khoản ban đầu chỉ có một định danh đã xác minh:
Hoặc email (qua OAuth2)

Hoặc sđt (qua OTP)

3. Muốn thêm định danh mới (email/sđt) thì:
Phải xác minh (OTP hoặc link)

Nếu định danh đã gắn với user khác → không cho thêm
(tránh chiếm tài khoản như case Converse)

20/07/2025:
Logic về tạo tài khoản với oauth2: User tạo tài khoản bằng provider nào đeo đầu tiên => lấy email đó gắn vào user local đầu tiên (VD ban đầu là cuonglm1@gmail.com)
Nếu có provider nào đấy cũng lại dùng đăng nhập hoặc đăng ký thì nếu trùng email và xác minh email đó chính chủ => vào đúng tài khoản có email đó
Nếu như sau này mà facebook đổi email chính (cuonglm1@gmail.com => cuonglm2@gmail.com) => thì vẫn còn provideruserid của provider đó => vẫn đăng nhập vào user local cũ đấy và email thì vẫn là email được tạo đầu tiên (cuonglm1@gmail.com) dù cho email chính hiện tại của provider facebook này đã được đổi thành cuonglm2@gmail.com
20/07/2025:
Thinking?: Thấy nửa vời quá => làm luôn toàn hệ thống luôn cho đỡ mệt?, kệ tính sau
1. catalog
Chứa các khái niệm liên quan đến sản phẩm (product), phân loại (category), thuộc tính (attribute)...
🚧 product
Product: sản phẩm chung, không có số lượng
ProductVariant: mỗi variant có tổ hợp ProductAttributeValue, có quantity, sku, price, v.v.
ProductOption: (có thể đồng nghĩa với Attribute nếu dùng như Shopee)
ProductAttributeValue: cặp (attribute + option) dùng cho 1 variant
ProductImage, ProductMedia...
🚧 attribute
Attribute: thuộc tính (VD: Màu sắc, Kích thước)
AttributeOption: giá trị cho thuộc tính (Đỏ, Đen, Trắng,...)
AttributeScope: GLOBAL / SHOP
AttributeGroup: tập các attribute, dùng cho 1 loại sản phẩm
AttributeGroupAttribute: mapping table giữa group và attribute
🚧 category
Category: ngành hàng phân cấp
Có thể global, shop không được tạo
Sử dụng để điều hướng và lọc
🚧 collection
Collection: shop tự tạo, gán list sản phẩm, phẳng (không có collection con)
Giao diện landing page giống Shopee: “Hàng Mới Về”, “Sale Cuối Tuần”
2. shop
Shop: thực thể đại diện 1 cửa hàng
User có 1 Shop (hiện tại 1:1, sau này có thể mở rộng)
Shop có thể tạo Collection, Attribute (scope=SHOP), brand local...
3. brand
Brand: thực thể riêng
Có scope là GLOBAL hoặc SHOP
Phải gửi yêu cầu để admin duyệt thành global
Gắn vào Product
4. admin
Tùy lựa chọn:
Cách 1: Có 1 feature riêng admin (tốt khi muốn gom UI/API riêng)
Cách 2: Mỗi feature đều có AdminController riêng trong feature đó (tốt khi dùng cùng logic như user nhưng khác quyền)

21/07/2025:
Category thì sẽ hướng đến dùng Materizlized Path (nhưng là mở rộng của Parent-Child (Adjacency)) để lưu nested