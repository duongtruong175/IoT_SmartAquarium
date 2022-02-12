/*
   Khai báo thư viện
*/
#include <WiFi.h>
#include <PubSubClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <Arduino_JSON.h>

/*
   Tùy chỉnh các thông số kết nối mạng
*/
// thông số kết nối wifi
const char* ssid = "TANG 2";
const char* password = "1234567890";
// thông số kết nối Watson Iot Platform
#define ORG "8w374s"
#define DEVICE_TYPE "ESP32"
#define DEVICE_ID "D8B0ACE6E2E0"
#define TOKEN "h2X!sY?3dJ(z2LYMAh"
char server[] = ORG ".messaging.internetofthings.ibmcloud.com";
char authMethod[] = "use-token-auth";
char token[] = TOKEN;
char clientId[] = "d:" ORG ":" DEVICE_TYPE ":" DEVICE_ID;
char pubTopic[] = "iot-2/evt/readSensors/fmt/json"; // topic gui du lieu sensor
char subTopic[] = "iot-2/cmd/controlActuators/fmt/json"; // topic nhan du lieu dieu khien
char pubTopic1[] = "iot-2/evt/readStatusActuators/fmt/json"; // topic gui du lieu trang thai thiet bi
char subTopic1[] = "iot-2/cmd/statusActuators/fmt/json"; // topic nhan du lieu trang thai
// định nghĩa protype hàm
void callback(char*, byte*, unsigned int);
// biến kết nối MQTT
WiFiClient wifiClient;
PubSubClient client(server, 1883, callback, wifiClient);

/*
   Định nghĩa các chân kết nối thiết bị thực thi
*/
// chân điều khiển relay, bật tắt máy bơm
const int RELAY_PIN = 15; //GPIO15 - D8
// chân đèn led
const int redPin = 5; // GPIO5 - D1
const int greenPin = 0; // GPIO0 - D3
const int bluePin = 2; // GPIO2 - D4
// chân đèn led thay máy sưởi
const int LED = 16; // GPIO16 - D0

/*
   Đo nhiệt độ nước với cảm biến nhiệt độ DS18B20:
   + giao tiếp oneWire
   + dùng thư viện OneWire.h và DallasTemperature.h để tính toán
*/
// chân cảm biến nhiệt độ
const int oneWireBus = 4; // GPIO4 - D2
// thiết lập lớp oneWire
OneWire oneWire(oneWireBus);
// chuyển tham chiếu oneWire sang Dallas Temperature sensor
DallasTemperature sensors(&oneWire);

/*
   Đo mức nước của bể với cảm biến siêu âm HC-SR04:
   + cảm biến truyền ra sóng và nhận lại sóng đập lại -> thời gian truyền -> tính được khoảng cách từ mặt bể tới nước
   + chiều cao mức nước = chiều cao của bể - khoảng cách từ mặt bể tới nước
*/
// chân cảm biến sensor siêu âm
const int trigPin = 12; // GPIO12 - D6
const int echoPin = 14; // GPIO14 - D5
// tốc độ sóng âm trong không khí tính theo cm/uS
const float SOUND_VELOCITY = 0.0343;
// chiều cao bể cá theo cm
const float high_water = 70;
// biến đo thời gian truyền sóng
long duration;

/*
   Lấy thời gian gửi gói dữ liệu dùng NTP server
*/
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "1.asia.pool.ntp.org", 0 * 60 * 60, 60 * 1000);

/*
    Biến lưu giá trị cảm biến, thiết bị thực thi
*/
float tds, temperatureC, distanceCm, ledrgb;

/*
   Biến giúp quản lý thời gian gửi dữ liệu lên internet
*/
unsigned long time_send;
unsigned long lastMsg = 0;

