package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("123");

        // when
        User createdUser = userService.createUser(testUser);

        // then
        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("123");
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same username
        User testUser2 = new User();

        // change the name but forget about the username
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }
    // This method checks that a dublicate creation of a user with the same name throws an error
    @Test
    public void createUser_duplicateName_throwsException() {
        assertNull(userRepository.findByName("testName"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");

        //when
        User createdUser = userService.createUser(testUser);

        // attempt to create second user with same name
        User testUser2 = new User();

        testUser2.setName("testName");
        testUser2.setUsername("testUsername2");
        testUser2.setPassword("v1234");

        // check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
    }

    // test method checks, if the getUserbyId method
    @Test
    public void check_getUserById_success() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");

        User createdUser = userService.createUser(testUser);

        // when
        User found = userService.getUserbyID(createdUser.getId());

        // then
        assertEquals(testUser.getId(), found.getId());
    }

    //tests the update of the currentUser with Inputs
    @Test
    public void updateCurrentUser_with_Inputs_success() {
        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");
        User createdUser = userService.createUser(testUser);

        //User input for update
        User testUser2 = new User();
        testUser2.setUsername("NewUsername");
        testUser2.setBirthday("23.03.1994");

        // when
        User updatedUser = userService.updateUser(createdUser, testUser2);

        //then
        assertEquals(updatedUser.getId(), createdUser.getId());
        assertEquals(updatedUser.getUsername(), testUser2.getUsername());
        assertEquals(updatedUser.getBirthday(), testUser2.getBirthday());
    }

    //tests Login, if Login has valid Inputs
    @Test
    public void LoginUser_validInputs_success() {
        //given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");
        User createdUser = userService.createUser(testUser);
        // when
        User loggedInUser = userService.checkLoginCredentials(createdUser);

        //then
        assertEquals(testUser.getUsername(), loggedInUser.getUsername());
        assertEquals(testUser.getPassword(), loggedInUser.getPassword());
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
    }

    //test method for Logout
    @Test
    public void LogoutUser_success() {

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");
        User createdUser = userService.createUser(testUser);

        // when
        User loggedOutUser = userService.setUserOffline(createdUser);

        // then
        assertEquals(loggedOutUser.getStatus(), UserStatus.OFFLINE);
    }


    //tests Login with a wrong password given
    @Test
    public void LoginUser_with_WrongPassword_throwsException() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("v1234");
        User createdUser = userService.createUser(testUser);

        //create input user
        User testUser2 = new User();
        // wrong password input
        testUser2.setUsername("testUsername");
        testUser2.setPassword("wrongPassword");

        // when
        User loggedInUser = userService.checkLoginCredentials(createdUser);

        // Is an error thrown
        assertThrows(ResponseStatusException.class, () -> userService.checkLoginCredentials(testUser2));
    }

}
