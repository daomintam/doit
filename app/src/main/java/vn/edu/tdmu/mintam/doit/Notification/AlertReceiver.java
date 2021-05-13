package vn.edu.tdmu.mintam.doit.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import vn.edu.tdmu.mintam.doit.Helper.NotificationHelper;
import vn.edu.tdmu.mintam.doit.R;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b = intent.getExtras();
        Toast.makeText(context,b.getString("tit"),Toast.LENGTH_LONG).show();
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.sam);
        mp.start();
    }
}
