package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */

// We want to specifically test the UserControllerClass
@WebMvcTest(UserController.class)
public class UserControllerTest {

    // This allows us to call particular requests on specific paths
    @Autowired
    private MockMvc mockMvc;

    // Dummy Mock Service-> No need to implement the persistence
    @MockBean
    private UserService userService;

    /*
     * This test is for the get Request, to get all users. In this test, we only check if one user is created
     * and the response is correct @GetMapping("/users")
     */
    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setPassword("1234");
        user.setStatus(UserStatus.OFFLINE);

        // This is what we are expecting to be returned
        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then we perform the action
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));

    }
    /* This is a similar test to the first one, however it checks, with more than one user
     * @GetMapping("/users")
     */
    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray2() throws Exception {
        // given
        User firstuser = new User();
        firstuser.setName("Dennis Shushack");
        firstuser.setUsername("dennis@shushack");
        firstuser.setPassword("1234");
        firstuser.setStatus(UserStatus.OFFLINE);

        User seconduser = new User();
        seconduser.setName("Max Mustermann");
        seconduser.setUsername("max@mustermann");
        seconduser.setPassword("3424");
        seconduser.setStatus(UserStatus.OFFLINE);

        // Creates an  Arraylist with the first and the second user
        List<User> allUsers = Arrays.asList(firstuser,seconduser);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(firstuser.getName())))
                .andExpect(jsonPath("$[0].username", is(firstuser.getUsername())))
                .andExpect(jsonPath("$[0].status", is(firstuser.getStatus().toString())))
                .andExpect(jsonPath("$[1].name", is(seconduser.getName())))
                .andExpect(jsonPath("$[1].username", is(seconduser.getUsername())))
                .andExpect(jsonPath("$[1].status", is(seconduser.getStatus().toString())));

    }

    /* This tests the post Request, when adding a new user
    *  @PostMapping("/users")
    */
    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setPassword("123");
        user.setStatus(UserStatus.OFFLINE);

        // We create a UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setName("Test User");
        userPostDTO.setUsername("testUsername");
        userPostDTO.setPassword("123");

        // Arrange -> Whatever user we enter we will receive user
        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then (Act)
        mockMvc.perform(postRequest)
                // Assert expected outcomes
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }
    /* Checks, if the login-functionality is working properly (Put Request)
     * @PutMapping("/login")
     */
    @Test
    public void loginUser_valid() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setPassword("1234");

        given(userService.checkLoginCredentials(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    /* This one checks if the logout is valid:
     * @PutMapping("/logout")
     */
    @Test
    public void logoutUser_valid() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setPassword("123");
        userPutDTO.setId(1L);

        given(userService.setUserOffline(Mockito.any())).willReturn(user);

        /// when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));
        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
    }

    /* This test refers to the @PutMapping
     * @PutMapping("/users/{userId}")
     */

        @Test
    public void updateUser_valid() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setBirthday("12.02.2020");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setId(1L);
        userPutDTO.setUsername("testUsername");
        userPutDTO.setBirthday("12.02.2020");

        given(userService.updateUser(Mockito.any(),Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO));

        // then -> Returns NO_CONTENT SO CHECK STATUS
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());

    }

    // The function checks the get function getUserbyID ( @GetMapping("/users/{userId}"))
    @Test
    public void givenUsers_GetUser_byId_valid() throws Exception{
        // given
        User user = new User();
        user.setId(1L);
        user.setName("TestUser");
        user.setUsername("testuser");
        user.setBirthday("12.02.2020");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        // this mocks the UserService -> we define above what the userService should return when getUserbyID() is called
        given(userService.getUserbyID(user.getId())).willReturn(user);

        // this mocks the UserService -> we define above what the userService should return when getUserbyID() is called
        MockHttpServletRequestBuilder getRequest = get("/users/1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.status", is(user.getStatus().toString())));


    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}