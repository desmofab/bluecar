package it.loopstudio.bluecar.bluecar;

import android.app.IntentService;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Play Service and BT management
 * Created by desmo on 24/05/2017.
 */

public class ActivityRecognizedService  extends IntentService {

    private BluetoothAdapter mBluetoothAdapter;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }

    private Timer countdown;

    private TimerTask TurnOffBt;

    //public static String headsetName = "desmoInsignia";


    public void onCreate(){
        super.onCreate();

        //Bluetooth Adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //New Timer
        countdown = new Timer();

        //Scheduled Action check if headset is connected
        TurnOffBt = new TimerTask() {
            @Override
            public void run() {

                //List connectedDevice = mBluetoothHeadset.getConnectedDevices();
                //if(connectedDevice.isEmpty())
                Log.e("Timer","Controllo headsets connessi");
                if(noBluetoothHeadsetConnected()) {
                    DisableBluetooth();
                    //Log.e("Timer","Inattività Bluetooth verrà disabilitato se attivo");
                }
            }
        };

        //Nel caso il BT fosse attivo già da prima
        if (mBluetoothAdapter.isEnabled() && noBluetoothHeadsetConnected())
            startTimer();
    }

    private void startTimer(){

        //Countdown Starts
        countdown.schedule(TurnOffBt, 60000, 180000);
        Log.e("Timer","Timer partito");
    }


    private void stopTimer(){

        //Countdown Stops
        countdown.cancel();
        countdown.purge();
        Log.e("Timer","Timer spento");
    }


    //Intent Servizio Background
    @Override
    protected void onHandleIntent(Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            //NEW SOLUTION
            DetectedActivity MostProbableActivity = result.getMostProbableActivity();

            if(MostProbableActivity.getType() == DetectedActivity.IN_VEHICLE && MostProbableActivity.getConfidence() >= 75){

                EnableBluetooth();
            }


            //05/05/2017

            //handleDetectedActivities( result.getProbableActivities() );
        }
    }


    //Abilita antenna BT
    private void EnableBluetooth(){

        if(isWifiConnected()){
            Log.e("Bluecar","...but WiFi is connected");
            return;
        }

        //if(!isDeviceNearby(headsetName))
            //return;

        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();

            startTimer();

            Log.e("Bluecar","Bluetooth Enabled");
            notifying("turned ON");
        }

    }


    //Disabilita antenna BT
    private void DisableBluetooth(){

        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();

            Log.e("Bluecar","Bluetooth Disabled");
            notifying("turned OFF");
        }else
        {
            Log.e("Bluecar","Bluetooth already disabled");
        }

        stopTimer();

    }



    //Check if a BT Headset device is connected
    private boolean noBluetoothHeadsetConnected() {
        return !(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED);
    }



//    //Check if specificed BT Device is nearby
//    public boolean isDeviceNearby(String BTDev){
//
//        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//            }
//        }
//
//        return false;
//    }


    //Check if WIFI is connected
    private boolean isWifiConnected(){

        ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        //NetworkInfo wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); --DEPRECATED METHOD

        return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }




    //Notifica antenna ON/OFF
    private void notifying(String onOff){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText( "Bluetooth "+ onOff);
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        builder.setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }


//    //Cambio stato Play Service
//    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
//        for( DetectedActivity activity : probableActivities ) {
//            switch( activity.getType() ) {
//                case DetectedActivity.IN_VEHICLE: {
//                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
//                    if(activity.getConfidence() >= 75) {
//                        EnableBluetooth();
//                    }
//                    break;
//                }
//                case DetectedActivity.ON_BICYCLE: {
//                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
//                    break;
//                }
//                case DetectedActivity.ON_FOOT: {
//                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
//                    break;
//                }
//                case DetectedActivity.RUNNING: {
//                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
//                    break;
//                }
//                case DetectedActivity.STILL: {
//                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
////                    if(activity.getConfidence() >= 75) {
////                        EnableBluetooth();
////                    }
//                    break;
//                }
//                case DetectedActivity.TILTING: {
//                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
//                    break;
//                }
//                case DetectedActivity.WALKING: {
//                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
//                    //DisableBluetooth(activity.getConfidence());
//                    break;
//                }
//                case DetectedActivity.UNKNOWN: {
//                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
//                    //DisableBluetooth();
//                    break;
//                }
//            }
//        }
//    }


}
