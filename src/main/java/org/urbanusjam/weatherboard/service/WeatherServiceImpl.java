package org.urbanusjam.weatherboard.service;

import com.google.common.collect.Lists;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Condition;
import org.urbanusjam.weatherboard.model.Forecast;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.repository.BoardRepository;
import org.urbanusjam.weatherboard.repository.LocationRepository;
import org.urbanusjam.weatherboard.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static final String API_ENDPOINT = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    private static final String FORECAST_QUERY = "select * from weather.forecast where woeid=%s and u='%s'";
    private static final String LOCATION_QUERY = "select * from geo.places where text='%s'";

    private OkHttpClient client = new OkHttpClient();

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void updateLocations() {
        logger.info("About to execute scheduled job updateLocations()");
        locationRepository.findAll().forEach(location -> executeForecastQuery(location.getWoeid()));
        logger.info("Completed execution of scheduled job");
    }

    @Override
    public List<User> getUsers() {
        logger.debug("Retrieving users...");
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public User addUser(User user) {
        userRepository.insert(user);
        return boardRepository.insert(new Board(user)).getUser();
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }

    @Override
    public Board getBoard(String username) {
        return boardRepository.findByUsername(username);
    }

    @Override
    public String executeQuery(String query) {
        String result = null;
        try {
            Request request = new Request.Builder().url(HttpUrl.parse(query)).build();
            logger.info("query: " + request.url().toString());
            result = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Transactional
    @Override
    public String addLocationToBoard(String username, Location location) {
        Board board = boardRepository.findByUsername(username);
        if(board != null) {
            location.setStatus(executeForecastQuery(location.getWoeid()));
            board.getLocations().add(location);
            locationRepository.insert(location);
            boardRepository.save(board);
            return board.getId();
        }
        return null;
    }

    @Override
    public Location getLocation(String user, String locationId) {
        return locationRepository.findById(locationId);
    }

    @Override
    public void deleteLocation(String username, String locationId) {
        Board board = boardRepository.findByUsername(username);
        board.getLocations().stream()
                .filter(loc -> loc.getId().equals(locationId))
                .findFirst()
                .ifPresent(loc -> board.getLocations().remove(loc));
        boardRepository.save(board);
    }

    // TODO: move auxiliary methods to utility class

    private Condition executeForecastQuery(String woeid) {
        // TODO: validate empty result
        // TODO: narrow attributes in SELECT statement and map JSON to POJO
        JSONObject channel = new JSONObject(executeQuery(format(API_ENDPOINT, format(FORECAST_QUERY, woeid, "c")))).getJSONObject("query").getJSONObject("results").getJSONObject("channel");
        JSONObject item = channel.getJSONObject("item");
        //condition
        JSONObject condition = item.getJSONObject("condition");
        Condition status = new Condition();
        status.setTemp(condition.getString("temp"));
        status.setDescription(condition.getString("text"));
        status.setLastUpdated(LocalDateTime.now());

        //wind
        JSONObject wind = channel.getJSONObject("wind");

        //atmosphere
        JSONObject atmosphere = channel.getJSONObject("atmosphere");

        //forecast
        JSONArray forecastItems = item.getJSONArray("forecast");
        List<Forecast> forecastList = Lists.newArrayList();
        for(int i=0; i < forecastItems.length(); i++) {
            JSONObject aux = (JSONObject) forecastItems.get(i);
            Forecast forecast = new Forecast();
            forecast.setDate(aux.getString("date"));
            forecast.setDay(aux.getString("day"));
            forecast.setHigh(aux.getString("high"));
            forecast.setLow(aux.getString("low"));
            forecast.setDesc(aux.getString("text"));
            forecastList.add(forecast);
        }
        status.setForecast(forecastList);

//        Location location = locationRepository.findByWoeid(woeid);
//        location.setStatus(status);
//        locationRepository.save(location);

        return status;
    }

    private Location convert(JSONObject place) throws JSONException {
        JSONObject country = place.getJSONObject("country");
        return new Location(place.getString("woeid"), place.getString("name"), country.getString("content"), country.getString("code"), null);
    }

}
