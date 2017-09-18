package org.urbanusjam.weatherboard.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.urbanusjam.weatherboard.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    User findByUsername(String username);

}
