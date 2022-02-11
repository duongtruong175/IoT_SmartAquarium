# IoT_SmartAquarium

Hệ thống IoT giúp quản lý, chăm sóc và điều khiển bể cá cảnh từ xa

## Cài đặt

1. Cài đặt mysql và tạo cơ sở dữ liệu (thư mục database)
2. Tạo tài khoản IBM Cloud, service IBM Watson IoT Platform. Thao tác tạo các thông số kết nối với device, app
3. Lắp đặt thiết bị ESP và nạp code (thư mục device). Chú ý: sửa các thông số kết nối IBM phù hợp
4. Cài đặt node-red và chỉnh sửa các kết nối phù hợp (thư mục node-red app)
5. Tải code và biên dịch ứng dụng android (thư mục android app). Chú ý: sửa các thông số kết nối node-red, IBM phù hợp
6. Chạy ứng dụng nodejs để thao tác admin frontend (thư mục nodejs)
