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

30/07/2025:  
TODO: User feature

---

## **08/08/2025** - Ý tưởng triển khai Auth feature
- Thêm package auth để phát triển tính năng xác thực (authentication)
- Xử lý đăng ký (register), đăng nhập (login) và quản lý token
- Nhận DTO request từ client: AuthRegisterRequestDto, AuthLoginRequestDto
- Gọi User service để tạo hoặc cập nhật thông tin người dùng
- Trả về DTO response như AuthRegisterResponseDto, AuthLoginResponseDto
- Tách biệt rõ ràng giữa DTO cho client và DTO nội bộ giữa các feature
- Giao tiếp giữa Auth và User thông qua các service sử dụng hai loại DTO nội bộ:
  - Internal DTO: dùng trong nội bộ mỗi feature để trao đổi giữa các lớp trong cùng feature.
  - Integration DTO: dùng cho việc giao tiếp và trao đổi dữ liệu giữa các feature/module khác nhau trong hệ thống.
- Giúp quản lý và mở rộng tính năng authentication dễ dàng, bảo trì tốt hơn

16/10/2025: Cài đặt chức năng đăng nhập/đăng ký User
Luồng đăng nhập/đăng ký: End-user/client gọi đến Auth controller để login/register, controller gọi service auth 
=> authservice gọi user service của Feature User để check user nếu chưa tồn tại hay đã tồn tại thfi tạo và issue acess token và refresh token 
19/10/2025(done) to do: (nghi vấn) giờ đang bị lỗi là nếu tôi khởi tạo data thì nó sẽ bị lỗi 
Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): 
vì chưa có data, nhưng nếu tôi để null thì spring lại không cho, giờ tính sao để khởi tạo data đây?
22/10/25 (done): xong tạm ổn phần đăng nhập và convert jwt
-> tiếp tục làm phần đăng ký người dùng, đang nhập qua google và tạo các api user

23/10/25: đã xong converter, jwtdecoder cho resource server
(done)
đang có vấn đề nếu client đăng ký mà không có secret đang không lấy đc token qua pkce
hóa ra phải chọn Client Authentication là "Send client credential in body" trong Postman để cho nó không tự gửi Authorization Header kèm theo,
thay vì "Send as basic auth header" thì mới đc và cấu hình là ClientAuthenticationMethod.NONE hoặc .POST) 

