package com.example.handsonrescue;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SMSSend {
    public void smsSend(String number, String msg, Context context,double latitude,double longitude){

//        String sendMsg = msg+" " + latitude +" " + longitude + " ajay@1996";
        String sendMsg = msg +" Code->"+"ajay@1996"+" Latitude:"+latitude+" Longitude:"+longitude;

        try {
            SmsManager smsManager=SmsManager.getDefault();
            smsManager.sendTextMessage(number,null,sendMsg,null,null);
            Toast.makeText(context,"Sent",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(context,"Some fiels is Empty",Toast.LENGTH_LONG).show();
        }
    }

}
