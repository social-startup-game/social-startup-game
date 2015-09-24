package edu.bsu.cybersec.java;

import edu.bsu.cybersec.core.ui.PlatformSpecificDateFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Java7DateFormatter implements PlatformSpecificDateFormatter {

    private final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss aaa");

    public Java7DateFormatter() {
        final GregorianCalendar now = new GregorianCalendar();
        format.setCalendar(now);
    }

    @Override
    public String format(long ms) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(ms);
        return format.format(c.getTime());
    }
}