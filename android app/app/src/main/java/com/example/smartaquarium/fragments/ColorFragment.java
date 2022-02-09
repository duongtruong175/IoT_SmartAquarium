package com.example.smartaquarium.fragments;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartaquarium.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class ColorFragment extends Fragment {

    TextView tvOpenPicker;

    // cac thong so ket noi mqtt
    private final String IOT_ORGANIZATION_TCP = ".messaging.internetofthings.ibmcloud.com";
    private final String ORGANIZATION = "8w374s";
    private final String API_KEY = "a-8w374s-xb6inpoy3a";
    private final String AUTHORIZATION_TOKEN= "P*C)1+rgojQU@x(p1O";
    String publishTopic;
    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;

    private String deviceType;
    private String deviceId;

    public ColorFragment() {
        // Required empty public constructor
    }

    public static ColorFragment newInstance(String deviceType, String deviceId) {
        ColorFragment fragment = new ColorFragment();
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
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        tvOpenPicker = view.findViewById(R.id.tv_open_picker);
        tvOpenPicker.setBackgroundColor(0xffff0000);

        // dinh nghia cac topic se su dung
        publishTopic = "iot-2/type/" + deviceType + "/id/" + deviceId + "/cmd/controlActuators/fmt/json"; //topic dieu khien thiet bi

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

        tvOpenPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorDrawable cd = (ColorDrawable) tvOpenPicker.getBackground();
                int colorCode = cd.getColor();
                ColorPickerDialogBuilder
                        .with(getActivity())
                        .setTitle("Chọn màu sắc")
                        .initialColor(colorCode)
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                int red = Color.red(selectedColor);
                                int green = Color.green(selectedColor);
                                int blue = Color.blue(selectedColor);
                                String payload = "{\"led\":{\"red\":" + red + ",\"green\":" + green + ",\"blue\":" + blue + "}}";
                                if (publishMessage(mqttAndroidClient, publishTopic, payload)) {
                                    tvOpenPicker.setBackgroundColor(selectedColor);
                                    Toast.makeText(getActivity(), "Điều chỉnh đèn LED thành công", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Có lỗi xảy ra, xin thử lại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("hủy bỏ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
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
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
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
}