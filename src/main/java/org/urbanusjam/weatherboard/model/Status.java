
package org.urbanusjam.weatherboard.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static java.lang.String.format;

public class Status {

    @JsonFormat(pattern = "EEEE dd", locale = "es")
    private LocalDateTime date;
    private String temperature;
    private String description;
    private String humidity;
    private String pressure;
    @JsonFormat(pattern = "dd MMM yyyy HH:mm")
    private LocalDateTime lastUpdated;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return format("Status [date=%s, temperature=%s, description=%s, lastUpdates=%s]", date, temperature, description, lastUpdated);
    }
}
