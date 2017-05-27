package it.loopstudio.bluecar.bluecar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Bluetooth Activity
 * Created by desmo on 26/05/2017.
 */

public class BTReceiver extends BroadcastReceiver {

    public Context context;

    public BluetoothAdapter mBluetoothAdapter;

    public BTReceiver(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        String action = intent.getAction();

        switch (action){
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                Log.e( "BLUETOOTH ACTIVITY", "ACTION_ACL_CONNECTED");
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
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
