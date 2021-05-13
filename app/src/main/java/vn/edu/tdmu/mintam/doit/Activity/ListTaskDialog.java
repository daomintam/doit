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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.spark.submitbutton.SubmitButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


public class ListTaskDialog extends BottomSheetDialogFragment{

    public static ListTaskDialog newInstance(){
        return new ListTaskDialog();
    }
    private ArrayList<ToDoModel> tasks;
    private OnDialogDismissListener listener;
    private RecyclerView lvTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.task_dialog, container, false);
        view.findViewById(R.id.BackButton).setOnClickListener(view1 -> {
            dismiss();
        });
        lvTask = view.findViewById(R.id.list_task);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToDoAdapter adapter = new ToDoAdapter(new DatabaseHandler(getContext()), getActivity());
        adapter.setTasks(tasks);
        lvTask.setAdapter(adapter);
    }

    public void show(FragmentManager manager, ArrayList<ToDoModel> tasks, OnDialogDismissListener listener) {
        this.tasks = tasks;
        this.listener = listener;
        show(manager, getClass().getName());
    }

    @Override
    public void dismiss() {
        super.dismiss();
        listener.onDismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        listener.onDismiss();
    }

    public interface OnDialogDismissListener {
        void onDismiss();
    }
}
