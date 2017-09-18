package org.urbanusjam.weatherboard.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.urbanusjam.weatherboard.model.Location;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;
    private Location newLocation;

    @Before
    public void setUp() {
        newLocation = new Location("20070753", "Ciudad de Buenos Aires", "Argentina", "AR", null);
    }

    @Test
    public void testInsertLocation() {
        locationRepository.insert(newLocation);
        Location location = locationRepository.findByWoeid(newLocation.getWoeid());
        assertThat(location, notNullValue());
        assertThat(location.getId(), notNullValue());
    }

    @After
    public void tearDown() {
        locationRepository.deleteAll();
    }

}
