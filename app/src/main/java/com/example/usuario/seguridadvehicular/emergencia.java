package com.example.usuario.seguridadvehicular;


import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;




public class emergencia extends AppCompatActivity {
    Button MenuR2, IdBCoordenada, IdCortaCorriente;
    double Lat, Lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);
        MenuR2= (Button) findViewById(R.id.MenuR2);
        IdCortaCorriente= (Button) findViewById(R.id.IdCortaCorriente);
        IdBCoordenada= (Button) findViewById(R.id.IdBCoordenada);

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
        Intent i = new Intent(emergencia.this, MapsActivity.class);
        startActivity(i);
        /*
        //Enviar dato de actividad a actividad, pero ya no se requiere este metodo.
        Bundle miBundle = new Bundle();
        Bundle miBundle2 = new Bundle();
        miBundle.putDouble("Dato1",Lat);
        miBundle2.putDouble("Dato2",Lon);
        i.putExtras(miBundle);
        i.putExtras(miBundle2);
        startActivity(i);
        startActivity(i);
        //Enviar mensaje a hardware sim808 pero ya no se rquiere este paso.
        try{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("Numero de hardware a controlar",
                    null, "@GPS", null,null);
            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();

        }

        catch (Exception e){
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos."
                    + e.getMessage().toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
    }



}
