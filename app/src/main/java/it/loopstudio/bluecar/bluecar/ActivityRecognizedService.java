package it.loopstudio.bluecar.bluecar;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import android.bluetooth.BluetoothAdapter;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Play Service
 * Created by desmo on 24/05/2017.
 */

public class ActivityRecognizedService  extends IntentService {

    public BluetoothAdapter mBluetoothAdapter;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name) {
        super(name);
    }



    public void onCreate(){
        super.onCreate();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivities( result.getProbableActivities() );
        }
    }


    private void EnableBluetooth(){

        if (!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();

            Log.e("Bluecar","Bluetooth Enabled");
            notifying("turned ON");
        }

    }


//    private void DisableBluetooth(){
//
//        if (mBluetoothAdapter.isEnabled()){
//            mBluetoothAdapter.disable();
//            Log.e("Bluecar","Bluetooth Disabled");
//            notifying("turned OFF");
//        }
//
//    }


    private void notifying(String onOff){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentText( "Bluetooth "+ onOff);
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( getString( R.string.app_name ) );
        NotificationManagerCompat.from(this).notify(0, builder.build());
    }

    private void handleDetectedActivities(List<DetectedActivity> probableActivities) {
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e( "ActivityRecogition", "In Vehicle: " + activity.getConfidence() );
                    if(activity.getConfidence() >= 80) {
                        EnableBluetooth();
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
//                    if(activity.getConfidence() >= 75) {
//                        EnableBluetooth();
//                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    //DisableBluetooth(activity.getConfidence());
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    //DisableBluetooth();
                    break;
                }
            }
        }
    }






//    public static boolean isBluetoothHeadsetConnected() {
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()
//                && mBluetoothAdapter.getProfileConnectionState(BluetoothHeadset.HEADSET) == BluetoothHeadset.STATE_CONNECTED;
//    }





}
