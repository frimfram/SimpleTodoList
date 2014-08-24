package simpletodolist.codepath.jsrmobile.com.simpletodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class TodoDbData {
    private static final String TAG = TodoDbData.class.getSimpleName();

    static final String DB_NAME = "todo.db"; //
    static final int DB_VERSION = 1; //
    static final String TABLE = "TodoItems"; //
    static final String C_ID = BaseColumns._ID;

    public static final String KEY_TEXT = "ctext";
    public static final String KEY_PRIORITY = "cpriority";
    public static final String KEY_DUEDATE = "cduedate";


    class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            StringBuilder sql = new StringBuilder();
            sql.append("create table if not exists ");
            sql.append(TABLE + " (" + C_ID);
            sql.append(" integer PRIMARY KEY autoincrement , ");
            sql.append("ctext text, " +
                    "cpriority text, " +
                    "cduedate text)");
            db.execSQL(sql.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE);
            onCreate(db);
        }
    }

    private final DbHelper dbHelper;

    public TodoDbData(Context context) {
        this.dbHelper = new DbHelper(context);
        Log.i(TAG, "TodoDbData initialized");
    }

    public void close() {
        this.dbHelper.close();
    }

    public void insertOrIgnore(ContentValues values) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void update(ContentValues values) {
        String id = values.getAsString(TodoDbData.C_ID);
        if(id == null) return;

        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.update(TABLE, values, "_id = ?", new String[] {id});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public boolean delete(String id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        int result = 0;
        try {
            db.beginTransaction();
            result = db.delete(TABLE, "_id = ?", new String[] {id});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return result > 0;
    }

    public Cursor fetchAllTodoItems() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        return db.query(TABLE, null, null, null, null, null, null);
    }

    public int fetchRecordCount() {
        int result = 0;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor dataCount = db.rawQuery("select count(*) from " + TABLE, null);
        dataCount.moveToFirst();
        result = dataCount.getInt(0);
        dataCount.close();
        return result;
    }

    public void deleteAllTodoItems() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(TABLE, "1", null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

}

