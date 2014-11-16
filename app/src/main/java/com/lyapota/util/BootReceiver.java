package com.lyapota.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lyapota.tweakslonelyx.R;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SystemHelper.init(context);
            if (SystemHelper.setOnBoot()) {
                Toast.makeText(context, context.getString(R.string.title_section_system), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
