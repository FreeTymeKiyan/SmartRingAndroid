package me.freetymekiyan.smartring.controllers;

/**
 * Created by Kiyan on 2/9/15.
 */
public class PrefChangedEvent {

    public final String newValue;

    public final int prefKey;

    public PrefChangedEvent(String newValue, int prefKey) {
        this.newValue = newValue;
        this.prefKey = prefKey;
    }
}
