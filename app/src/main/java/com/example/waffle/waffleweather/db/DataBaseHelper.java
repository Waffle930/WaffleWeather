package com.example.waffle.waffleweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Waffle on 2016/2/21.
 */
public class DataBaseHelper extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.example.waffle.waffleweather/databases/CityList";

    private static String DB_NAME = "CityList";

    private SQLiteDatabase database;

    private final Context mcontext;

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mcontext = context;
    }

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(!dbExist){
            this.getWritableDatabase();
            copyDataBase();
        }
    }

    private void copyDataBase() throws IOException{
        InputStream input = mcontext.getAssets().open(DB_NAME);
        OutputStream out = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while((length = input.read(buffer))>0){
            out.write(buffer,0,length);
        }
        out.flush();
        out.close();
        input.close();
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        checkDB = SQLiteDatabase.openDatabase(DB_PATH,null,SQLiteDatabase.OPEN_READONLY);
        if(checkDB != null){
            checkDB.close();
            return true;
        } else {
            return false;
        }
    }

    public void openDatabase(){
        database = SQLiteDatabase.openDatabase(DB_PATH,null,SQLiteDatabase.OPEN_READONLY);
    }

    public synchronized void close(){
        if(database!=null){
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
