package org.urbanusjam.weatherboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import static java.lang.String.format;

/**
 * Represents a geographic place.
 */
public class Location {

    @Id
    @JsonProperty("woeid")
    private String woeid;
    @JsonProperty("name")
    private String city;
    @JsonProperty("locality1")
    private String locality;
    @JsonProperty("admin1")
    private String region;
    @JsonProperty("country")
    private String country;
    @JsonIgnoreProperties("status")
    private Status status;

    public Location() {}

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return format("Location [woeid=%s, city=%s, locality=%s, region=%s, country=%s]", woeid, city, locality, region, country);
    }

}
