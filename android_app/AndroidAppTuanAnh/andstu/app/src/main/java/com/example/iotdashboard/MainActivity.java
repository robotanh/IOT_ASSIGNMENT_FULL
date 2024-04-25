package com.example.iotdashboard;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView    txtTemp, txtHumil, txtLt, txtA;
    LabeledSwitch btnLED, btnPUMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMQTT();
        txtTemp = findViewById(R.id.txtTemperature);
        txtHumil = findViewById(R.id.txtHumidity);
        txtLt = findViewById(R.id.txtLight);
        txtA = findViewById(R.id.txtAi);
        btnLED = findViewById(R.id.btnLED);
        btnPUMP = findViewById(R.id.btnPUMP);

        btnLED.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("robotanh/feeds/nutnhan1", "1");
                }else{
                    sendDataMQTT("robotanh/feeds/nutnhan1", "0");
                }
            }
        });

        btnPUMP.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn == true){
                    sendDataMQTT("robotanh/feeds/nutnhan2", "1");
                }else{
                    sendDataMQTT("robotanh/feeds/nutnhan2", "0");
                }
            }
        });
    }

    public void sendDataMQTT(String topic, String value){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);

        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        }catch (MqttException e){

        }
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" +message.toString());
                if (topic.contains("cambien1")){
                    txtTemp.setText(message.toString() + "°C");
                }else if(topic.contains("cambien2")) {
                    txtHumil.setText(message.toString() + "%");
                }else if(topic.contains("cambien3")) {
                    txtLt.setText(message.toString() + " lux");
                }else if(topic.contains("ai")) {
                    txtA.setText(message.toString());
                }else if (topic.contains("nutnhan1")){
                    if (message.toString().equals("1")){
                        btnLED.setOn(true);
                    }else {
                        btnLED.setOn(false);
                    }
                }else if (topic.contains("nutnhan2")){
                    if (message.toString().equals("1")){
                        btnPUMP.setOn(true);
                    }else {
                        btnPUMP.setOn(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }
}