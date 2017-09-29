package org.urbanusjam.weatherboard.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.urbanusjam.weatherboard.WeatherApplication;
import org.urbanusjam.weatherboard.controller.BoardController;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WeatherApplication.class })
public class ApplicationTest {

    @Autowired
    private BoardController controller;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller, notNullValue());
    }
}
