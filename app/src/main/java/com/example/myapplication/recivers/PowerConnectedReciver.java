package com.example.myapplication.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PowerConnectedReciver extends BroadcastReceiver {
    public PowerConnectedReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Charging", Toast.LENGTH_SHORT).show();
    }
}
