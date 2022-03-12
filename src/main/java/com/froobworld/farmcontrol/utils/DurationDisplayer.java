package com.froobworld.farmcontrol.utils;

import java.util.concurrent.TimeUnit;

public final class DurationDisplayer {

    private DurationDisplayer() {
    }

    private static String quantityText(int quantity, String singularSuffix, String pluralSuffix) {
        String quantityText = quantity + "";
        if (quantity == 1) {
            quantityText = "one";
        } else if (quantity == 2) {
            quantityText = "two";
        } else if (quantity == 3) {
            quantityText = "three";
        } else if (quantity == 4) {
            quantityText = "four";
        } else if (quantity == 5) {
            quantityText = "five";
        } else if (quantity == 6) {
            quantityText = "six";
        } else if (quantity == 7) {
            quantityText = "seven";
        } else if (quantity == 8) {
            quantityText = "eight";
        } else if (quantity == 9) {
            quantityText = "nine";
        }

        return quantityText + (quantity == 1 ? singularSuffix : pluralSuffix);
    }

    public static String getDurationInMinutesAndSeconds(long durationInMillis) {
        long timeInMinutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis);
        long timeInSeconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis - TimeUnit.MINUTES.toMillis(timeInMinutes));
        if (timeInSeconds == 0 && timeInMinutes == 0) {
            return "less than a second";
        }
        String minutePart = quantityText((int) timeInMinutes, " minute", " minutes");
        String secondPart = (timeInMinutes > 0 ? " and " : "") + quantityText((int) timeInSeconds, " second", " seconds");
        String result = "";
        if (timeInMinutes > 0) {
            result += minutePart;
        }
        if (timeInSeconds > 0) {
            result += secondPart;
        }

        return result;
    }

}
