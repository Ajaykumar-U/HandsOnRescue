package com.example.handsonrescue;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.handsonrescue.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements InMedia, InComingMsgCallback {

    String receivedMsg;

    InfGetLocation infGetLocation;

    LocationListener locationListener;
    LocationManager locationManager;
    double latitude, longitude;

    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    Button sendBtn;
    EditText txtphoneNo;
    EditText txtMessage;

    MediaPlayer mediaPlayer;
    ActivityMainBinding binding;

    static MainActivity mainActivity;

    static MainActivity getInstance() {
        return mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        mainActivity = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        /*
        Location Manager & Services
         */
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (( ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) && ( ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 10, locationListener);
//
////        sendBtn = (Button) findViewById(R.id.btnSendMsg);
//        txtphoneNo = (EditText) findViewById(R.id.etPhone);
//        txtMessage = (EditText) findViewById(R.id.etText);

        //GIVE SEND PERMISSION
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }

        //Give RECEIVE Permission
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }

        binding.btnSendMsg.setOnClickListener(view -> {
            String number = binding.etPhone.getText().toString();
            String msg = binding.etText.getText().toString();
            SMSSend sms = new SMSSend();
            sms.smsSend(number, msg, getApplicationContext(), latitude, longitude);
        });

        int resID = getResources().getIdentifier("music", "raw", getPackageName());
        mediaPlayer = MediaPlayer.create(MainActivity.this, resID);

        binding.btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str1="",str2="";
                int num=0;
                int i,j;
                String str=receivedMsg;
                char ch[]=str.toCharArray();
                for(i=0;i<ch.length;i++) {
                    if(ch[i]==':') {
                        for (j = i+1; j < ch.length; j++) {
                            if(ch[j]==' ') {
                                break;
                            }
                            else {
                                if(num==0) {
                                    str1=str1+ch[j];
                                }
                                else {
                                    str2=str2+ch[j];
                                }
                            }
                        }
                        num=1;
                        i=j;
                    }

                }
                mediaPlayer.pause();
                double lat2 = Double.parseDouble(str1);
                double long2 = Double.parseDouble(str2);
                double lat1 = latitude;
                double long1 = longitude;

                String uriLoc = "http://maps.google.com/maps?saddr=" + lat1 + "," + long1 + "&daddr=" + lat2 + "," + long2;
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uriLoc));
                startActivity(intent);
            }
        });
    }


    @Override
    public void playMusic() {
        mediaPlayer.start();
    }

    @Override
    public void onMessageReceived(String msg) {
        receivedMsg=msg;
         Animation mShakeAnimation= AnimationUtils.loadAnimation(this,R.anim.shake_animation);
        binding.btnGetLocation.startAnimation(mShakeAnimation);
        //todo when message arried
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}