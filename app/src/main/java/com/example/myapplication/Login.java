package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Login extends AppCompatActivity {

    private MqttAndroidClient mqttAndroidClient;
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mqttHelper = new MqttHelper(getApplicationContext());

        startMqtt();

        EditText UserEditText = findViewById(R.id.user);
        EditText SenhaEditText = findViewById(R.id.senha);
        Button BotaoLogin = findViewById(R.id.loginbtn);
        Button NovoUsuario = findViewById(R.id.novo_cadastro_qm);
        Button GetID = findViewById(R.id.getidbutton);

        BotaoLogin.setOnClickListener(view -> {
            mqttHelper.publish(UserEditText.getText().toString() + "/" + SenhaEditText.getText().toString(),
                    "Led8266/MySQL/tempId/login.user_info");
        });

        GetID.setOnClickListener(view -> {
            mqttHelper.publish(mqttHelper.getClientId() + "/get.user_info",
                    "Led8266/MySQL/" + mqttHelper.getClientId() + "/get.user_info");
        });

        NovoUsuario.setOnClickListener(view -> {
            startActivity(new Intent(this, Register.class));
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
                if (topic.equals("Led8266/MySQL/tempId/returnId.user_info")) {
                    try {
                        Log.w("Debug", mqttMessage.toString());
                        String[] loggedClientID = mqttMessage.toString().split("/");

                        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), mqttHelper.getServerUri(), loggedClientID[0]);
                        mqttHelper.publish(mqttHelper.clientId +"/"+ loggedClientID[0], "Led8266/MySQL/tempId/client.id");
                    } catch (Error e) {
                        Toast.makeText(Login.this, "Erro: " + e, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
    }
}