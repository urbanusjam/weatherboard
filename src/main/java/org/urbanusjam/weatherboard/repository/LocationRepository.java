package org.urbanusjam.weatherboard.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.urbanusjam.weatherboard.model.Location;

public interface LocationRepository extends MongoRepository<Location, String> {

    @Query(fields = "{'woeid': ?0}")
    Location findLocationByWoeid(String woeid);

}
