# Quản lý kho hàng

Project Android Java và XML cho bài thực hành SQLite. Dữ liệu được lưu cục bộ trong `warehouse.db`, phiên bản 1.

## Mở và chạy

1. Giải nén tệp ZIP. Trong Android Studio, chọn **Open** và mở thư mục `QuanLyKhoHang` có tệp `settings.gradle`.
2. Dùng Gradle JDK đi kèm Android Studio. Nếu IDE yêu cầu, cài Android SDK Platform 37 và đợi Gradle Sync.
3. Chọn máy ảo hoặc điện thoại Android 6.0 trở lên, sau đó bấm **Run**. Đăng nhập bằng **admin** / **123456**.
4. Có thể biên dịch bằng lệnh `gradlew.bat assembleDebug` trên Windows nếu JDK và Android SDK đã được cấu hình.

## Luồng hoạt động

- `LoginActivity` gọi `DatabaseHelper.checkLogin()` để truy vấn bảng `users`. Đăng nhập thành công sẽ mở `MainActivity` và đóng màn hình đăng nhập.
- `MainActivity` lấy danh sách và tính thống kê trong `onResume()`. Khi chọn một dòng, màn hình truyền mã sản phẩm qua Intent.
- `AddProductActivity` kiểm tra tên, giá và số lượng. Sau khi ghi SQLite thành công, màn hình gọi `finish()` để trở về danh sách.
- `ProductDetailActivity` đọc lại sản phẩm theo mã. Các thao tác nhập, bán và xóa ghi trực tiếp vào SQLite. Nút xóa có hộp thoại xác nhận.
- `DatabaseHelper` tạo và chèn dữ liệu mẫu vào hai bảng `users` và `products` ở lần tạo cơ sở dữ liệu đầu tiên. `Product` giữ dữ liệu của một dòng sản phẩm.

## Kịch bản kiểm tra

| Thao tác | Kết quả mong đợi |
| --- | --- |
| Bỏ trống hoặc nhập sai thông tin đăng nhập | Có thông báo lỗi, vẫn ở màn hình đăng nhập |
| Đăng nhập đúng trên lần cài mới | 3 sản phẩm, tổng 5.350.000 ₫, 1 sản phẩm sắp hết hàng |
| Nhập giá 0 hoặc bỏ trống số lượng khi thêm | Hiện lỗi ở ô tương ứng, không ghi sản phẩm |
| Thêm sản phẩm giá 10.000 ₫, số lượng 0 | 4 sản phẩm, tổng 5.350.000 ₫, 2 sản phẩm sắp hết hàng |
| Bán sản phẩm vừa thêm | Hiện “Hết hàng”, số lượng vẫn bằng 0 |
| Nhập thêm 1 rồi quay lại | Tổng giá trị kho tăng thành 5.360.000 ₫ |
| Nhập thêm 2 cho Bàn phím cơ từ số lượng 3 | Số lượng thành 5, cảnh báo của Bàn phím cơ biến mất |
| Đóng hẳn rồi mở lại và đăng nhập | Các thay đổi trong kho vẫn còn |
| Xóa sản phẩm vừa thêm rồi đóng và mở lại | Sản phẩm đã xóa vẫn không xuất hiện |
