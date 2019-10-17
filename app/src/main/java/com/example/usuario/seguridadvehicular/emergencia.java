package com.example.usuario.seguridadvehicular;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.UUID;
import java.util.jar.Manifest;

public class emergencia extends AppCompatActivity {
    Button MenuR2, IdBCoordenada, IdCortaCorriente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);
        MenuR2= (Button) findViewById(R.id.MenuR2);
        IdCortaCorriente= (Button) findViewById(R.id.IdCortaCorriente);

        MenuR2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(emergencia.this, Menu.class);
                startActivity(i);
            }

        });

        if(ActivityCompat.checkSelfPermission
                (emergencia.this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission
                (emergencia.this, android.Manifest.permission.SEND_SMS)
                !=PackageManager.PERMISSION_GRANTED){ActivityCompat.requestPermissions(emergencia.this, new String[]
                {
                        android.Manifest.permission.SEND_SMS,},1000);
                }else{

         };
    }



    public void EnviarMensaje(View v){
            try{
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("6391553336", null, "@CC", null,null);
                Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
            }

            catch (Exception e){
                Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos."
                        + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


    public void EnviarCoordenadas(View v){
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("6391553336", null, "@GPS", null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage().toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



}
