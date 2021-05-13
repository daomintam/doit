package vn.edu.tdmu.mintam.doit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import vn.edu.tdmu.mintam.doit.Activity.CalendarActivity;
import vn.edu.tdmu.mintam.doit.Model.ToDoModel;
import vn.edu.tdmu.mintam.doit.R;
import vn.edu.tdmu.mintam.doit.Utils.DatabaseHandler;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private List<ToDoModel> todoList;
    private DatabaseHandler db;
    private CalendarActivity activity;
    Locale id=new Locale("vi","VN");
    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy", id);
    SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
    Calendar calendar;

    public EventAdapter(DatabaseHandler db, CalendarActivity activity) {
        this.db = db;
        this.activity = activity;
    }
    public List<ToDoModel> getTodoList() {
        return todoList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        db.openDatabase();
        final ToDoModel item = todoList.get(position);
        holder.EventTitle.setText(item.getTask());
        holder.EventLocation.setText(item.getLocation());
        holder.EventDate.setText(item.getDate());
        holder.EventTime.setText(item.getTime());
    }
    public Context getContext() {
        return activity;
    }
    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if(todoList!=null){
            return todoList.size();
        }
        return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView EventTitle,EventDate,EventLocation,EventTime;

        ViewHolder(View view) {
            super(view);
            EventTitle = view.findViewById(R.id.EventTitle);
            EventTime = view.findViewById(R.id.EventTime);
            EventDate = view.findViewById(R.id.EventDate);
            EventLocation = view.findViewById(R.id.EventLocation);

        }
    }
}
