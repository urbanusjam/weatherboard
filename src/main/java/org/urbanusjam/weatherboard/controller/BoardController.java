package org.urbanusjam.weatherboard.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.urbanusjam.weatherboard.model.Board;
import org.urbanusjam.weatherboard.model.Location;
import org.urbanusjam.weatherboard.model.User;
import org.urbanusjam.weatherboard.service.WeatherService;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.urbanusjam.weatherboard.util.JSONConverter.toLocation;

@RestController
@RequestMapping("/")
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private WeatherService service;

    @GetMapping(value="/login")
    public ResponseEntity<User> login(@RequestParam("username") String username) {
        User user = service.getUser(username);
        if(user != null) {
            return new ResponseEntity<>(user, OK);
        } else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @PostMapping(value="/register")
    public ResponseEntity<?> register(@RequestBody String username) {
        try {
            service.addUser(new User(username));
            return new ResponseEntity<>(CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(CONFLICT);
        }
    }

    @GetMapping(value={ "/boards/{username}", "/boards/{username}/locations" }, produces = {"application/json" })
    public ResponseEntity<List<Location>> listLocations(@PathVariable String username) {
        Board board = service.getBoard(username);
        if(board != null) {
            return new ResponseEntity<>(board.getLocations(), OK) ;
        }
        else {
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @PostMapping(value="/boards/{username}/locations", consumes="application/json")
    public ResponseEntity<?> addLocation(@PathVariable String username, @RequestBody String location) throws IOException {
        try {
            Location convertedLocation = toLocation(location);
            logger.debug("Location to add: " + convertedLocation);
            if(convertedLocation == null) {
                return new ResponseEntity<>(BAD_REQUEST);
            }
            return new ResponseEntity<>(service.addLocation(username, convertedLocation), CREATED);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(CONFLICT);
        }
    }

    @GetMapping(value="/boards/{username}/locations/{locationId}")
    public Location getLocation(@PathVariable String username, @PathVariable String locationId) throws IOException {
        return service.getLocation(locationId);
    }

    @DeleteMapping(value="/boards/{username}/locations/{locationId}")
    public ResponseEntity<?> deleteLocation(@PathVariable String username, @PathVariable String locationId) {
        service.deleteLocation(username, locationId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @Scheduled(fixedRate = 50000)
    public void updateLocations() {
        service.updateLocations();
    }

}
