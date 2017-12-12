package com.example.kayhan.automation.Database;

/**
 * Created by Md. Abdur Rahim on 06-Apr-16.
 */
public class Constants {
    public static String DB_NAME = "Error_Efficiency";
    public static int DB_VERSION = 1;

    public static String ID = "id";
    public static String KILOWATT = "kilowatt";
    public static String CALENDAR = "calendar";

    public static String LOG_TABLE_NAME = "log";

    public static String CREATE_LOG_TABLE = "CREATE TABLE " + LOG_TABLE_NAME+ "("+
            ID+" INTEGER PRIMARY KEY, "+
            KILOWATT + " double NOT NULL,"+
            ")";

}
