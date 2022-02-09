# IoT_SmartAgriculture

ESP8266

## Cài đặt

### 1. Khởi tạo project

- clone code từ github và mở bằng Arduino IDE

### 2. Cài đặt Add-on ESP8266 Board trên Arduino IDE

- Thêm <http://arduino.esp8266.com/stable/package_esp8266com_index.json>
vào trong File -> Preferences -> Additional Boards Manager URLs
- Chọn Tools -> Board -> Boards Manager... -> Tìm kiếm "esp8266" và Install

### 3. Thêm các thư viện cần thiết

- Chọn Sketch -> Include Library -> Manager Libraries...
- các thư viện: PubSubClient, NTPClient, Arduino_JSON

## Nạp code và chạy

- Cắm thiết bị ESP8266 vào cổng USB và xem Port trong Device Manager
- Chỉnh các thông số trong Tools
- Chọn Upload
