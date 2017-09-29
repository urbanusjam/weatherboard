package org.urbanusjam.weatherboard.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeatherServiceTest {

    @Autowired
    private WeatherService service;

    private Location location;

    @Before
    public void setUp() {
        location = new Location();
        location.setWoeid("466887");
        location.setCity("Tandil");
        location.setLocality("Tandil");
        location.setRegion("Buenos Aires");
        location.setCountry("Argentina");
    }

    @Test
    public void getBoard(){
        Board board = service.getBoard("lala");

        Location loc = board.getLocations().get(0);


//        String la = board.getLocation("466887").getWoeid();
        assertThat(board, notNullValue());
    }

    @Test
    public void addUser(){
        service.addUser(new User("lala"));

    }

    @Test
    public void removeLocation(){
        service.deleteLocation("panda", "466887");

    }

    @Test
    public void addLocation(){
        service.addLocation("lala", location);
    }



}
