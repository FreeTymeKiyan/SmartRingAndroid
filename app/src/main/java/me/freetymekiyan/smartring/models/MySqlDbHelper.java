package me.freetymekiyan.smartring.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.freetymekiyan.smartring.models.PulseContract.PulseEntry;

/**
 * Created by Kiyan on 2/2/15.
 */
public class MySqlDbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "SmartRing.db";

    public static final int VER = 2;

    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + PulseEntry.TABLE_NAME + "("
            + PulseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PulseEntry.COL_NAME_VAL + " INTEGER, "
            + PulseEntry.COL_NAME_STATE + " INTEGER, "
            + PulseEntry.COL_NAME_MEASURED_DATE + " DATETIME DEFAULT CURRENT_DATE, "
            + PulseEntry.COL_NAME_MEASURED_TIMESTAMP
            + " DATETIME DEFAULT (datetime('now','localtime'))" +
            ")";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + PulseEntry.TABLE_NAME;


    public MySqlDbHelper(Context context) {
        super(context, DB_NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DEBUG", SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DEBUG", SQL_DELETE_TABLE);
        db.execSQL(SQL_DELETE_TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addPulseRate(int rate, int state) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PulseEntry.COL_NAME_VAL, rate);
        values.put(PulseEntry.COL_NAME_STATE, state);
        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(PulseEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            Log.d("DEBUG", "addPulseRate failed");
        }
        return rowId;
    }

    public List<Pulse> getLast7Days() {
        List<Pulse> res = new ArrayList<Pulse>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                PulseEntry.COL_NAME_MEASURED_DATE,
                "AVG(" + PulseEntry.COL_NAME_VAL + ")" + " AS " + PulseEntry.COL_NAME_AVG_VAL,
        };
        String selection = PulseEntry.COL_NAME_STATE + "=?";
        String[] selectionArgs = new String[]{"0"};
        String groupBy = PulseEntry.COL_NAME_MEASURED_DATE;
        String having = PulseEntry.COL_NAME_MEASURED_DATE
                + " >= DATE('now', 'weekday 0', '-7 days')";
        String orderBy = PulseEntry.COL_NAME_MEASURED_DATE + " ASC";

        Cursor c = db.query(PulseEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy,
                having, orderBy);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String date = c
                    .getString(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_MEASURED_DATE));
            int value = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_AVG_VAL));
            Pulse p = new Pulse(value, date, Pulse.State.REST);
            res.add(p);
            c.moveToNext();
        }
        return res;
    }

    public String getAllPulses() {
        SQLiteDatabase db = getWritableDatabase();
        String sortOrder = PulseEntry.COL_NAME_MEASURED_DATE + " ASC";

        Cursor c = db.query(PulseEntry.TABLE_NAME, null, null, null, null, null, sortOrder);
        c.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!c.isAfterLast()) {
            long id = c.getLong(c.getColumnIndexOrThrow(PulseEntry._ID));
            int rate = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_VAL));
            String state = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_STATE)) == 0
                    ? "static" : "workout";
            String date = c
                    .getString(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_MEASURED_DATE));
            String time = c
                    .getString(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_MEASURED_TIMESTAMP));
            res.append(id);
            res.append('\t');
            res.append(rate);
            res.append('\t');
            res.append(state);
            res.append('\t');
            res.append(date);
            res.append('\t');
            res.append(time);
            res.append('\t');
            res.append('\n');
            c.moveToNext();
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        return res.toString();
    }
}
