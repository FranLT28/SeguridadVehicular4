package com.example.usuario.seguridadvehicular;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class UserInterfaz extends AppCompatActivity {

    //1)
    Button IdEncender, IdApagar,IdDesconectar, IdMenu, IdCoordenada, IdRegister;
    TextView IdBufferIn,IdConectado;
    private TextView et1, et2;
    int r=0, F=0;
    double Lat, Lon, Latanterior;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //-------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private static String address = null;
    public static int MILISEGUNDOS_ESPERA =8000;

    //-------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interfaz);
        //2)
        //Enlaza los controles con sus respectivas vistas
        IdEncender = (Button) findViewById(R.id.IdEncender);
        IdApagar = (Button) findViewById(R.id.IdApagar);
        IdMenu = (Button) findViewById(R.id.IdMenu);
        IdDesconectar = (Button) findViewById(R.id.IdDesconectar);
        IdBufferIn = (TextView) findViewById(R.id.IdBufferIn);
        IdConectado = (TextView) findViewById(R.id.IdConectado);
        IdCoordenada = (Button) findViewById(R.id.IdCoordenada);
        IdRegister = (Button) findViewById(R.id.IdRegister);
        et1=(TextView) findViewById(R.id.et1);
        et2=(TextView) findViewById(R.id.et2);



        bluetoothIn = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {
                    String readMessage = (String) msg.obj;
                    DataStringIN.append(readMessage);

                   // IdBufferIn.setText("Dato: " + DataStringIN);//<-<- PARTE A MODIFICAR >->->

                    int endOfLineIndex = DataStringIN.indexOf("#");

                    if (endOfLineIndex > 0) {
                        if (r==1){
                            String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                            r=2;
                            String coord=dataInPrint;
                            String[] parts= coord.split(",");
                            String part1=parts[0];
                            String part2=parts[1];

                            et1.setText("Latitud: "+part1);
                            et2.setText("Longitud: "+part2);

                            Lat = Double.parseDouble(part1);
                            Lon = Double.parseDouble(part2);

                        }
                        if(r==0){
                            String dataInPrint = DataStringIN.substring(0, endOfLineIndex);
                            IdBufferIn.setText("Dato: " + dataInPrint);//<-<- PARTE A MODIFICAR >->->
                            r=0;
                        }
                        DataStringIN.delete(0, DataStringIN.length());
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        // Configuracion onClick listeners para los botones
        // para indicar que se realizara cuando se detecte
        // el evento de Click
        IdEncender.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                MyConexionBT.write("Y");
            }
        });

        IdApagar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MyConexionBT.write("X");

            }
        });

        IdCoordenada.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyConexionBT.write("Z");
                r=1;

            }
        });


       /*IdRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                        if(Latanterior!=Lat){
                            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                                    "administracion", null, 1);
                            SQLiteDatabase bd = admin.getWritableDatabase();
                            ContentValues registro = new ContentValues();
                            registro.put("Latitud", Lat);
                            registro.put("Longitud", Lon);
                            bd.insert("cerco", null, registro);
                            bd.close();
                            Toast.makeText(this, "Se cargaron los datos del artículo",
                                    Toast.LENGTH_SHORT).show();
                            Latanterior=Lat;
                            MyConexionBT.write("Z");
                            r=1;
                        }
                    }
        });*/


        IdDesconectar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (btSocket!=null)
                {
                    try {btSocket.close();}
                    catch (IOException e)
                    { Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();;}
                }
                finish();
            }
        });


        IdMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInterfaz.this, Menu.class);
                //i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                startActivity(i);
            }
        });



    }

    public void Regist(View v) {
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                            "administracion", null, 1);
                    SQLiteDatabase bd = admin.getWritableDatabase();
                    ContentValues registro = new ContentValues();
                    registro.put("Latitud", Lat);
                    registro.put("Longitud", Lon);
                    bd.insert("cerco", null, registro);
                    bd.close();
                    Toast.makeText(this, "Se cargaron los datos del artículo",
                            Toast.LENGTH_SHORT).show();
                    Latanterior=Lat;
                    MyConexionBT.write("Z");
                    r=1;
                    esperarYCerrar(MILISEGUNDOS_ESPERA);
    }

    public void esperarYCerrar(int milisegundos) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // acciones que se ejecutan tras los milisegundos
                IdRegister.performClick();
            }
        }, milisegundos);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException
    {
        //crea un conexion de salida segura para el dispositivo
        //usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //Consigue la direccion MAC desde DeviceListActivity via intent
        Intent intent = getIntent();
        //Consigue la direccion MAC desde DeviceListActivity via EXTRA
        address = intent.getStringExtra(DispositivosBT.EXTRA_DEVICE_ADDRESS);//<-<- PARTE A MODIFICAR >->->
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

   /* se sale de la aplicación esta parte permite
            // @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }*/

    //Comprueba que el dispositivo Bluetooth Bluetooth está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try
            {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            byte[] buffer = new byte[256];
            int bytes;

            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //Envio de trama
        public void write(String input)
        {
            try {
                mmOutStream.write(input.getBytes());
            }
            catch (IOException e)
            {
                //si no es posible enviar datos se cierra la conexión
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
