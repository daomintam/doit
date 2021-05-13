package vn.edu.tdmu.mintam.doit.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spark.submitbutton.SubmitButton;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vn.edu.tdmu.mintam.doit.Adapters.ToDoAdapter;
import vn.edu.tdmu.mintam.doit.Helper.NotificationHelper;
import vn.edu.tdmu.mintam.doit.Listener.DialogCloseListener;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.Notification.AlertReceiver;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;
import vn.edu.tdmu.mintam.doit.alarm.AlarmService;
import vn.edu.tdmu.mintam.doit.alarm.AlarmWorker;


public class AddNewTask extends BottomSheetDialogFragment{

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private SubmitButton newTaskSaveButton;
    private Calendar calendar = Calendar.getInstance();

    private EditText edtTitle, edtSubTitle, edtLocation;
    private TextView edtDate,edtTime;
    private ImageButton btnSelectDate,btnSelectTime,backButton;
    private CheckBox cbForewarned;
    private AppCompatSpinner spType;
    private AppCompatSpinner spForewarned;
    int mHour,mMinute;
    private  Calendar calen = Calendar.getInstance();
    Locale id = new Locale("vi", "VN");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY",id);
    private DatabaseHandler db;

    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);
        edtTitle = Objects.requireNonNull(getView().findViewById(R.id.newTaskText));
        edtSubTitle = getView().findViewById(R.id.SubTitleEditText);
        edtLocation = getView().findViewById(R.id.LocationEditText);
        edtDate = getView().findViewById(R.id.DateEditText);
        edtTime = getView().findViewById(R.id.TimeEditText);
        btnSelectDate = getView().findViewById(R.id.SelectDateButton);
        btnSelectTime = getView().findViewById(R.id.SelectTimeButton);
        spType = getView().findViewById(R.id.sp_type);
        spForewarned = getView().findViewById(R.id.sp_forewarned);
        backButton=getView().findViewById(R.id.BackButton);
        cbForewarned = getView().findViewById(R.id.cb_forewarned);
        cbForewarned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    spForewarned.setVisibility(View.VISIBLE);
                } else {
                    spForewarned.setVisibility(View.GONE);
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
//            String title = bundle.getString("title");
            String subtitle = bundle.getString("subtitle");
            String location = bundle.getString("location");
            String date = bundle.getString("date");
            String time = bundle.getString("time");
            long timeStamp = bundle.getLong("time_stamp");
            calendar.setTimeInMillis(timeStamp);
            edtTitle.setText(task);
            edtSubTitle.setText(subtitle);
            edtLocation.setText(location);
            edtDate.setText(date);
            edtTime.setText(time);
            newTaskText.setText(task);
            assert task != null;
            if(task.length()>0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimaryDark));
        }

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                }
                else{
                    newTaskSaveButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        c.set(Calendar.SECOND,0);
                        if(minute<10){
                            edtTime.setText(hourOfDay+":0"+minute);
                        }else{
                            edtTime.setText(hourOfDay+":"+minute);
                        }
                    }
                }, mHour, mMinute, true);
                timePickerDialog.setTitle("CHỌN THỜI GIAN");
                timePickerDialog.setIcon(R.drawable.ic_time);
                timePickerDialog.show();
            }
        });
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calen.set(year,month,dayOfMonth);
                        calendar.set(year, month, dayOfMonth);
                        edtDate.setText(dateFormat.format(calen.getTime()));
                    }
                },calen.get(Calendar.YEAR),calen.get(Calendar.MONTH),calen.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.setTitle("CHỌN NGÀY");
//                datePickerDialog.setCustomTitle(");
            }
        });
        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(v -> {
            String title = edtTitle.getText().toString();
            String subtitle = edtSubTitle.getText().toString();
            String location = edtLocation.getText().toString();
            String date = edtDate.getText().toString();
            String time = edtTime.getText().toString();
            String text = newTaskText.getText().toString();
            String forewarned = spForewarned.getSelectedItem().toString();
            try {
                if(title==""){
                    edtTitle.setError("Tên công việc/hoạt động không  được để trống!");
                }
                if(finalIsUpdate){
                    db.updateTitle(bundle.getInt("id"),title);
                    db.updateTask(bundle.getInt("id"), text);
                    db.updateSubTitle(bundle.getInt("id"),subtitle);
                    db.updateLocation(bundle.getInt("id"),location);
                    db.updateDate(bundle.getInt("id"),date, calendar.getTime().getTime());
                    db.updateType(bundle.getInt("id"), spType.getSelectedItem().toString());
                    db.updateAlarmed(bundle.getInt("id"), 0);
                    if (cbForewarned.isChecked()) {
                        db.updateForewarned(bundle.getInt("id"), forewarned);
                    }
                    db.updateTime(bundle.getInt("id"),time);
                }
                else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setTitle(text);
                    task.setSubtitle(subtitle);
                    task.setLocation(location);
                    task.setDate(date);
                    task.setTime(time);
                    if (cbForewarned.isChecked()) {
                        task.setForewarned(forewarned);
                    } else {
                        task.setForewarned("0");
                    }
                    task.setType(spType.getSelectedItem().toString());
                    task.setTimeStamp(calendar.getTimeInMillis());
                    task.setStatus(0);
                    db.insertTask(task);
                }
                dismiss();
                AlarmService.startAlarm(getContext());
//                AlarmWorker.setWork(getContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog){
        Activity activity = getActivity();
        try{
            if(activity instanceof DialogCloseListener)
                ((DialogCloseListener)activity).handleDialogClose(dialog);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
