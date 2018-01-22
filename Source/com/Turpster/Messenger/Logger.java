package com.Turpster.Messenger;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Logger
{
    public void log(Level level, String message)
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();

        System.out.println("~ [" + dateFormat.format(date) + "] " + level + ": " + message);
    }
}
