package ru.cardiacare.cardiacare.idt_ecg.common;

import java.util.TimeZone;

public class DateTimeUtl {
    public static String getCurrentUTCOffset() {
        int offset = TimeZone.getDefault().getRawOffset();
        String str = "%s%02d%02d";
        Object[] objArr = new Object[3];
        objArr[0] = offset >= 0 ? "+" : "-";
        objArr[1] = Integer.valueOf(offset / 3600000);
        objArr[2] = Integer.valueOf((offset / 60000) % 60);
        return String.format(str, objArr);
    }
}
