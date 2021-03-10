package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs21.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }
    /**
     * This function get's a list of all users in the database
     */
    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    /**
     * This function creates a user and checks before, if the user is allready present in the database,
     * if yes an Exception is thrown.
     */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
        // Checks if username & password given are not empty
        if(userPostDTO.getUsername() == null || userPostDTO.getPassword()==null){
            String baseErrorMessage = "You need to provide a username and password";
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,String.format(baseErrorMessage));
        }
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // create user
        User createdUser = userService.createUser(userInput);

        // convert internal representation of user back to API
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
    }

    /**
     * This function retrieves a single User based on his ID
     */
    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable("userId") long userId){
        // Get's the user by id -> Function implemented in the UserService
        User user = userService.getUserbyID(userId);
        // We change the format to a UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        return userGetDTO;
    }

    /**
     * This function is for updating the User -> PutMapping.
     * It will update the users birthday and username
     */
    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public UserGetDTO updateUser(@PathVariable("userId")long userId, @RequestBody UserPutDTO userPutDTO) {
        User currentUser = userService.getUserbyID(userId);
        User inputUser = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User updatedUser = userService.updateUser(currentUser,inputUser);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(updatedUser);

    }
    /**
     * This function is specifically made for /login
     * This will specifically return true (in String) format if logged in correctly
     * Otherwise it will return false
     */
    @PutMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO checkLogin(@RequestBody UserPutDTO userPutDTO){
        User userInput = DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO);
        User user = userService.checkLoginCredentials(userInput);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    /**
     * This function is made for logging the User out
     * It sets the User.Status = Offline, when the user logs out
     */
    @PutMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String setUserOffline(@RequestBody UserPutDTO userPutDTO){
        long id = userPutDTO.getId();
        System.out.println(id);
        Boolean logout = userService.setUserOffline(id);
        return String.valueOf(logout);
    }

}
