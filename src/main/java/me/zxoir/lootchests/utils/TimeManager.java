package me.zxoir.lootchests.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * MIT License Copyright (c) 2020 Zxoir
 *
 * @author Zxoir
 * @since 7/2/2021
 */
public class TimeManager {
    String time;
    long timeLong;

    public TimeManager(String time) {
        this.time = time;
    }

    public TimeManager(long time) {
        this.timeLong = time;
    }

    @NotNull
    public String formatTime() {
        long days = TimeUnit.SECONDS.toDays(timeLong);
        long hours = TimeUnit.SECONDS.toHours(timeLong % 86400);
        long minutes = TimeUnit.SECONDS.toMinutes((timeLong % 3600));
        long seconds = TimeUnit.SECONDS.toSeconds(timeLong % 60);

        String timeString;
        if (timeLong >= 86400) { // days
            String day = (days != 1) ? "days" : "day";
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";

            if (hours > 0)
                timeString = timeString = days + " " + day + " " + hours + " " + hour;
            else if (minutes == 0)
                timeString = days + " " + day;
            else
                timeString = days + " " + day + " and " + minutes + " " + minute;

        } else if (timeLong >= 3600) { // hours
            String hour = (hours != 1) ? "hours" : "hour";
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (minutes > 0)
                timeString = hours + " " + hour + " and " + minutes + " " + minute;
            else if (seconds == 0)
                timeString = hours + " " + hour;
            else
                timeString = hours + " " + hour + " and " + seconds + " " + second;
        } else if (timeLong >= 60) { // minutes
            String minute = (minutes != 1) ? "minutes" : "minute";
            String second = (seconds != 1) ? "seconds" : "second";
            if (seconds > 0)
                timeString = minutes + " " + minute + " and " + seconds + " " + second;
            else
                timeString = minutes + " " + minute;
        } else { // seconds
            String second = (seconds != 1) ? "seconds" : "second";
            timeString = seconds + " " + second;
        }

        return timeString;
    }

    @Nullable
    public Long toMilliSecond() {
        time = time.replaceAll("\\s", ""); // Remove white space
        String[] sl = time.toLowerCase().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

        if (sl.length == 1) {
            if (isLong(time))
                return Long.parseLong(time) * 1000;
            else return null;
        }

        long total = 0;
        long temp = 0;
        for (String key : sl) {

            if (isLong(key)) {
                temp = Long.parseLong(key);
                continue;
            }

            switch (key) {
                case "second":
                case "seconds":
                case "s":
                    total += temp * 1000;
                    break;
                case "minutes":
                case "minute":
                case "m":
                    total += temp * 1000 * 60;
                    break;
                case "hours":
                case "hour":
                case "h":
                    total += temp * 1000 * 60 * 60;
                    break;
                case "days":
                case "day":
                case "d":
                    total += temp * 1000 * 60 * 60 * 24;
                    break;
                case "weeks":
                case "week":
                case "w":
                    total += temp * 1000 * 60 * 60 * 24 * 7;
                    break;
                case "months":
                case "month":
                case "mo":
                    total += temp * 1000 * 60 * 60 * 24 * 30;
                    break;
                default:
                    return null;
            }
        }

        return total;
    }

    private boolean isLong(String key) {
        try {
            Long.parseLong(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
