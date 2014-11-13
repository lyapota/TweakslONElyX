package com.lyapota.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SystemHelper.setContext(context);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "Kernel tweaks applied", Toast.LENGTH_SHORT).show();
            Log.d("BootReceiver", "BOOT_COMPLETED");
        }
    }

}
