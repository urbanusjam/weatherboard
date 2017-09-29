package org.urbanusjam.weatherboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.urbanusjam.weatherboard.util.JSONConverter;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.Status;

import java.io.IOException;
import java.io.InputStream;
import java.time.Month;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class MapperTest {

    String query = "{\"channel\":  \"lastBuildDate\": \"Wed, 27 Sep 2017 06:41 PM AKDT\", {\"atmosphere\":{\"rising\":\"0\",\"visibility\":\"25.91\",\"humidity\":\"80\",\"pressure\":\"33863.90\"},\"item\":{\"condition\":{\"date\":\"Wed, 27 Sep 2017 10:00 PM ART\",\"temp\":\"5\",\"code\":\"31\",\"text\":\"Clear\"}},\"astronomy\":{\"sunrise\":\"6:39 am\",\"sunset\":\"6:57 pm\"},\"wind\":{\"chill\":\"37\",\"speed\":\"17.70\",\"direction\":\"113\"}}}";
    String locationItem = "{\"name\":\"Tandil\",\"country\":\"Argentina\",\"admin1\":\"Buenos Aires\",\"locality1\":\"Tandil\",\"woeid\":\"466887\"}";

    @Test
    public void convertToLStatus() throws IOException {
        try (InputStream in = MapperTest.class.getResourceAsStream("/queryresult.txt")) {
            Status status = JSONConverter.toStatus(IOUtils.toString(in));
            assertThat(status, notNullValue());
        }
    }

    @Test
    public void convertDate() {
        ZonedDateTime date = ZonedDateTime.parse("Wed, 27 Sep 2017 06:41 PM AKDT", ofPattern("EEE, dd MMM yyyy hh:mm a z"));
        assertThat(date.getYear(), is(2017));
        assertThat(date.getMonth(), is(Month.SEPTEMBER));
        assertThat(date.getDayOfMonth(), is(27));
    }

    @Test
    public void convertToLocation() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Location location = mapper.readValue(locationItem, Location.class);
        assertThat(location.getWoeid(), notNullValue());
    }

}
