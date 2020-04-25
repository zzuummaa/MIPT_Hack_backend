package ru.zuma.mipthack.utils;

import java.time.LocalTime;

public class TimeConverter {
    public static LocalTime fromMS(int ms) {
        int secs = ms / 1000;
        return LocalTime.of(secs / 60 / 60 % 24, secs / 60 % 60, secs % 60);
    }

    public static LocalTime fromMS(long ms) {
        return fromMS((int)ms);
    }
}
