package org.urbanusjam.weatherboard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

public class JSONConverter {

    private static final Logger logger = LoggerFactory.getLogger(JSONConverter.class);
    private static ObjectMapper mapper = new ObjectMapper();

    private JSONConverter() {
        // Prevent instantiation of utility class
    }

    public static Location toLocation(String locationString) {
        try {
            return mapper.readValue(locationString, Location.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static Status toStatus(String response) {
        logger.debug("Query response: " + response);
        JSONObject results = new JSONObject(response).getJSONObject("query").getJSONObject("results");
        if(results == null) {
            logger.error("The request to Yahoo Weather Forecast service returned 0 results");
            return null;
        }

        JSONObject channel = results.getJSONObject("channel");
        Status status = new Status();
        // misc
        status.setLastUpdated(toDate(channel.getString("lastBuildDate")));
        status.setHumidity(channel.getJSONObject("atmosphere").getString("humidity"));
        status.setPressure(channel.getJSONObject("atmosphere").getString("pressure"));

        // current condition
        JSONObject condition = channel.getJSONObject("item").getJSONObject("condition");
        status.setDate(toDate(condition.getString("date")));
        status.setTemperature(condition.getString("temp"));
        status.setDescription(condition.getString("text"));
        return status;

    }

    public static LocalDateTime toDate(String date) {
       return ZonedDateTime.parse(date, ofPattern("EEE, dd MMM yyyy hh:mm a z")).toLocalDateTime();
    }

}
