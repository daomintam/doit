package vn.edu.tdmu.mintam.doit.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import vn.edu.tdmu.mintam.doit.Activity.AlarmActivity;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;

public class AlarmService extends Service {

    private Handler handler = new Handler();

    public static void startAlarm(Context context) {
        Intent intent = new Intent(context, AlarmService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setAlarm();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setAlarm() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        handler.removeCallbacksAndMessages(null);
        DatabaseHandler db = new DatabaseHandler(this);
        Date d = Calendar.getInstance().getTime();
        List<ToDoModel> tasks = db.getFeatureTask(d);
        if (tasks.isEmpty()) {
            stopForeground(true);
            return;
        }
        ToDoModel toDoModel = tasks.get(0);
        int forewarned = 0;
        try {
            String s = toDoModel.getForewarned().replace(" Phút", "");
            forewarned = Integer.parseInt(s);
        } catch (Exception ex) {
        }
        long time = toDoModel.getTimeStamp() - d.getTime();
        long timeD = time - 1000 * 60 * forewarned;
        if (timeD > 0) {
            time = timeD;
        }
        handler.postDelayed(() -> {
            db.openDatabase();
            db.updateAlarmed(toDoModel.getId(), 1);
            Intent intent = new Intent(this, AlarmActivity.class);
            intent.putExtra(ToDoModel.class.getName(), toDoModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            setAlarm();
        }, time);
        String id = "AlarmService";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(id, id, NotificationManager.IMPORTANCE_MIN);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder compat = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Time to alarm");
        startForeground(1234, compat.build());
    }
}
