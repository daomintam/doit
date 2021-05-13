package vn.edu.tdmu.mintam.doit.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import vn.edu.tdmu.mintam.doit.Model.ToDoModel;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    private static final String NAME = "data";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String SUBTITLE = "subtitle";
    private static final String LOCATION = "location";
    private static final String DATE = "date";
    private static final String DATE_LONG = "date_long";
    private static final String TIME = "time";
    private static final String TYPE = "type";
    private static final String FOREWARNED = "forewarned";
    private static final String ALARMED = "alarmed";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, "
            + SUBTITLE + " TEXT, "
            + LOCATION + " TEXT, "
            + DATE + " TEXT, "
            + DATE_LONG + " INTEGER, "
            + TIME + " TEXT, "
            + TYPE + " TEXT, "
            + FOREWARNED + " TEXT, "
            + ALARMED + " INTEGER, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

//    public DatabaseHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, NAME, null, VERSION);
//    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(TYPE, task.getType());
        cv.put(SUBTITLE, task.getSubtitle());
        cv.put(LOCATION, task.getLocation());
        cv.put(DATE, task.getDate());
        cv.put(DATE_LONG, task.getTimeStamp());
        cv.put(TIME, task.getTime());
        cv.put(STATUS, 0);
        cv.put(ALARMED, 0);
        cv.put(FOREWARNED, task.getForewarned());
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> byDate(Date date){
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TODO_TABLE + " where " + DATE + " = ? ", new String[]{simpleDateFormat.format(date)});
            return parseCursor(cursor);
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ToDoModel> byDate(Date startDate, Date endDate){
        try{
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
//            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TODO_TABLE + " where " + DATE_LONG + " >= ? AND " + DATE_LONG + " <= ?", new String[]{startDate.getTime() + "", endDate.getTime() + ""});
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TODO_TABLE + " WHERE " + DATE_LONG + " >= " + startDate.getTime() + " AND " + DATE_LONG + " <= " + endDate.getTime(), null);
            return parseCursor(cursor);
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<ToDoModel> getFeatureTask(Date startDate){
        try{
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TODO_TABLE + " WHERE " + DATE_LONG + " >= " + startDate.getTime() + " AND "+ ALARMED +" == 0 AND "+ STATUS +" == 0 order by " + DATE_LONG + " ASC", null);
            return parseCursor(cursor);
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<ToDoModel> parseCursor(Cursor cursor) {
        List<ToDoModel> works = new ArrayList<ToDoModel>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ToDoModel toDoModel = new ToDoModel();
            toDoModel.setId(cursor.getInt(cursor.getColumnIndex(ID)));
            toDoModel.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
            toDoModel.setTimeStamp(cursor.getLong(cursor.getColumnIndex(DATE_LONG)));
            toDoModel.setSubtitle(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
            toDoModel.setLocation(cursor.getString(cursor.getColumnIndex(LOCATION)));
            toDoModel.setForewarned(cursor.getString(cursor.getColumnIndex(FOREWARNED)));
            toDoModel.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
            toDoModel.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
            toDoModel.setStatus(cursor.getInt(cursor.getColumnIndex(STATUS)));
            toDoModel.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
            works.add(toDoModel);
            cursor.moveToNext();
        }
        return works;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateType(int id, String type){
        ContentValues cv = new ContentValues();
        cv.put(TYPE, type);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTitle(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, title);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateSubTitle(int id, String stitle) {
        ContentValues cv = new ContentValues();
        cv.put(SUBTITLE, stitle);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateForewarned(int id, String forewarned) {
        ContentValues cv = new ContentValues();
        cv.put(FOREWARNED, forewarned);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateLocation(int id, String location) {
        ContentValues cv = new ContentValues();
        cv.put(LOCATION, location);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateDate(int id, String date, long timestamp) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(DATE_LONG, timestamp);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateTime(int id, String time) {
        ContentValues cv = new ContentValues();
        cv.put(TIME, time);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateAlarmed(int id, int alarmed) {
        ContentValues cv = new ContentValues();
        cv.put(ALARMED, alarmed);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        openDatabase();
        db.delete(TODO_TABLE, ID + "= ?", new String[] {String.valueOf(id)});
    }
}