24/10/25:
todo: làm login bằng google
ý tưởng (đây đã là cách cũ rồi, không dùng FedCM):
SPA không tương tác trực tiếp với Google, mà chỉ tương tác với Auth Server của bạn.
1. Nút Đăng nhập: Khi người dùng click "Đăng nhập bằng Google", SPA chỉ cần chuyển hướng trình duyệt đến endpoint OAuth 2.0 Login của Spring Security trên Auth Server của bạn.
URL Chuyển hướng: http://localhost:8080/oauth2/authorization/google
2. Spring Xử lý:
Spring Security (AS) sẽ chuyển hướng trình duyệt đến trang đăng nhập của Google.
Người dùng đăng nhập/đồng ý.
Google chuyển hướng trở lại AS (/login/oauth2/code/google).
3. Tạo JWT cho Client SPA: Sau khi xác thực thành công qua Google, AS của bạn cần tạo một JWT mới cho SPA của bạn.
Logic Tùy chỉnh: Bạn cần một logic đặc biệt để lấy thông tin Authentication từ Google và tạo ra JWT của riêng bạn. 
4. AS sẽ chuyển hướng người dùng trở lại một URI trong SPA (ví dụ: http://localhost:3000/callback) kèm theo JWT trong tham số truy vấn hoặc body.
SPA Lưu Token: SPA nhận JWT và lưu vào bộ nhớ (hoặc Local Storage/Cookie).

-----------
cách mới ux mượt hơn mà không phải popup gì đó là dùng FedCM:
# 1. CÁCH 1: CHUYỂN HƯỚNG TOÀN MÀN HÌNH (REDIRECT - TRUYỀN THỐNG)
# Phương pháp này dựa trên HTTP Redirect và được Spring Security TỰ ĐỘNG xử lý.

1.  SPA: User click link đăng nhập.
2.  BROWSER: Redirect -> AS -> Google Auth URL.
3.  AS: Spring Security tạo State, lưu vào Session.
4.  GOOGLE: User đăng nhập -> Redirect về AS Callback (có Code và State trong URL).
5.  AS: Spring Security TỰ ĐỘNG nhận Code và đổi Code lấy Token Google (Server-to-Server).
6.  AS: AS tạo user local và tạo JWT nội bộ/Refresh Token.
7.  AS: AS -> SPA Callback URL (Redirect 302) -> Gửi JWT/RT (RT trong HttpOnly Cookie).
8.  SPA: Nhận JWT/RT, chuyển đổi giao diện.

# 2. CÁCH 2: UX MƯỢT MÀ/AJAX (FEDCM/GIS - HIỆN ĐẠI)
# Phương pháp này dựa trên JavaScript Callback và AJAX/Fetch, yêu cầu Custom Controller.

1.  SPA: User click nút -> JavaScript của Google (FedCM/GIS) kích hoạt hộp thoại nhỏ (Không Redirect).
2.  GOOGLE: User đăng nhập -> Trả Code/ID Token cho JavaScript Callback của SPA (trong bộ nhớ).
3.  SPA: SPA thực hiện AJAX/Fetch (POST) -> AS Custom Endpoint (/api/auth/google/callback) -> Gửi Code/ID Token trong JSON Body.
4.  AS: Custom Controller NHẬN Code/ID Token từ JSON.
5.  AS: AS THỦ CÔNG gọi Google (WebClient) -> Đổi Code lấy Access Token/ID Token.
6.  AS: AS tạo user local và tạo JWT nội bộ/Refresh Token.
7.  AS: AS -> SPA (Response 200 OK) -> Trả JWT trong JSON Body và RT trong HttpOnly Cookie.
8.  SPA: Nhận JWT/RT, lưu JWT In-Memory, chuyển đổi giao diện.
-> tiếp tục làm api user
25/10/25:
cấu hình được application.yml, mai sẽ hoàn thiện đăng nhập bằng google và tạo user local
todo: CustomOAuth2UserService extends DefaultOAuth2UserService
CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> 
 để làm quá trình tạo userlocal
 đang phân vân 2 cái trên khác nhau gì...
(done), ko phân vân nữa mà dùng delegate pattern

26/10/25: đăng nhập đc bằng google nhưng đang bị lỗi không chạy service CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> 
 được cấu hình để tạo user local, có thể sẽ phải thử <OidcUserRequest, OidcUser> oidcUserService thay vì OAuth2UserService<OAuth2UserRequest, OAuth2User> 
 có thể do "In my case, in application.properties changing spring.security.oauth2.client.registration.google.scope=openid, profile, email 
 to spring.security.oauth2.client.registration.google.scope=profile, email solved the problem for me." 
 vì đang yêu cầu oidc nên sẽ bị invoke cái oidc thay vì là oauth2
Hóa ra là đúng vì trả về có oidc nên bị chạy vào OidcUserRequest => khả năng cao là cần cấu hình cả 2 vì google thì là chuẩn IdP openid, 
còn github hay gì đó khác là ko chuẩn nên sẽ invoke OAuth2UserRequest (=> 27/10: Đúng như suy đoán)
=> cài đặt 2 service luôn(done) và áp dụng luôn cho cả 2 thì 2 trường hợp sẽ ok, khả năng cao là vậy
27/10/25: cài đc 2 service, nhưng vẫn chưa đọc được email khi đăng nhập bằng github oauth, khả năng cao phải tự fetch hoặc xem lại lỗi
đã cài được JwtTokenCustomizer để xử lý đối tượng Authentication sau khi đăng nhập bằng OAuth hoặc FormLogin => vẫn cần sửa thêm cho nó chuẩn logic để trả về token có roles, các thông tin khác
* idea: biết thêm 1 ý tưởng mới là lưu access token thành 2 phần, signature -> httpcookie, header.payload thì nhiều thông tin -> localstorage cho dễ lấy ra, 
còn có thể kết hợp mã hóa 1 chút cho hacker khó khăn tí hẹ hẹ hẹ
=> tiếp tục sẽ tạo userlocal và hiện tại
29/10/25: đã lấy đc email
giờ còn logic cho đăng nhập theo providerid
ý tưởng cơ bản:
1. lấy được idp, email, username 
2=> tìm xem có local user với các thông tin đó chưa? 
2.1 -> nếu có rồi thì lấy ra luôn và thêm cách thuộc tính role hay gì đó để lớp sau là JwtTokenCustomizer hứng OAuth2User
2.2 -> nếu chưa có thì tạo 1 user local và lưu, logic thì sẽ là tạo userlocal dựa trên tên đc cung cấp, email đã verify, và liên kết idp với user local
sau đó lưu vào database -> và cũng modify OAuth2User để JwtTokenCustomizer hứng
30/10/25
tìm hiểu thêm thì đã chưa làm xong, nhưng hiểu thêm là nên dùng UserSecurityDTO thêm các thông tin khác cho UserDetailsService hay OAuth2UserService dùng để thêm thông tin vào 
cho việc issue token có thêm các claim để cho SPA đọc, -> tiếp tục làm viẹc hôm trước
31/10/2025:
khả năng cao cần tạo 1 lớp cài đặt OAuth2User với OidcUser để JwtTokenCustomizer hứng và tạo token
(done) => đã tạo BasePrincipal và đồng thời để tránh lặp code thì cũng có lớp AbstractUserOAuth2Info implements BasePrincipal,
và những lớp [(CustomOAuth2Principal implements OAuth2User), (CustomOidcPrincipal implements OidcUser), (LocalUserPrincipal implements UserDetails)] 
sẽ kế thừa AbstractUserOAuth2Info để JwtTokenCustomizer hứng và tạo token từ BasePrincipal.
Các lớp thông tin mới cho UserService nhận vào: [GithubUserOAuth2Info GoogleUserOauth2Info] implements UserOauth2Info
- Đã đổi tên UserSecurityDTO -> UserSecurityAndProfileDTO
***Login flow hiện tại và nhận token như sau:
+ Khi đăng nhập qua FormLogin hay OAuth2/Oidc thì đều sẽ sử dụng BasePrincipal là đối tượng trả về cho Spring Security hiểu
+ Lớp BasePrincipal này có các phương thức để JwtTokenCustomizer lấy ra và issue token
3 luồng đều dùng BasePrincipal và trả về một implements của BasePrincipal:
1. FormLogin: người dùng nhập username+password -> qua CustomUserDetailsService -> check và đăng nhập như thường, trả về UserSecurityAndProfileDTO và cho vào LocalUserPrincipal cũng là BasePrincipal
2. OAuth2 login: người dùng đăng nhập qua GitHub -> Vào luồng CustomOAuth2UserService -> nhận về thông tin, sử dụng lớp GithubUserOAuth2Info để truyền vào UserService 
và tìm hoặc tạo người dùng nếu chưa có qua userService.findOrCreateUserByOAuth2() -> trả về UserSecurityAndProfileDTO và cho vào CustomOAuth2Principal (cũng là BasePrincipal) để trả về cho Spring Security
3. Oidc(OIDC) Login: Y như OAuth2 login logic nhưng mà dùng GoogleUserOauth2Info và trả về CustomOidcPrincipal
02/11/2025:
Đã xong luồng đăng nhập bằng Google/GitHub hơi khó nhằn, -> tiếp tục làm CRUD user, sau đó làm Product
todo: làm đăng ký bằng form đăng ký -> và phải verify email
03/11/2025:
chọn sign up user bằng cách gửi OTP qua email
05/11/2025: đã xong gửi mail otp lúc đăng ký, tuy nhiên chưa xong logic đăng ký người dùng nào, như nào đó... cần làm thêm phần verify otp token nữa
06/11/2025: đã xong verify, resend, nhưng còn vài logic chưa rõ tỏ ở lúc gửi lại otp, đang ko biết là nên để constraint hay ko và còn phương thức tìm token nữa
2 cái này khác nhau, cần xem lại và có thể thêm createdAt vào OtpToken nữa
findFirstByTargetAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc và
findFirstByTargetAndPurposeAndExpiresAtAfterAndUsedIsFalseOrderByExpiresAtDesc
+ nhớ thêm jpa auditing nữa, có thể tự implement class để biết ai là người moddify
07/11/2025:
- xong audit User, 2 phương thức đã rõ và cũng đã đánh được index để truy vấn cho nhanh (phương thức order by created đúng hơn) và index thì nếu như index single column thì sẽ phục vụ truy vấn nhanh cột đó
, còn nếu như composite index thì là nhiều cột, nếu có A, B C thì sẽ phục vụ truy vấn nhanh được cho 3 trường hợp: where A=?, where A=? and B=?, where A=? AND B=? and C=?
- coi như tạm xong otp
- logic resend otp vẫn còn các trường hợp khác như login, reset password...
+ Idea/Logic mới cho việc lấy access token cho tài khoản mà chưa xác minh: : Chặn hoàn toàn (Không cấp Token) - nhưng kết hợp với việc trả về một thông báo lỗi cụ thể để cải thiện UX.
Chặn ở Auth Service: Khi đăng nhập, nếu mật khẩu đúng nhưng trạng thái là PENDING_VERIFICATION
Ném ngoại lệ: Sử dụng một ngoại lệ tùy chỉnh như AccountNotActivatedException (hoặc DisabledException của Spring Security).
Client xử lý: Backend sẽ trả về mã HTTP 403 Forbidden kèm theo một mã lỗi (Error Code) trong JSON Body (ví dụ: {"errorCode": "USER_NOT_ACTIVATED", "message": "Tài khoản cần kích hoạt qua email."}).
SPA hiển thị: Client nhận mã lỗi, hiển thị thông báo và cung cấp nút "Gửi lại Email Kích hoạt".

11/11/2025:
Logic đăng nhập/liên kết đc hoàn thiện:
Nguyên tắc gốc
I. Mỗi user phải có ít nhất một định danh đã xác minh (email hoặc phone) để được coi là chủ sở hữu thật.
II. Liên kết OAuth2 chỉ dựa vào providerId (provider + providerUserId), không dựa vào email.
III. Chỉ được ghi đè email nếu email hiện tại chưa verified dù là login hoặc liên kết.
IV. Không bao giờ “chiếm” email đã verified ở user khác khi thực hiện login hoặc liên kết.

⚙️ Tóm tắt từng trường hợp
Case	Tình huống	Hành động hệ thống	Kết quả cuối cùng
1.1	user1 (email X verified hoặc phone verified) liên kết với OAuth2 (email Y verified)	Liên kết bằng providerId, không thay đổi email	Đăng nhập được qua OAuth2, email X giữ nguyên
1.2	user1 (email X chưa verified) hoặc null, phone verified, OAuth2 (email Y verified)	Ghi đè email user1 = Y, verified = true	User1 giữ lại, cập nhật email verified
1.3	user1 (email X verified) đã liên kết với OAuth2 (email Y verified), sau đó unlink	Xóa UserOAuth2, giữ nguyên email X	Sau này đăng nhập OAuth2 Y → tạo user2 mới
1.4	user1 (email X chưa verified, phone verified) + OAuth2 (email X verified)	Đánh dấu email X verified = true	User1 xác minh được email X nhờ OAuth2
1.5	user1 (email X chưa verified, phone chưa verified) + OAuth2 (email X verified)	Xóa email user1 (vì chưa active), tạo user2 từ OAuth2	Email X thuộc về user2 (verified thật)
1.6.1	user1 (email X verified, chưa liên kết) + OAuth2 (email X verified)	Tự động liên kết providerId với user1	Đăng nhập OAuth2 X → dùng luôn user1
1.6.2	OAuth2 (email X verified) đã liên kết với user2 rồi	Bỏ qua, luôn ưu tiên liên kết hiện có	OAuth2 login → map về user2

- Luồng cho việc đăng ký: đăng ký sẽ cần Verify-then-Create
1. Khi người dùng nhập email đăng ký → server tạo OTP (purpose = REGISTER, email) và gửi otp về email.
2. Khi người dùng nhập OTP để xác thực → client gửi lại OTP + thông tin đăng ký (email, username, password...).
3. Server kiểm tra OTP hợp lệ → nếu đúng và email chưa có trong DB thì tạo user mới, ngược lại báo lỗi.
4. Chỉ giữ OTP mới nhất cho mỗi email, và không cần bảng trung gian pending.
nên nhớ lưu otp trong OtpToken là hash để tránh lộ otp -> otpHash, còn gửi cho client thì vẫn là plain otp
-> Điều này vẫn an toàn nếu có kiểm soát attempts, expiry, hash, và unique constraint trên email.
12/11/2025: ĐÃ XONG ĐĂNG NHẬP ĐĂNG KÝ (LOCAL VÀ 3RD PARTY)
-> còn có tính năng liên kết tính sau
giờ tập trung làm Product