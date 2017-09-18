package org.urbanusjam.weatherboard.service;

import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;

import java.util.List;

public interface WeatherService {

    Board getBoard(String username);

    Location getLocation(String user, String locationId);

    String addLocationToBoard(String username, Location location);

    void deleteLocation(String user, String locationId);

    void updateLocations();

    String executeQuery(String query);

    List<User> getUsers();

    User addUser(User user);

    void deleteUser(String userId);

}
