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