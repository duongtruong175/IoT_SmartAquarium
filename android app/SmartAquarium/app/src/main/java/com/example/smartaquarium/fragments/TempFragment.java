package com.example.smartaquarium.fragments;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;

public class TempFragment extends Fragment {

    double tempValue;

    TextView tvTempValue;
    SwitchCompat sTemp;

    // cac thong so ket noi mqtt
    private final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com";
    private final String ORGANIZATION = "8w374s";
    private final String API_KEY = "a-8w374s-xb6inpoy3a";
    private final String AUTHORIZATION_TOKEN= "P*C)1+rgojQU@x(p1O";
    String publishTopic1, publishTopic2, subscribeTopic1, subscribeTopic2;
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;

    private String deviceType;
    private String deviceId;

    public TempFragment() {
        // Required empty public constructor
    }

    public static TempFragment newInstance(String deviceType, String deviceId) {
        TempFragment fragment = new TempFragment();
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
        View view = inflater.inflate(R.layout.fragment_temp, container, false);

        tvTempValue = view.findViewById(R.id.tv_temp_value);
        sTemp = view.findViewById(R.id.switch_temp);

        // dinh nghia cac topic se su dung
        publishTopic1 = "iot-2/type/" + deviceType + "/id/" + deviceId + "/cmd/statusActuators/fmt/json"; //topic gui yeu cau trang thai thiet bi
        publishTopic2 = "iot-2/type/" + deviceType + "/id/" + deviceId + "/cmd/controlActuators/fmt/json"; //topic dieu khien thiet bi
        subscribeTopic1 = "iot-2/type/" + deviceType + "/id/" + deviceId + "/evt/readStatusActuators/fmt/json"; //topic nhan trang thai thiet bi
        subscribeTopic2 = "iot-2/type/" + deviceType + "/id/" + deviceId + "/evt/readSensors/fmt/json"; //topic nhan du lieu sensors

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

        // bat/tat que suoi
        sTemp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (sTemp.isChecked()) {
                    String payload = "{\"temp\":1}";
                    if (publishMessage(mqttAndroidClient, publishTopic2, payload)) {
                        Toast.makeText(getActivity(), "Bật que sưởi thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        sTemp.setChecked(false);
                    }
                } else {
                    String payload = "{\"temp\":0}";
                    if (publishMessage(mqttAndroidClient, publishTopic2, payload)) {
                        Toast.makeText(getActivity(), "Tắt que sưởi thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        sTemp.setChecked(true);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // connect to mqtt server
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
                    subscribeToTopic(client, subscribeTopic1);
                    subscribeToTopic(client, subscribeTopic2);
                    // gui du lieu de lay trang thai thiet bi
                    String payload = "{\"temp\": \"Get status\"}";
                    publishMessage(client, publishTopic1, payload);
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
                if (topic.equals(subscribeTopic1)) {
                    try {
                        JSONObject payload = new JSONObject(message.toString());
                        JSONObject data = payload.getJSONObject("d");
                        if (data.getInt("pump") == 1) {
                            sTemp.setChecked(true);
                        } else {
                            sTemp.setChecked(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (topic.equals(subscribeTopic2)) {
                    try {
                        JSONObject payload = new JSONObject(message.toString());
                        JSONObject data = payload.getJSONObject("d");
                        tempValue = data.getDouble("temperature");
                        tvTempValue.setText(String.format("%.1f", tempValue) + "°C");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    // Publish
    public boolean publishMessage(MqttAndroidClient client, String topic, String payload) {
        try {
            byte[] encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            IMqttToken publishToken = client.publish(topic, message);
            publishToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Some errors occurred
                }
            });
            return true;
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
            return false;
        }
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