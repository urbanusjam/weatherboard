package org.urbanusjam.weatherboard.service;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.Status;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.repository.BoardRepository;
import org.urbanusjam.weatherboard.repository.LocationRepository;
import org.urbanusjam.weatherboard.repository.UserRepository;

import java.io.IOException;

import static java.lang.String.format;
import static org.urbanusjam.weatherboard.util.JSONConverter.toStatus;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static final String API_ENDPOINT = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    private static final String FORECAST_QUERY = "select lastBuildDate, atmosphere, item.condition from weather.forecast where woeid=%s and u='%s'";
    private OkHttpClient client = new OkHttpClient();

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(User user) {
        User newUser = userRepository.insert(user);
        boardRepository.insert(new Board(newUser));
    }

    @Override
    public User getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Board getBoard(String username) {
        return boardRepository.findByUsername(username);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Location addLocation(String username, Location location) {
        logger.debug("Find board for user " + username);
        Board board = boardRepository.findByUsername(username);
        if(board != null) {
            if(board.getLocations().stream()
                    .filter(loc -> loc.getWoeid().equals(location.getWoeid()))
                    .findFirst().isPresent()) {
                throw new DuplicateKeyException(format("Location '%s, %s' is already present", location.getCity(), location.getCountry()));
            }
            location.setStatus(getWeatherStatus(location.getWoeid()));
            board.getLocations().add(location);
            boardRepository.save(board);
            return location;
        }
        return null;
    }

    @Override
    public Location getLocation(String locationId) {
        return locationRepository.findLocationByWoeid(locationId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteLocation(String username, String locationId) {
        Board board = boardRepository.findByUsername(username);
        board.getLocations().stream()
                .filter(loc -> loc.getWoeid().equals(locationId))
                .findFirst()
                .ifPresent(loc -> board.getLocations().remove(loc));
        boardRepository.save(board);
    }

    @Override
    public void updateLocations() {
        logger.info("Execute scheduled job updateLocations()");
        boardRepository.findAll().forEach(board -> board.getLocations().forEach(location -> getWeatherStatus(location.getWoeid())));
        logger.info("Completed execution of scheduled job");
    }

    private Status getWeatherStatus(String woeid) {
        return toStatus(executeQuery(format(API_ENDPOINT, format(FORECAST_QUERY, woeid, "c"))));
    }

    public String executeQuery(String query) {
        String result = null;
        try {
            Request request = new Request.Builder().url(HttpUrl.parse(query)).build();
            logger.info("Query: " + request.url().toString());
            result = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

}
