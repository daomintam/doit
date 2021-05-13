package vn.edu.tdmu.mintam.doit.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkQuery;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import vn.edu.tdmu.mintam.doit.Adapters.ToDoAdapter;
import vn.edu.tdmu.mintam.doit.Helper.RecyclerItemTouchHelper;
import vn.edu.tdmu.mintam.doit.Listener.DialogCloseListener;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;
import vn.edu.tdmu.mintam.doit.alarm.AlarmWorker;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {
    TextView txtCurrentDate, txtHello, txtTatca, txtDaht, txtConlai;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    Calendar calen = Calendar.getInstance();
    Locale id = new Locale("vi", "VN");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", id);
    public static boolean isActionMode = false;
    private int counter = 0;
    private DatabaseHandler db;
    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private HorizontalCalendar horizontalCalendar;
    private static List<ToDoModel> taskList;
    private Date date;
    private TextClock textClock;
    private BottomNavigationView bottomView;
    private LottieAnimationView animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        textClock = findViewById(R.id.digitalClock);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        txtCurrentDate = findViewById(R.id.CurrentDateTextview);
        txtHello = findViewById(R.id.HelloTextView);
        txtConlai = findViewById(R.id.txtConlai);
        txtDaht = findViewById(R.id.txtDahoanthanh);
        txtTatca = findViewById(R.id.txtTatca);
        animation = findViewById(R.id.animationAdd);


        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm a");
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour <= 11) {
            txtHello.setText("Chào buổi sáng");
        } else if (hour > 11 && hour <= 14) {

            txtHello.setText("Chào buổi trưa");
        } else if (hour > 14 && hour <= 18) {
            txtHello.setText("Chào buổi chiều");
        } else if (hour > 18 && hour < 24) {
            txtHello.setText("Chào buổi tối");
        }
        bottomView = findViewById(R.id.bottomNavigationView);
//        chip.setItemSelected(R.id.day,true);
        bottomView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.month:
                    Intent in = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(in);
                    break;
                case R.id.chart:
                    Intent chart = new Intent(MainActivity.this, ChartActivity.class);
                    startActivity(chart);
                    break;
                case R.id.day:
                    break;
                case R.id.info:
                    Intent info = new Intent(MainActivity.this, InformationActivity.class);
                    startActivity(info);
                    break;
            }
            return true;
        });
        db = new DatabaseHandler(this);
        db.openDatabase();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);
        final Calendar defaultSelectedDate = Calendar.getInstance();

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EEE")
                .showTopText(false)
                .showBottomText(true)
                .selectorColor(Color.parseColor("#FFFFB300"))
                .textColor(Color.LTGRAY, Color.GRAY)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#FFFFB300"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .build();

        Log.i("Default Date", DateFormat.format("dd/MM/yyyy", defaultSelectedDate).toString());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String selectedDateStr = DateFormat.format("dd/MM/yyyy", date).toString();
                String k = "" + taskList.size();
                Log.i("onDateSelected", selectedDateStr + " - Position = " + position);
                txtCurrentDate.setText(selectedDateStr);
                try {
                    simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dt = null;
                    try {
                        calendar = Calendar.getInstance();
                        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String d = txtCurrentDate.getText().toString();
                        dt = simpleDateFormat.parse(d);
                        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        tasksAdapter = new ToDoAdapter(db, MainActivity.this);
                        tasksRecyclerView.setAdapter(tasksAdapter);
                        tasksAdapter.notifyDataSetChanged();
                        ItemTouchHelper itemTouchHelper = new
                                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
                        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
                        taskList = db.byDate(dt);
                        tasksAdapter.setTasks(taskList);
                        updateTotal();
                    } catch (Exception e) {
                        dt = null;
                    }
                } catch (Exception e) {
                    date = null;
                }
            }
        });
//        Collections.sort(taskList, (o1, o2) -> {
//            if (Date.parse(o1.getDate()) == Date.parse(o2.getDate())) {
//                return o1.getTime().compareTo(o2.getTime());
//            }
//            if (Date.parse(o1.getDate()) > Date.parse(o2.getDate())) {
//                return o1.getDate().compareTo(o2.getDate());
//            }
//            return 0;
//        });




        //taskList = db.getAllTasks();
//        taskList = db.getTasksByDate(date);
//        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String z = day + "/" + month + "/" + year;
            date = simpleDateFormat.parse(z);
        } catch (Exception e) {
            date = null;
        }
        taskList = db.byDate(date);
        updateTotal();
//        Collections.reverse(taskList);

        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db, MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);
        tasksAdapter.notifyDataSetChanged();
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        tasksAdapter.setTasks(taskList);

        taskList = db.byDate(date);
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day > 10 || month < 10) {
            txtCurrentDate.setText(day + "/0" + month + "/" + year);
        } else {
            txtCurrentDate.setText("0" + day + "/" + month + "/" + year);
        }
//        fab = findViewById(R.id.btn_add);
//        fab.setOnClickListener(v -> {
//            AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
//        });
        animation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(this)) {
            RequestPermission();
        }
        List<ToDoModel> data = (tasksAdapter == null || tasksAdapter.getTodoList() == null) ? taskList : tasksAdapter.getTodoList();
        int cou = data.size();
        if(cou>0){
            ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.GONE);
        }else if(cou==0){
            ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.VISIBLE);
        }
    }

    private void RequestPermission() {
        // Check if Android M or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    public void updateTotal() {
        List<ToDoModel> data = (tasksAdapter == null || tasksAdapter.getTodoList() == null) ? taskList : tasksAdapter.getTodoList();
        int cou = data.size();
        int done = 0;
        for (ToDoModel t : data) {
            if (t.getStatus() == 1) {
                done++;
            }
        }
        txtTatca.setText("Tất cả: " + cou);
        txtDaht.setText("Đã hoàn thành: " + done);
        txtConlai.setText("Còn lại: " + (cou - done));
        load();
    }


    public void load() {
        if (tasksAdapter == null || tasksAdapter.getItemCount() == 0) {
            ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.VISIBLE);
        } else ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.GONE);
    }


    @Override
    public void handleDialogClose(DialogInterface dialog) {
        try {
            taskList = db.byDate(date);
//            taskList = db.getAllTasks();
            Collections.reverse(taskList);
            tasksAdapter.setTasks(taskList);
            tasksAdapter.notifyDataSetChanged();
            List<ToDoModel> data = (tasksAdapter == null || tasksAdapter.getTodoList() == null) ? taskList : tasksAdapter.getTodoList();
            int cou = data.size();
            if(cou>0){
                ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.GONE);
            }else if(cou==0){
                ((android.widget.ImageView) findViewById(R.id.nothing)).setVisibility(View.VISIBLE);
            }
            updateTotal();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}