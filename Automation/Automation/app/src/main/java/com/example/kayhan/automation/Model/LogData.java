package com.example.kayhan.automation.Model;

import java.util.Calendar;

/**
 * Created by Md. Abdur Rahim on 06-Apr-16.
 */
public class LogData {
    private int logId;
    private double kiloWatt;
    private Calendar calendar;

    public LogData(double kiloWatt, Calendar calendar) {
        this.kiloWatt = kiloWatt;
        this.calendar = calendar;
    }

    public void setKiloWatt(double kiloWatt) {
        this.kiloWatt = kiloWatt;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getLogId() {
        return logId;
    }

    public double getKiloWatt() {
        return kiloWatt;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
