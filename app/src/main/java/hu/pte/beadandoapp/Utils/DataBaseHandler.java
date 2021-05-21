package hu.pte.beadandoapp.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hu.pte.beadandoapp.Model.ToDoModel;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String CREATEDATE = "create_date";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, " + STATUS + " INTEGER, " + DESCRIPTION + " TEXT, " + CREATEDATE + " TEXT)";

    private SQLiteDatabase db;

    public DataBaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(DESCRIPTION, task.getDescription());

        DateTimeFormatter drf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate now = LocalDate.now();

        cv.put(CREATEDATE, drf.format(now).toString());
        db.insert(TODO_TABLE, null, cv);
    }

    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try{
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur != null){
                if(cur.moveToFirst()){
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setDescription(cur.getString(cur.getColumnIndex(DESCRIPTION)));
                        task.setCreateDate(cur.getString(cur.getColumnIndex(CREATEDATE)));

                        taskList.add(task);
                    }
                    while (cur.moveToNext());
                }
            }
        }
        finally {
            db.endTransaction();
            cur.close();
        }

        return taskList;
    }

    public ToDoModel findById(String id){
        SQLiteDatabase finder = getReadableDatabase();
        String where = ID + " =? ";
        String[] whereArgs = {id};
        @SuppressLint("Recycle") Cursor cursor = db.query(TODO_TABLE, null, where, whereArgs, null, null, null);
        ToDoModel model = null;
        try{
            if (cursor.moveToFirst()){
                model = new ToDoModel();
                model.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                model.setTask(cursor.getString(cursor.getColumnIndex(TASK)));
                model.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                model.setCreateDate(cursor.getString(cursor.getColumnIndex(CREATEDATE)));
            }
        }
        finally {
            cursor.close();
        }

        return model;
    }

    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "=?", new String[] { String.valueOf(id) });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateTask(int id, String task, String description){
        ContentValues cv = new ContentValues();
        DateTimeFormatter drf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate now = LocalDate.now();

        cv.put(TASK, task);
        cv.put(DESCRIPTION, description);
        cv.put(CREATEDATE, drf.format(now).toString());
        db.update(TODO_TABLE, cv, ID + "=?", new String[] { String.valueOf(id) });
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "=?", new String[] { String.valueOf(id) });
    }

}
