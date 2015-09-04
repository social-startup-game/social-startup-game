package edu.bsu.cybersec.core;

public interface PlatformSpecificDateFormatter {
    long now();

    String format(long ms);
}
