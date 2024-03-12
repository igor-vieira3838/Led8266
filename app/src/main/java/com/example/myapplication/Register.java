package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Register extends AppCompatActivity {

    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mqttHelper = new MqttHelper(getApplicationContext());

        startMqtt();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText NovoUserEditText = findViewById(R.id.novouser);
        EditText NovaSenhaEditText = findViewById(R.id.novasenha);
        Button SignUp = findViewById(R.id.signup);

        SignUp.setOnClickListener(view -> {
            mqttHelper.publish(NovoUserEditText.getText().toString() + "/" + NovaSenhaEditText.getText().toString(),
                    "Led8266/MySQL/tempId/signup.user_info");
        });

        //teste de publish no MQTT
        EditText CaixaTeste = findViewById(R.id.testebox);
        Button BotaoTeste = findViewById(R.id.testeButton);

        BotaoTeste.setOnClickListener(view -> {
            mqttHelper.publish(mqttHelper.getClientId() + "/get.user_info",
                    "Led8266/MySQL/tempId/get.user_info");
        });
    }

    private void startMqtt() {

        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
                //Aparece essa mensagem sempre que a conexão for perdida
                Toast.makeText(getApplicationContext(), "Conexão perdida", Toast.LENGTH_SHORT).show();
            }

            @Override
            // messageArrived é uma função que é chamada toda vez que o cliente MQTT recebe uma mensagem
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                if(topic.equals("Led8266/MySQL/tempId/generate.user_id")){
                    String newClientId = mqttHelper.generateClientId();
                    mqttHelper.publish(newClientId +"/ ", "Led8266/MySQL/tempId/finishSignup.user_info");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
    }
}
