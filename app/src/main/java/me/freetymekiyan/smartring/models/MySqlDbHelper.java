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
            + PulseEntry.COLUMN_NAME_VALUE + " INTEGER, "
            + PulseEntry.COLUMN_NAME_STATE + " INTEGER, "
            + PulseEntry.COLUMN_NAME_MEASURED_DATE + " DATETIME DEFAULT CURRENT_DATE, "
            + PulseEntry.COLUMN_NAME_MEASURED_TIMESTAMP
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
        values.put(PulseEntry.COLUMN_NAME_VALUE, rate);
        values.put(PulseEntry.COLUMN_NAME_STATE, state);
        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(PulseEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
            Log.d("DEBUG", "addPulseRate failed");
        }
        return rowId;
    }

    public List<Pulse> getLast7Days() {
        // SELECT date, avg(value), state
        // FROM table_name
        // WHERE date >= date('now', 'weekday 0', '-7 days')
        // GROUP BY date, state
        List<Pulse> res = new ArrayList<Pulse>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                PulseEntry._ID,
                PulseEntry.COLUMN_NAME_VALUE,
                PulseEntry.COLUMN_NAME_STATE,
                PulseEntry.COLUMN_NAME_MEASURED_DATE,
        };
        String selection = "WHERE " + PulseEntry.COLUMN_NAME_MEASURED_DATE + " >= ?";
        String[] selectionArgs = new String[]{"DATE('now', 'weekday 0', '-7 days')"};
        String groupBy = PulseEntry.COLUMN_NAME_MEASURED_DATE;
        String orderBy = PulseEntry.COLUMN_NAME_MEASURED_DATE + " ASC";
        db.query(PulseEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy, null,
                orderBy);
        return res;
    }

    public String getAllPulses() {
        SQLiteDatabase db = getWritableDatabase();
        String sortOrder = PulseEntry.COLUMN_NAME_MEASURED_DATE + " ASC";

        Cursor c = db.query(PulseEntry.TABLE_NAME, null, null, null, null, null, sortOrder);
        c.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!c.isAfterLast()) {
            long id = c.getLong(c.getColumnIndexOrThrow(PulseEntry._ID));
            int rate = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COLUMN_NAME_VALUE));
            String state = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COLUMN_NAME_STATE)) == 0
                    ? "static" : "workout";
            String date = c
                    .getString(c.getColumnIndexOrThrow(PulseEntry.COLUMN_NAME_MEASURED_DATE));
            String time = c
                    .getString(c.getColumnIndexOrThrow(PulseEntry.COLUMN_NAME_MEASURED_TIMESTAMP));
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
        if (!c.isClosed()) {
            c.close();
        }
        return res.toString();
    }
}
