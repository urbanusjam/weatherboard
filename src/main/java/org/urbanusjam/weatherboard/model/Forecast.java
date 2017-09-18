package org.urbanusjam.weatherboard.model;

/**
 * Weather forecast for a given date. Part of {@link Condition}.
 */
public class Forecast {

    private String date;
    private String day;
    private String low;
    private String high;
    private String desc;

    public Forecast() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
