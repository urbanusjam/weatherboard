package org.urbanusjam.weatherboard.repository;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.assertj.core.util.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.service.WeatherService;
import org.urbanusjam.weatherboard.rest.BoardController;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@WebMvcTest(BoardController.class)
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;

    @Mock
    private WeatherService service;

    private User user;
    private Board board;
    private static final String API_ENDPOINT = "https://query.yahooapis.com/v1/public/yql?q=%s&format=json";
    private static final String FORECAST_QUERY = "select * from weather.forecast where woeid=%s and u='%s'";
    private static final String LOCATION_QUERY = "select * from geo.places where text='%s'";

    @Before
    public void setUp() {
        user = new User("dummy");
        userRepository.save(user);
    }

    @Test
    public void saveBoard(){
//        board = new Board(user);
//        boardRepository.save(board);
//        assertThat(boardRepository.findByUser(user), notNullValue());

        Location location = new Location();
        location.setWoeid("12345");
        location.setCity("Buenos Aires");
        location.setCountry("Argentina");
//        location.setCountryCode("AR");
        service.addLocationToBoard(user.getUsername(), location);

        Location savedLoc = service.getLocation(user.getUsername(), location.getId());
        assertThat(savedLoc, notNullValue());
    }

    public List<Location> queryLocations() throws IOException, JSONException {
        String text = "buenos aires";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(HttpUrl.parse(format(API_ENDPOINT, format(LOCATION_QUERY, text))))
                .build();
        Response response = client.newCall(request).execute();
        String result = response.body().string();

        JSONArray arr = new JSONObject(result).getJSONObject("query").getJSONObject("results").getJSONArray("place");
        List<Location> locations = Lists.newArrayList();
        for(int i=0; i < arr.length(); i++) {
            locations.add(convert(arr.getJSONObject(i)));
        }
        return locations;
    }

    private Location convert(JSONObject place) throws JSONException {
        JSONObject country = place.getJSONObject("country");
        return new Location(place.getString("woeid"), place.getString("name"), country.getString("content"), country.getString("code"), null);
    }

//    @Test
    public void testRemoveLocationFromBoard() throws IOException, JSONException {
        Board board = boardRepository.findByUsername(user.getUsername());

        Location searchLoc = queryLocations().get(0);
        List<Location> currentLocations = board.getLocations();
        assertThat(currentLocations, hasSize(0));
        currentLocations.add(searchLoc);
        board.setLocations(currentLocations);
        boardRepository.save(board);

        board.getLocations().stream()
                .filter(loc -> loc.getWoeid().equals(searchLoc.getWoeid()))
                .findFirst()
                .ifPresent(loc -> board.getLocations().remove(loc));
        boardRepository.save(board);
    }

    @Test
    public void testAddLocationFromBoard() throws IOException, JSONException {
        Board board = boardRepository.findByUsername(user.getUsername());
        List<Location> currentLocations = board.getLocations();
        assertThat(currentLocations, hasSize(0));
        currentLocations.add(queryLocations().get(0));
        board.setLocations(currentLocations);
        boardRepository.save(board);
        assertThat(board.getLocations(), hasSize(1));
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
    }

}

