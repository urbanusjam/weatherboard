package org.urbanusjam.weatherboard.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a geographic place.
 */
@Document(collection = "locations")
public class Location {

    @Id
    private String id;
    @Indexed(unique = true)
    private String woeid;
    private String city;
    private String locality;
    private String country;
    private Condition status;

    public Location() {}

    public Location(String woeid, String city, String locality, String country) {
        this.woeid = woeid;
        this.city = city;
        this.locality = locality;
        this.country = country;
    }

    public Location(String woeid, String city, String locality, String country, Condition currentStatus) {
        this.woeid = woeid;
        this.city = city;
        this.locality = locality;
        this.country = country;
        this.status = currentStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Condition getStatus() {
        return status;
    }

    public void setStatus(Condition status) {
        this.status = status;
    }

}
