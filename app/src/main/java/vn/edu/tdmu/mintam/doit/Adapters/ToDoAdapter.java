package vn.edu.tdmu.mintam.doit.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.igenius.customcheckbox.CustomCheckBox;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import vn.edu.tdmu.mintam.doit.Activity.AddNewTask;
import vn.edu.tdmu.mintam.doit.Activity.MainActivity;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;
import vn.edu.tdmu.mintam.doit.alarm.AlarmService;
import vn.edu.tdmu.mintam.doit.alarm.AlarmWorker;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private FragmentActivity activity;
    Locale id=new Locale("vi","VN");
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", id);
    SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
    Calendar calendar;

    public ToDoAdapter(DatabaseHandler db, FragmentActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    public List<ToDoModel> getTodoList() {
        return todoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ToDoModel item = todoList.get(position);
        holder.txtTitle.setText(item.getTask());
        holder.txtSubTitle.setText(item.getSubtitle());
        holder.txtLocation.setText(item.getLocation());
        holder.txtDate.setText(item.getDate());
        holder.txtTime.setText(item.getTime());
        holder.check.setChecked(toBoolean(item.getStatus()));
        String[] types = getContext().getResources().getStringArray(R.array.type);
        String[] colors = getContext().getResources().getStringArray(R.array.colors);
        int color = Color.parseColor(colors[Arrays.asList(types).indexOf(item.getType())]);
        holder.colorType.setBackgroundColor(color);

//        final MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.sam);
        holder.check.setOnCheckedChangeListener(new CustomCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
                db.openDatabase();
                if(isChecked){
                    item.setStatus(1);
                    db.updateStatus(item.getId(), 1);
                    Toast.makeText(getContext(), "Hoàn thành", Toast.LENGTH_SHORT).show();
                    ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setTextColor(Color.GRAY);
                    ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//                    ((android.widget.ImageView) holder.itemView.findViewById(R.id.BackgroundTimetable)).setImageResource(R.drawable.it_background);
                }else{
                    item.setStatus(0);
                    db.updateStatus(item.getId(), 0);
                    ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setPaintFlags(Paint.END_HYPHEN_EDIT_NO_EDIT);
//                    ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setTextColor(Color.parseColor("#B5000000"));
//                    ((android.widget.ImageView) holder.itemView.findViewById(R.id.BackgroundTimetable)).setImageResource(R.drawable.item_background);
                }
                ((MainActivity) activity).updateTotal();
                AlarmService.startAlarm(getContext());
                db.close();
            }
        });
        if(item.getDate().trim().equals("")){
            ((android.widget.TextView)holder.itemView.findViewById(R.id.DateTextview)).setVisibility(View.GONE);
        }
        if(item.getLocation().trim().equals("")){
            ((android.widget.TextView)holder.itemView.findViewById(R.id.LocationTextview)).setVisibility(View.GONE);
        }
        if(item.getStatus()==1){
            ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setTextColor(Color.GRAY);
            ((android.widget.TextView)holder.itemView.findViewById(R.id.TimeTextview)).setTextColor(Color.GRAY);
            ((android.widget.TextView)holder.itemView.findViewById(R.id.LocationTextview)).setTextColor(Color.GRAY);
            ((android.widget.TextView)holder.itemView.findViewById(R.id.SubTitleTextview)).setTextColor(Color.GRAY);
            ((android.widget.TextView)holder.itemView.findViewById(R.id.DateTextview)).setTextColor(Color.GRAY);

//            ((android.widget.TextView)holder.itemView.findViewById(R.id.TimeTextview)).setTextColor(Color.GRAY);
            ((android.widget.TextView)holder.itemView.findViewById(R.id.TitleTextview)).setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
//            ((android.widget.ImageView) holder.itemView.findViewById(R.id.BackgroundTimetable)).setImageResource(R.drawable.it_background);
        }



//        Collections.sort(todoList, new Comparator<ToDoModel>() {
//            @Override
//            public int compare(ToDoModel o1, ToDoModel o2) {
////                do{
////                    if(o1.getTime()==null||o2.getTime()==null){
////                        return 0;
////                    }return o1.getTime().compareTo(o2.getTime());
////                }while (o1.getDate()==o2.getDate());
//                if(Date.parse(o1.getDate().toString())<Date.parse(o2.getDate().toString())){
//                    return o1.getDate().compareTo(o2.getDate());
//                }else return 0;
//            }
//        });
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        if(todoList!=null){
            return todoList.size();
        }
        return 0;
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyDataSetChanged();
        AlarmService.startAlarm(getContext());
        ((MainActivity) activity).updateTotal();
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putLong("time_stamp", item.getTimeStamp());
        bundle.putString("title", item.getTitle());
        bundle.putString("subtitle",item.getSubtitle());
        bundle.putString("location",item.getLocation());
        bundle.putString("date",item.getDate());
        bundle.putString("time",item.getTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task; CustomCheckBox check;
        TextView txtTitle,txtSubTitle,txtLocation,txtDate,txtTime;
        View colorType;
        ViewHolder(View view) {
            super(view);
//            task = view.findViewById(R.id.todoCheckbox);
            txtTitle = view.findViewById(R.id.TitleTextview);
            txtSubTitle = view.findViewById(R.id.SubTitleTextview);
            txtLocation = view.findViewById(R.id.LocationTextview);
            txtDate = view.findViewById(R.id.DateTextview);
            txtTime = view.findViewById(R.id.TimeTextview);
            check = view.findViewById(R.id.checkCheck);
            colorType = view.findViewById(R.id.view_color_type);
        }
    }
}
