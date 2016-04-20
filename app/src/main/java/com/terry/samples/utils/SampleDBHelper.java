package com.terry.samples.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.terry.samples.activity.MainActivity;
import com.terry.samples.model.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2016/4/19.
 */
public class SampleDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MainActivity.class.getSimpleName();


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "samples.db";

    public static final String TABLE_NAME = "person";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";

    private static SampleDBHelper sInstance;
    private Context mContext;

    public static synchronized SampleDBHelper getInstance(Context context) {
        // Use the application mContext, which will ensure that you
        // don't accidentally leak an Activity's mContext.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SampleDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public SampleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        Log.i(TAG, "onCreate");
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    public void deleteDatabase() {
        mContext.deleteDatabase(DATABASE_NAME);
    }

    public void insert(String name) {
        Log.i(TAG, "insert " + name);

        // Method 1
//        final SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COLUMN_NAME, name);
//        db.insert(TABLE_NAME, null, contentValues);
//        db.close();

        // Method 2
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, name);
            db.insert(TABLE_NAME, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public Person queryName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_NAME},
                COLUMN_NAME + "=?", new String[]{name}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Person person = new Person(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
        cursor.close();
        db.close();
        return person;
    }

    public int update(Person person, String newName) {
        Log.i(TAG, "update " + person.getName() + " to " + newName);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, newName);

        return db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ",
                new String[]{String.valueOf(person.getId())});
    }

    public int delete(Person person) {
        Log.i(TAG, "delete " + person.getName());

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ? ",
                new String[]{String.valueOf(person.getId())});
    }

    public List<Person> queryAll() {
        List<Person> result = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Person person = new Person(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
                result.add(person);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return result;
    }

    public int getCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.close();
        db.close();
        return cursor.getCount();
    }
}
