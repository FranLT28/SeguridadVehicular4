package com.example.usuario.seguridadvehicular;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    //Variable
    Button Vehiculo, EmergenciaM, Conectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Vehiculo= (Button) findViewById(R.id.Vehiculo);
        EmergenciaM= (Button) findViewById(R.id.EmergenciaM);
        Conectar= (Button) findViewById(R.id.Conectar);

        Conectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, DispositivosBT.class);
                startActivity(i);
                try{
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage("6391553336", null, "@BT", null,null);
                    Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
                }

                catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos." + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        EmergenciaM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Menu.this,emergencia.class);

                startActivity(a);
            }
        });

        Vehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Menu.this, UserInterfaz.class);
                startActivity(i);
            }
        });

    }



}
