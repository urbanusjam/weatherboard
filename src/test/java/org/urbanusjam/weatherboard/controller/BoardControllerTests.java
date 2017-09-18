package org.urbanusjam.weatherboard.controller;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.service.WeatherService;
import org.urbanusjam.weatherboard.rest.BoardController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;


@RunWith(SpringRunner.class)
@WebMvcTest(BoardController.class)
public class BoardControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeatherService service;

    private User user;

    @Test
    public void saveBoard(){
        //        board = new Board(user);
        //        boardRepository.save(board);
        //        assertThat(boardRepository.findByUser(user), notNullValue());
        user = service.addUser(new User("panda"));



        Location location = new Location();
        location.setWoeid("12345");
        location.setCity("Buenos Aires");
        location.setCountry("Argentina");
//        location.setCountryCode("AR");
        service.addLocationToBoard(user.getUsername(), location);

        Location savedLoc = service.getLocation(user.getUsername(), location.getId());
        assertThat(savedLoc, notNullValue());
    }

    @After
    public void tearDown() {
//        service.deleteUser(user.getId());
    }

}
