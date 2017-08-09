package it.loopstudio.bluecar.bluecar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Bluetooth Activity
 * Created by desmo on 26/05/2017.
 */

public class BTReceiver extends BroadcastReceiver {

    private boolean BTisConnected;

    private Context context;

    private final BluetoothAdapter mBluetoothAdapter;

    private final Timer countdown;

    private final TimerTask TurnOffBt;

    public BTReceiver(){

        //BTisConnected = false;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        countdown = new Timer();

        TurnOffBt = new TimerTask() {
            @Override
            public void run() {

                if(!BTisConnected)
                    DisableBluetooth();
            }
        };
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        String action = intent.getAction();
        int state;

        switch (action){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_OFF)
                {
                    //Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show();
                    Log.d("BroadcastActions", "Bluetooth is off");
                }
                else if (state == BluetoothAdapter.STATE_TURNING_OFF)
                {
                    //Toast.makeText(context, "Bluetooth is turning off", Toast.LENGTH_SHORT).show();
                    Log.d("BroadcastActions", "Bluetooth is turning off");
                }
                else if(state == BluetoothAdapter.STATE_ON)
                {
                    Log.d("BroadcastActions", "Bluetooth is on");
                    BTisConnected = false;
                    //Disconnect if no devices was connected within 5 mins
                    countdown.schedule(TurnOffBt, 60000);
                }
                break;

            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.e( "BLUETOOTH ACTIVITY", "ACTION_ACL_CONNECTED");

                BTisConnected = true;
                TurnOffBt.cancel();
                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                BTisConnected = false;
                DisableBluetooth();

                Log.e( "BLUETOOTH ACTIVITY", "ACTION_ACL_DISCONNECTED");
                break;
        }
    }


    private void DisableBluetooth(){

        if (mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
            Log.e("Bluecar","Bluetooth Disabled");
            notifying();
        }

    }


    private void notifying(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText( "Bluetooth is OFF");
        builder.setSmallIcon( R.mipmap.ic_launcher );
        builder.setContentTitle( context.getString( R.string.app_name ) );
        NotificationManagerCompat.from(context).notify(0, builder.build());
    }


}
