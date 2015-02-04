package me.freetymekiyan.smartring.models;

import java.util.Date;

/**
 * Created by Kiyan on 2/3/15.
 */
public class Pulse {

    int value;

    Date date;

    public Pulse() {

    }

    public Pulse(int v, Date d) {
        this.value = value;
        this.date = d;
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
}
