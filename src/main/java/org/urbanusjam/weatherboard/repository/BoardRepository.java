package org.urbanusjam.weatherboard.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.urbanusjam.weatherboard.model.Board;

public interface BoardRepository extends MongoRepository<Board, String> {

    @Query("{'user.username': ?0}")
    Board findByUsername(String username);

}
