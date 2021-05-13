package vn.edu.tdmu.mintam.doit.Model;

import java.io.Serializable;

public class ToDoModel implements Serializable, Comparable<ToDoModel>{
    private int id;
    private String title;
    private String subtitle;
    private String location;
    private int status;
    private String time;
    private String date;
    private long timeStamp;
    private String task;
    private String type;
    private String forewarned;

    public ToDoModel() {
    }

    public String getForewarned() {
        return forewarned;
    }

    public void setForewarned(String forewarned) {
        this.forewarned = forewarned;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public int compareTo(ToDoModel o) {
//        return getDate().compareTo(o.getDate());
        if (getDate() == null || o.getDate() == null)
            return 0;
        return getDate().compareTo(o.getDate());
    }
}