/*
   Hàm callback subscribe topic MQTT, được gọi khi có topic publish message
*/
void callback(char* topic, byte* payload, unsigned int length) {
  // in ra serial dữ liệu nhận được
  Serial.print("Message received: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  // xử lý dữ liệu nhận được
  JSONVar myObject = JSON.parse((const char*)payload);
  if (JSON.typeof(myObject) == "undefined") {
    Serial.println("Parsing payload failed!");
    return;
  }
  Serial.println(topic);

  /*
     Kiểm tra tùy theo topic để xử lý tương ứng
     + subTopic: topic gửi lệnh điều khiển các thiết bị máy bơm, led
     + subTopic1: topic yêu cầu vi điều khiển gửi trạng thái thiết bị máy bơm
  */
  String tmp = String(topic);
  String topic2 = String(subTopic);
  String topic3 = String(subTopic1);

  if (tmp == topic2) {
    Serial.println("Command to control device");
    if (myObject.hasOwnProperty("temp")) {
      Serial.println("Control Temp");
      int lampCmd = (int)myObject["temp"];
      if (lampCmd == 1) {
        digitalWrite(LED, HIGH); // bật thanh sười (demo LED)
        delay(1000);
      }
      else if (lampCmd == 0) {
        digitalWrite(LED, LOW); // tắt thanh sười (demo LED)
        delay(1000);
      }
    }
    else if (myObject.hasOwnProperty("pump")) {
      Serial.println("Control Pump");
      int pumpCmd = (int)myObject["pump"];
      if (pumpCmd == 1) {
        digitalWrite(RELAY_PIN, HIGH); // bật máy bơm
        delay(1000);
      }
      else if (pumpCmd == 0) {
        digitalWrite(RELAY_PIN, LOW); // tắt máy bơm
        delay(1000);
      }
    }
    else if (myObject.hasOwnProperty("led")) {
      Serial.println("Control LED");
      int redCmd = (int)myObject["led"]["red"];
      int greenCmd = (int)myObject["led"]["green"];
      int blueCmd = (int)myObject["led"]["blue"];
      analogWrite(redPin, redCmd);
      analogWrite(greenPin, greenCmd);
      analogWrite(bluePin, blueCmd);
      delay(1000);
    }
  }
  else if (tmp == topic3) {
    Serial.println("Require device's status");
    String payload;
    int val;
    if (myObject.hasOwnProperty("temp")) {
      val = digitalRead(LED);
      payload = "{\"d\":{\"temp\":";
      payload += val;
      payload += "}}";
    }
    else if (myObject.hasOwnProperty("pump")) {
      val = digitalRead(RELAY_PIN);
      payload = "{\"d\":{\"pump\":";
      payload += val;
      payload += "}}";
    }
    Serial.print("Sending payload: ");
    Serial.println(payload);
    if (client.publish(pubTopic1, (char*) payload.c_str())) {
      Serial.println("Publish ok");
    } else {
      Serial.println("Publish failed");
    }
  }
}

/*
   Hàm kết nối lại tới MQTT
*/
void mqttReconnect() {
  if (!client.connected()) {
    Serial.print("Reconnecting client to ");
    Serial.println(server);
    while (!client.connect(clientId, authMethod, token)) {
      Serial.print(".");
      delay(500);
    }
    client.subscribe(subTopic);
    client.subscribe(subTopic1);
    Serial.println("Bluemix connected");
  }
}

void setup() {
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(LED, OUTPUT);
  pinMode(RELAY_PIN, OUTPUT);

  // khởi tạo các giá trị mặc định
  digitalWrite(LED, LOW);
  digitalWrite(RELAY_PIN, LOW);
  analogWrite(redPin, 0);
  analogWrite(greenPin, 0);
  analogWrite(bluePin, 0);

  Serial.begin(115200);
  sensors.begin();
  timeClient.begin();

  // kết nối Wifi
  Serial.println();
  Serial.print("Connecting to ");
  Serial.print(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");

  Serial.print("WiFi connected, IP address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  client.loop();
  if (!client.connected()) {
    mqttReconnect();
  }

  unsigned long now = millis();
  if (now - lastMsg > 10000) {
    lastMsg = now;

    // đọc dữ liệu cảm biến nhiệt độ
    sensors.requestTemperatures();
    temperatureC = sensors.getTempCByIndex(0);

    // đọc dữ liệu cảm biến siêu âm
    // Clears the trigPin
    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    // Sets the trigPin on HIGH state for 10 micro seconds
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);
    // Reads the echoPin, returns the sound wave travel time in microseconds
    duration = pulseIn(echoPin, HIGH);
    // Calculate the distance
    distanceCm = duration * SOUND_VELOCITY / 2;

    // fake dữ liệu cảm biến tds
    tds = random(1, 1000);

    // timestamp theo second
    timeClient.update();
    time_send = timeClient.getEpochTime();

    // định dạng chuỗi Json gửi cho server
    String payload = "{\"d\":{\"device_id\":\"" DEVICE_ID "\"";
    payload += ",\"temperature\":";
    payload += temperatureC;
    payload += ",\"water_level\":";
    payload += high_water - distanceCm;
    payload += ",\"tds\":";
    payload += tds;
    payload += ",\"time_send\":";
    payload += time_send;
    payload += "}}";

    Serial.print("Sending payload: ");
    Serial.println(payload);
    if (client.publish(pubTopic, (char*) payload.c_str())) {
      Serial.println("Publish ok");
    } else {
      Serial.println("Publish failed");
    }
  }
}
