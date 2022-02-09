package com.example.smartaquarium.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartaquarium.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class TdsFragment extends Fragment {

    private static final String TDS_TIPS_LEVEL_1 = "Hiện tại, giá trị TDS đang ở mức thấp và độ cứng của nước không đủ, cần phải tăng các thành phần nguyên tố vi lượng trong nước để có môi trường sống tốt hơn cho cá.";
    private static final String TDS_TIPS_LEVEL_2 = "Hiện tại, giá trị TDS đang ở mức trung bình và chất lượng nước là lý tưởng cho hoạt động sống của cá.";
    private static final String TDS_TIPS_LEVEL_3 = "Hiện tại, giá trị TDS đang hơi cao và nước có tạp chất, cặn cã. Cần chuẩn bị thay nước cho bể cá để đảm bảo môi trường sống tốt hơn cho cá.";
    private static final String TDS_TIPS_LEVEL_4 = "Hiện tại, giá trị TDS đang ở mức cao. Cần nhanh chóng thay nước trong bể cá.";

    double tdsValue;

    TextView tvTdsValue;
    TextView tvTipsValue;

    // cac thong so ket noi mqtt
    private final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com";
    private final String ORGANIZATION = "8w374s";
    private final String API_KEY = "a-8w374s-xb6inpoy3a";
    private final String AUTHORIZATION_TOKEN= "P*C)1+rgojQU@x(p1O";
    String subscribeTopic;
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;

    private String deviceType;
    private String deviceId;

    public TdsFragment() {
        // Required empty public constructor
    }

    public static TdsFragment newInstance(String deviceType, String deviceId) {
        TdsFragment fragment = new TdsFragment();
        Bundle args = new Bundle();
        args.putString("deviceType", deviceType);
        args.putString("deviceId", deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deviceType = getArguments().getString("deviceType");
            deviceId = getArguments().getString("deviceId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tds, container, false);
        tvTdsValue = view.findViewById(R.id.tv_tds_value);
        tvTipsValue = view.findViewById(R.id.tv_tips_value);

        // dinh nghia cac topic se su dung
        subscribeTopic = "iot-2/type/" + deviceType + "/id/" + deviceId + "/evt/readSensors/fmt/json"; //topic nhan du lieu tư sensor

        // dinh nghia cac thong so ket noi IBM Watson IoT kieu api key
        String clientID = "a:" + ORGANIZATION + ":" + MqttClient.generateClientId();
        String connectionURI = "tcp://" + ORGANIZATION + IOT_ORGANIZATION_TCP;
        String username = API_KEY;
        char[] password = AUTHORIZATION_TOKEN.toCharArray();

        // khoi tao ket noi
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password);
        mqttAndroidClient = new MqttAndroidClient(getActivity(), connectionURI, clientID);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // connect to mqtt server va subscribe cac topic sau do dat callback
        connectToMQTTServer(mqttAndroidClient, mqttConnectOptions);
    }

    @Override
    public void onPause() {
        super.onPause();
        // disconnect to mqtt server
        disconnectToMQTTServer(mqttAndroidClient);
    }

    // Connect
    public void connectToMQTTServer(MqttAndroidClient client, MqttConnectOptions options) {
        try {
            IMqttToken connectToken = client.connect(options);
            connectToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                     subscribeToTopic(client, subscribeTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // Set callback cho Subscribe
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // Nhan du lieu, phan tich va hien thi thong so tds
                try {
                    JSONObject payload = new JSONObject(message.toString());
                    JSONObject data = payload.getJSONObject("d");
                    tdsValue = data.getDouble("tds");
                    tvTdsValue.setText(String.format("%.1f", tdsValue));
                    if (tdsValue >= 0 && tdsValue <= 150) {
                        tvTipsValue.setText(TDS_TIPS_LEVEL_1);
                    } else if (tdsValue >= 151 && tdsValue <= 350) {
                        tvTipsValue.setText(TDS_TIPS_LEVEL_2);
                    } else if (tdsValue >= 351 && tdsValue <= 550) {
                        tvTipsValue.setText(TDS_TIPS_LEVEL_3);
                    } else if (tdsValue >= 551 && tdsValue <= 1000) {
                        tvTipsValue.setText(TDS_TIPS_LEVEL_4);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                //
            }
        });
    }

    // Disconnect
    public void disconnectToMQTTServer(MqttAndroidClient client) {
        try {
            IMqttToken disconnectToken = client.disconnect();
            disconnectToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.unregisterResources();
    }

    // Subscribe
    public void subscribeToTopic(MqttAndroidClient client, String topic) {
        try {
            int qos = 0;
            IMqttToken subscribeToken = client.subscribe(topic, qos);
            subscribeToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The subscription could successfully be added to the client
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}