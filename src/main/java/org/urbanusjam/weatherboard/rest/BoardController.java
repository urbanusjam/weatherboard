package org.urbanusjam.weatherboard.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.service.WeatherService;

import java.io.IOException;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class BoardController {

    private static final String MESSAGE_404 = "¡Oops! La página solicitada no existe.";

    @Autowired
    private WeatherService service;

    @RequestMapping(value= { "/", "/boards"}, method = GET)
    public String getIndex() {
        return "index";
    }

    @RequestMapping(value="/boards", method = POST)
    public ResponseEntity<?> addUser(@RequestBody String username) {
        try {
            service.addUser(new User(username));
            return new ResponseEntity<>(CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(CONFLICT);
        }
    }

    @RequestMapping(value= { "/boards/{username}", "/boards/{username}/locations" }, method = GET)
    public String getUserBoard(@PathVariable String username, Model model) {
        Board board = service.getBoard(username);
        if(board != null) {
            model.addAttribute("user", board.getUser().getUsername());
            model.addAttribute("locationList", board.getLocations());
            return "board";
        }
        else {
            return getErrorPage(model, MESSAGE_404);
        }
    }

    @RequestMapping(value="/boards/{user}/locations", method = POST)
    public ResponseEntity<?> addLocationToBoard(@PathVariable String user, @RequestBody String location) throws IOException {
        try {
            return service.addLocationToBoard(user, new ObjectMapper().readValue(location, Location.class)) != null ? new ResponseEntity<>(CREATED) : new ResponseEntity<>(NOT_FOUND);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(CONFLICT);
        }
    }

    @RequestMapping(value="/boards/{user}/locations/{locationId}", method = GET)
    public String getLocation(@PathVariable String user, @PathVariable String locationId, Model model) throws IOException {
        Location location =  service.getLocation(user, locationId);
        if(location != null) {
            model.addAttribute("location", location);
            return "location";
        }
        else {
            return getErrorPage(model, MESSAGE_404);
        }
    }

    @RequestMapping(value="/boards/{user}/locations/{locationId}", method = DELETE)
    public ResponseEntity<?> deleteLocation(@PathVariable String user, @PathVariable String locationId) {
        service.deleteLocation(user, locationId);
        return new ResponseEntity<Location>(NO_CONTENT);
    }

    // polling service scheduled to execute every 5 minutes to update the status of all locations
//    @Scheduled(fixedDelay=500000)
    public void updateLocations() throws IOException {
        service.updateLocations();
    }

    private String getErrorPage(Model model, String message) {
        model.addAttribute("errorMessage", message);
        return "error";
    }

}
