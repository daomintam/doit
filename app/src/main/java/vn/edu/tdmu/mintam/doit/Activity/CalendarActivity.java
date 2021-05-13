package vn.edu.tdmu.mintam.doit.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import vn.edu.tdmu.mintam.doit.Adapters.EventAdapter;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;


public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView txtTimetable, txtCount;
    private static List<ToDoModel> taskList;
    private DatabaseHandler db;
    private EventAdapter eventAdapter;
    SimpleDateFormat simpleDateFormat;
    RecyclerView EventRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_calendar);
        getSupportActionBar().hide();


        db = new DatabaseHandler(this);
        db.openDatabase();
        txtCount = findViewById(R.id.txtCountEvent);
        txtTimetable = (TextView) findViewById(R.id.TimetableTextview);
        EventRecyclerView = findViewById(R.id.calendarEventShow);
        calendarView = findViewById(R.id.CalendarView);
        findViewById(R.id.BackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        EventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(db, CalendarActivity.this);
        EventRecyclerView.setAdapter(eventAdapter);
        showEvent(calendarView.getDate());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                showEvent(c.getTimeInMillis());
                update();
            }
        });
        update();

    }

    private void update() {
        List<ToDoModel> data = (eventAdapter == null || eventAdapter.getTodoList() == null) ? taskList : eventAdapter.getTodoList();
        int cou = data.size();
        if(cou ==0){
            txtCount.setText(""+0);
        }
        else{
            txtCount.setText(""+cou);
        }

    }

    private void showEvent(Long date) {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String d = simpleDateFormat.format(date);
        txtTimetable.setText(d);
        try {
            taskList =db.byDate(new Date(date));
            eventAdapter.setTasks(taskList);
        } catch (Exception e) {
        }
    }
}