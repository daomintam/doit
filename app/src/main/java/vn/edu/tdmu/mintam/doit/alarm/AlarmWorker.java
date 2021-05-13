package vn.edu.tdmu.mintam.doit.alarm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import vn.edu.tdmu.mintam.doit.Activity.AlarmActivity;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;

public class AlarmWorker extends Worker {
    public AlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String data = getInputData().getString(ToDoModel.class.getName());
        Gson gson = new Gson();
        ToDoModel toDoModel = gson.fromJson(data, ToDoModel.class);
        Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
        intent.putExtra(ToDoModel.class.getName(), toDoModel);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        return Result.success();
    }

    public static void setWork(Context context) {
        WorkManager work = WorkManager.getInstance(context);
        work.cancelAllWork();
        DatabaseHandler db = new DatabaseHandler(context);
        Date d = Calendar.getInstance().getTime();
        List<ToDoModel> tasks = db.getFeatureTask(d);
        if (tasks.isEmpty()) return;
        long time = tasks.get(0).getTimeStamp() - d.getTime();
        Gson gson = new Gson();
        Data data = new Data.Builder()
                .putString(ToDoModel.class.getName(), gson.toJson(tasks.get(0)))
                .build();
//        PeriodicWorkRequest request =
//                new PeriodicWorkRequest.Builder(AlarmWorker.class, time, TimeUnit.MILLISECONDS)
//                        // Constraints
//                        .setInputData(data)
//                        .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(AlarmWorker.class)
                .setInitialDelay(time, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();
        work.enqueue(request);
    }
}
