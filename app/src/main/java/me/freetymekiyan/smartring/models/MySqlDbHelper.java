package me.freetymekiyan.smartring.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
            + PulseEntry.COL_NAME_MEASURED_DATE + " DATETIME DEFAULT (date('now','localtime')), "
            + PulseEntry.COL_NAME_MEASURED_TIMESTAMP
            + " DATETIME DEFAULT (datetime('now','localtime'))" +
            ")";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + PulseEntry.TABLE_NAME;

    public MySqlDbHelper(Context context) {
        super(context, DB_NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d("DEBUG", SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.d("DEBUG", SQL_DELETE_TABLE);
        db.execSQL(SQL_DELETE_TABLE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long addPulseRate(int rate, int state) {
        return addPulseRateWithDate(rate, state, null);
    }

    public long addPulseRateWithDate(int rate, int state, Date date) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PulseEntry.COL_NAME_VAL, rate);
        values.put(PulseEntry.COL_NAME_STATE, state);
        if (date != null) {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            values.put(PulseEntry.COL_NAME_MEASURED_DATE, df.format(date));
            final SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            values.put(PulseEntry.COL_NAME_MEASURED_TIMESTAMP, df2.format(date));
        }
        // Insert the new row, returning the primary key value of the new row
        long rowId = db.insert(PulseEntry.TABLE_NAME, null, values);
        if (rowId == -1) {
//            Log.d("DEBUG", "addPulseRate failed");
        }
        return rowId;
    }

    public List<Pulse> getLast7DaysRest() {
        return getLast7Days(Pulse.State.REST);
    }

    public List<Pulse> getLast7DaysActive() {
        return getLast7Days(Pulse.State.ACTIVE);
    }

    public List<Pulse> getLast7Days(Pulse.State s) {
        List<Pulse> res = new ArrayList<Pulse>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                PulseEntry.COL_NAME_MEASURED_DATE,
                "AVG(" + PulseEntry.COL_NAME_VAL + ")" + " AS " + PulseEntry.COL_NAME_AVG_VAL,
        };
        String selection = PulseEntry.COL_NAME_STATE + "=?";
        String[] selectionArgs = new String[]{s.ordinal() + ""};
        String groupBy = PulseEntry.COL_NAME_MEASURED_DATE;
        String having = PulseEntry.COL_NAME_MEASURED_DATE
                + " >= DATE('now', '-7 days')";
        String orderBy = PulseEntry.COL_NAME_MEASURED_DATE + " ASC";

        Cursor c = db.query(PulseEntry.TABLE_NAME, projection, selection, selectionArgs, groupBy,
                having, orderBy);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String date = c.getString(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_MEASURED_DATE));
            int value = c.getInt(c.getColumnIndexOrThrow(PulseEntry.COL_NAME_AVG_VAL));
//            Log.d("DEBUG", "date: " + date);
            Pulse p = new Pulse(value, date, Pulse.State.REST);
            res.add(p);
            c.moveToNext();
        }
        if (c != null && !c.isClosed()) {
            c.close();
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

    public void addLast7Days() {
        final Random r = new Random();
        final Calendar c = Calendar.getInstance();
        final Calendar c2 = Calendar.getInstance();
        c.clear();
        c.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DATE, -6);
        for (int i = 0; i < 7; i++) {
            int rate = 50 + r.nextInt(41);
            addPulseRateWithDate(rate, Pulse.State.REST.ordinal(), c.getTime());
            rate = 120 + r.nextInt(41);
            addPulseRateWithDate(rate, Pulse.State.ACTIVE.ordinal(), c.getTime());
            c.add(Calendar.DATE, 1);
        }
    }
}
