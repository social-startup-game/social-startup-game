package edu.bsu.cybersec.html;

import com.google.gwt.core.client.JsDate;
import com.google.gwt.i18n.client.DateTimeFormat;
import edu.bsu.cybersec.core.PlatformSpecificDateFormatter;

public class GWTDateFormatter implements PlatformSpecificDateFormatter {
    private final DateTimeFormat format = DateTimeFormat.getFormat("EEE, d MMM yyyy hh:mm:ss aaa");

    @Override
    public long now() {
        return (long) JsDate.now();
    }

    @Override
    public String format(long ms) {
        java.util.Date javaDate = new java.util.Date(ms);
        return format.format(javaDate);
    }
}
