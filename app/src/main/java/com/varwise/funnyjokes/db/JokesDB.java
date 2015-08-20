package com.varwise.funnyjokes.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class JokesDB extends SQLiteAssetHelper {
    private Context context;
    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "jokes.db";
    private static final int DATABASE_VERSION = 1;

    public JokesDB(Context context) {
        this(context, DATABASE_NAME, null, 1);
    }

    private JokesDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    public Cursor getAllCategories() {
        return db.query("category", new String[]{"_id", "name"}, null, null, null, null, null);
    }

    public Cursor getAllJokesInCategoryById(int categoryId){
        return db.query("joke", new String[] {"_id", "content" , "categoryId"}, "categoryId="+categoryId, null, null, null, null);
    }
}
