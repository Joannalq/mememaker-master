package com.teamtreehouse.mememaker.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class MemeSQLiteHelper extends SQLiteOpenHelper{
    private static final String DB_NAME="memes.db";
    private static final int DB_VERSION=2;

    public static final String MEMES_TAB="memes";
    public static final String COLUMN_MEME_ASSET="asset";
    public static final String COLUMN_MEME_NAME="names";
    public static final String COLUMN_MEME_CREATE_DATE="CREATE_DATE";

    private static String CREATE_MEMES=
            "CREATE TABLE " + MEMES_TAB + "("
                    + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MEME_ASSET +" TEXT," +
                    COLUMN_MEME_NAME + " TEXT,"+
                    COLUMN_MEME_CREATE_DATE+"INTEGER)";


    public static final String ANNOTATIONS_TABLE = "ANNOTATIONS";
    public static final String COLUMN_ANNOTATION_COLOR = "COLOR";
    public static final String COLUMN_ANNOTATION_X = "X";
    public static final String COLUMN_ANNOTATION_Y = "Y";
    public static final String COLUMN_ANNOTATION_TITLE = "TITLE";
    public static final String COLUMN_FOREIGN_KEY_MEME = "MEME_ID";

    private static final String CREATE_ANNOTATIONS = "CREATE TABLE " + ANNOTATIONS_TABLE + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_ANNOTATION_X + " INTEGER, " +
            COLUMN_ANNOTATION_Y + " INTEGER, " +
            COLUMN_ANNOTATION_TITLE + " TEXT, " +
            COLUMN_ANNOTATION_COLOR + " TEXT, " +
            COLUMN_FOREIGN_KEY_MEME + " INTEGER, " +
            " FOREIGN KEY(" + COLUMN_FOREIGN_KEY_MEME + ") REFERENCES MEMES(_ID))";

    public MemeSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_MEMES);
        sqLiteDatabase.execSQL(CREATE_ANNOTATIONS);
    }

    private static final String ALTER_ADD_CREATE_DATE="ALTER TABLE "+ MEMES_TAB+
            " ADD COLUMN "+COLUMN_MEME_CREATE_DATE+" INTEGER";
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        switch (oldVersion){
            case 1:
                sqLiteDatabase.execSQL(ALTER_ADD_CREATE_DATE);
        }
    }
    //Meme Table functionality

    //Meme Table Annotations functionality

}
