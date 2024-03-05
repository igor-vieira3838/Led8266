package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Login extends AppCompatActivity {

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

        BotaoLogin.setOnClickListener(view -> {
            mqttHelper.publish(UserEditText.getText().toString()+"/"+SenhaEditText.getText().toString(),
                    "MeuNovoApp/"+mqttHelper.getClientId()+"/Login/Infos");
        });

        startActivity(new Intent(this, Register.class));

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
                if(topic.equals("MeuNovoApp/"+mqttHelper.getClientId()+"/Login/Infos")) {

                    // Altere os valores necessários dentro da condição "if" para validar o usuário e senha
                    try {
                        String[] strings = mqttMessage.toString().split("/");
                        if (strings[0].equals("Igor") && strings[1].equals("abc")) {
                            Toast.makeText(Login.this, "Login aprovado!", Toast.LENGTH_SHORT).show();
                        } else if (strings[0].equals("admin") && !strings[1].equals("admin")) {
                            Toast.makeText(Login.this, "Senha incorreta!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Credenciais inválidas!", Toast.LENGTH_SHORT).show();
                        }
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