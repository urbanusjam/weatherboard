package org.urbanusjam.weatherboard.service;

import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;

public interface WeatherService {

    void addUser(User user);

    User getUser(String username);

    Board getBoard(String username);

    Location getLocation(String locationId);

    Location addLocation(String username, Location location);

    void deleteLocation(String username, String woeid);

    void updateLocations();

    String executeQuery(String query);

}
