package com.intel.most.tools.mobibench;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "history_db";
    public static int DATABASE_VERSION = 1;

    public static class HistoryColumns implements BaseColumns {
        public static final String TABLE_NAME = "history";
        //_id integer, date text, type text, has_result integer, act text, io text, idl text, ct_total text, ct_vol text, thrp text, exp_name text not null
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_ACTION = "act";
        public static final String COLUMN_IO = "io";
        public static final String COLUMN_IDL = "idl";
        public static final String COLUMN_CT_TOTAL = "ct_total";
        public static final String COLUMN_CT_VOL = "ct_vol";
        public static final String COLUMN_THRP = "thrp";
        public static final String COLUMN_EXP_NAME = "exp_name";
        public static final String COLUMN_EXP_ID = "exp_id";
    }

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + HistoryColumns.TABLE_NAME + " ("
                    + HistoryColumns._ID + " INTEGER,"
                    + HistoryColumns.COLUMN_DATE + " TEXT,"
                    + HistoryColumns.COLUMN_TYPE + " TEXT,"
                    + HistoryColumns.COLUMN_ACTION + " TEXT,"
                    + HistoryColumns.COLUMN_IO + " TEXT,"
                    + HistoryColumns.COLUMN_IDL + " TEXT,"
                    + HistoryColumns.COLUMN_CT_TOTAL + " TEXT,"
                    + HistoryColumns.COLUMN_CT_VOL + " TEXT,"
                    + HistoryColumns.COLUMN_THRP + " TEXT,"
                    + HistoryColumns.COLUMN_EXP_NAME + " TEXT NOT NULL,"
                    + HistoryColumns.COLUMN_EXP_ID + " INTEGER"
                    + ")";

    private static final String SQL_DELETE_HISTORY =
            "DROP TABLE IF EXISTS " + HistoryColumns.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_HISTORY);
        onCreate(sqLiteDatabase);
    }
}
