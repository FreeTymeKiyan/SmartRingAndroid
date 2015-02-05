package me.freetymekiyan.smartring.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kiyan on 2/3/15.
 */
public class Pulse {

    public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd";

    public enum State {
        REST,
        ACTIVE
    }

    int value;

    Date date;

    State state;

    public Pulse() {

    }

    public Pulse(int v, String dateStr, State s) {
        this.value = v;
        DateFormat format = new SimpleDateFormat(SQLITE_DATE_FORMAT, Locale.ENGLISH);
        try {
            this.date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.state = s;
    }

    public Pulse(int v, Date d, State s) {
        this.value = v;
        this.date = d;
        this.state = s;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Pulse) {
            Pulse toCompare = (Pulse) o;
            return this.date.compareTo(toCompare.getDate()) == 0;
        }
        return false;
    }
}
