package com.example.usuario.seguridadvehicular;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GeoQueryEventListener {

    private static final String TAG = "MapsActivity";


    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE=7192;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST=300193;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiCLient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL=5000;
    private static int FASTEST_INTERVAL=3000;
    private static int DISPLACEMENT=10;
    private DatabaseReference ref2;


    /////////////////////////////////////Notificaciones en segundo plano
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 500;
    private String GEOGENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE=10001;


    ////////////////////////////////////////////////////////




    DatabaseReference ref;
    GeoFire geoFire;
    Marker myCurrent;
    Double d3=0.0, d4=0.0;
    Double d5=0.0, d6=0.0;
    int ccreada;
    int c=0, cc,g=0;
    int radio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpLocation();
        //recibirDatos();


        ref= FirebaseDatabase.getInstance().getReference("Mi localizacion vehicular");
        geoFire=new GeoFire(ref);


        /////////////////////////////////////////////Notificaciones en segundo plano
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

    }

    //Requisito de permisos google
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    if (checkPlayServices())
                    {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
                break;
        }
    }

    //Requisistos de permisos de localizacion.
    private void setUpLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            //
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION_REQUEST_CODE);
        }else{
            if (checkPlayServices())
            {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }


    //
    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        mLastLocation=LocationServices.FusedLocationApi.getLastLocation(mGoogleApiCLient);
        if (mLastLocation!=null)
        {
            final double latitude=mLastLocation.getLatitude();
            final double longitude=mLastLocation.getLongitude();
            Log.d("SSV",String.format("Tu localizacion cambio: %f /%f",latitude, longitude));
        }
        else {
            Log.d("SSV","No has obtenido tu localizacion.");
        }
    }

    private void createLocationRequest() {
        mLocationRequest= new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {
        mGoogleApiCLient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiCLient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            else{
                Toast.makeText(this,"No se soporta este dispositivo", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        



        //Obtener coordenadas del vehiculo por Firebase.
        ref2=FirebaseDatabase.getInstance().getReference();
        ref2.child("Sim808").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final double latitudeV= Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
                    final double longitudeV= Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
                    d5=latitudeV;
                    d6=longitudeV;

                    //Actualizar localizacion actual del vehiculo en maps.
                    geoFire.setLocation("808Sim", new GeoLocation(d5, d6),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    //agregar marca
                                    if (myCurrent != null)
                                        myCurrent.remove(); //borra vieja marca
                                    myCurrent = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(d5,d6))
                                            .title("Aqui esta tu vehiculo.")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconomaps)));
                                    //Mueve la camara al marcador
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(d5, d6), 15.0f));
                                    g=1;
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        //Habilitar ubicacion actual del dispositivo android
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //Creacion de Geocerca por metodo anterior al de las notificaciones de segundo plano.
        /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                if (c==0){
                    mMap.addMarker( new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.igeocerca))
                            .position(latLng));
                    //Coordenadas Geocerca
                    final LatLng cerca = new LatLng(latLng.latitude,latLng.longitude);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if (c==0){
                                mMap.addCircle(new CircleOptions()
                                        .center(cerca)
                                        .radius(500)
                                        .strokeColor(Color.GREEN)
                                        .fillColor(0X220000FF)
                                        .strokeWidth(5.0f)
                                );//Consulta geocerca //.5f = 500 metros
                                GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(cerca.latitude,cerca.longitude), 0.5f);
                                Toast.makeText(getApplicationContext(), "Cerco creado "+cerca, Toast.LENGTH_LONG).show();
                                geoQuery.addGeoQueryEventListener(MapsActivity.this);
                                c=1;

                                int correcto= 1;
                                //Sube la cerca creada a firebase
                                ref2.child("Mi ubicacion de cerco coordenado").child("latitude").setValue(cerca.latitude);
                                ref2.child("Mi ubicacion de cerco coordenado").child("longitude").setValue(cerca.longitude);
                                ref2.child("Mi ubicacion de cerco coordenado").child("Cerco creado").setValue(correcto);
                            }

                            c=1;
                            return false;
                        }
                    });

                }
            }
        });*/




        //Guardar ubicacion del cerco coordenado en Firebase
            ref2=FirebaseDatabase.getInstance().getReference();
            ref2.child("Mi ubicacion de cerco coordenado").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int ccreado = Integer.parseInt(dataSnapshot.child("Cerco creado").getValue().toString());
                    final double latitudeC= Double.valueOf(dataSnapshot.child("latitude").getValue().toString());
                    final double longitudeC= Double.valueOf(dataSnapshot.child("longitude").getValue().toString());
                    d3=latitudeC;
                    d4=longitudeC;
                    ccreada=ccreado;

                    //Actualizar localizacion actual del vehiculo en maps.
                    geoFire.setLocation("CercaCoordenada", new GeoLocation(d3, d4),
                            new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {
                                    if (ccreada==1){

                                        mMap.addCircle(new CircleOptions()
                                                .center(new LatLng(d3,d4))
                                                .radius(500)
                                                .strokeColor(Color.GREEN)
                                                .fillColor(0X220000FF)
                                                .strokeWidth(5.0f)

                                        );//Consulta geocerca //.5f = 500 metros
                                        GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(d3,d4), 0.5f);
                                        //Toast.makeText(getApplicationContext(), "Cerco creado "+cerca, Toast.LENGTH_LONG).show();
                                        geoQuery.addGeoQueryEventListener(MapsActivity.this);
                                        addGeofence(new LatLng(d3,d4), GEOFENCE_RADIUS);

                                    }
                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            mMap.setOnMapLongClickListener(this);

    }






    //Recivir dato de acticity anterior.
    /*public void recibirDatos(){
        Bundle miBundle=this.getIntent().getExtras();
        Double d1= miBundle.getDouble("Dato1");
        Double d2= miBundle.getDouble("Dato2");
        d3= d1;
        d4= d2;
    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiCLient,mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiCLient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation= location;
        displayLocation();

    }

    @Override
    public void onKeyEntered(String key, GeoLocation location) {
        sendNotification("SSV",String.format("Tu vehiculo esta DENTRO del area establecida"));

    }

    @Override
    public void onKeyExited(String key) {
        sendNotification("SSV",String.format("Tu vehiculo esta fuera de los limites del area establecida."));
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d("Move",String.format("Muevete de esta area [%f/%f]",location.latitude, location.longitude));
    }

    @Override
    public void onGeoQueryReady() {

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.e("ERROR",""+error);
    }

    private void sendNotification(String title, String content){
        String NOTIFICATION_CHANNEL_ID="SSV";
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            //Configuracion de la notificacion del vehiculo
            notificationChannel.setDescription("Channel Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{1000,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(MapsActivity.this,MapsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MapsActivity.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));



        Notification notification=builder.build();
        notificationManager.notify(new Random().nextInt(),notification);
    }


 ///////Notificaciones en segundo plano


    @Override
    public void onMapLongClick(LatLng latLng) {

        /*if (Build.VERSION.SDK_INT>=29){
            //Se nececita el permiso del background
            //if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCKATION)==PackageManager.PERMISSION_GRANTED){
                mMap.clear();
                addMarker(latLng);
                addCircle(latLng, GEOFENCE_RADIUS);
                addGeofence(latLng, GEOFENCE_RADIUS);

            }
        }*/
        handleMapLongClick(latLng);


    }



    private void handleMapLongClick(LatLng latLng){
        //mMap.clear();
        //addMarker(latLng);
        //addCircle(latLng, GEOFENCE_RADIUS);
        //addGeofence(latLng, GEOFENCE_RADIUS);


        if (c==0){
            if (ccreada==0){
                mMap.addMarker( new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.igeocerca))
                        .position(latLng));
                //Coordenadas Geocerca
                final LatLng cerca = new LatLng(latLng.latitude,latLng.longitude);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (c==0){


                                mMap.addCircle(new CircleOptions()
                                        .center(cerca)
                                        .radius(500)
                                        .strokeColor(Color.GREEN)
                                        .fillColor(0X220000FF)
                                        .strokeWidth(5.0f)
                                );//Consulta geocerca //.5f = 500 metros
                                GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(cerca.latitude,cerca.longitude), 0.5f);
                                Toast.makeText(getApplicationContext(), "Cerco creado "+cerca, Toast.LENGTH_LONG).show();
                                geoQuery.addGeoQueryEventListener(MapsActivity.this);


                                //Agregamos cerco de notificaciones en segundo plano
                                addGeofence(cerca, GEOFENCE_RADIUS);


                                c=1;
                                int correcto= 1;
                                //Sube la cerca creada a firebase
                                ref2.child("Mi ubicacion de cerco coordenado").child("latitude").setValue(cerca.latitude);
                                ref2.child("Mi ubicacion de cerco coordenado").child("longitude").setValue(cerca.longitude);
                                ref2.child("Mi ubicacion de cerco coordenado").child("Cerco creado").setValue(correcto);



                        }

                        c=1;
                        return false;
                    }
                });
            }

        }

    }



   private void addGeofence(LatLng latLng, float radius){
        Geofence geofence = geofenceHelper.getGeofence(GEOGENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL
                |Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.geofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofance added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG,"OnFailure"+errorMessage);
                    }
                });
    }


    ///////////////////////////////////////////////////////////////////




    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255,0,0));
        circleOptions.fillColor(Color.argb(65, 255,0, 0));
        circleOptions.strokeColor(4);
        mMap.addCircle(circleOptions);
    }
}
