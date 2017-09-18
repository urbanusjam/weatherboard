package org.urbanusjam.weatherboard.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.urbanusjam.weatherboard.model.Location;

public interface LocationRepository extends MongoRepository<Location, String> {

    Location findById(String id);

    Location findByWoeid(String woeid);

}
