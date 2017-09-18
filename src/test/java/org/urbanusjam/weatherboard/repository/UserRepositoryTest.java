package org.urbanusjam.weatherboard.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.urbanusjam.weatherboard.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private final String username = "alice";

    @Before
    public void setUp() throws Exception {
        userRepository.save(new User(username));
    }

    @Test
    public void testRetrieveExistingUser(){
        User user = userRepository.findByUsername(username);
        assertThat(user, notNullValue());
        assertThat(user.getId(), notNullValue());
        assertThat(userRepository.findAll(), hasSize(1));
    }

    @Test
    public void testRetrieveNonExistingUser() {
        assertThat(userRepository.findByUsername("bob"), nullValue());
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
    }
}
