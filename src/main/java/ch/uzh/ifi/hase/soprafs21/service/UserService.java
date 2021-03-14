package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        // User is created an automatically logged in
        newUser.setStatus(UserStatus.ONLINE);

        checkIfUserExists(newUser);

        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }
    /**
     * Checks the login credentials, if they are correct or not -> Throws an error if incorrect
     */
    public User checkLoginCredentials(User user){
        // First we need to check, if the user even exists in our Repository
        if(userRepository.findByUsername(user.getUsername())==null){
            String baseErrorMessage = "The user with this username does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(baseErrorMessage));
        } else{
            // Get's us the current User saved with this username
            User currentUser = userRepository.findByUsername(user.getUsername());
            // Get's his current password and the one we want to compare
            String testPassword = user.getPassword();
            String currentPassword = currentUser.getPassword();
            // Checks now, if the password of the login, is the same, as the currentPassword
            if(!currentPassword.equals(testPassword)){
                String baseErrorMessage = "The password is incorrect!";
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,String.format(baseErrorMessage));
            }else{
                // Set's the user to online, if he logged in correctly
                currentUser.setStatus(UserStatus.ONLINE);
                userRepository.save(currentUser);
                userRepository.flush();
                return currentUser;
            }

        }

    }

    /**
     * This function get's the User by id
     * It also checks, if the User exists with this id, otherwise throws an error
     */
    public User getUserbyID(long id){
        User user =  userRepository.findById(id);
        if (user == null){
            String baseErrorMessage = "The user with this id does not exist";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,String.format(baseErrorMessage));
        }else{
            return user;
        }
    }

    /**
     * This function updates the old User with the new Username or the new Birthday added
     */
    public User updateUser(User currentUser, User userInput){
        if(userInput.getUsername()==null){
            String baseErrorMessage = "You cannot choose an empty Username!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
        }
        // We check first, if the userInputs username is empty
        if(userInput.getUsername()!=null){
            // If it isn't empty, we need to check, if there is already a user with this username
            // We need to also check, that the User didn't just set the same Username as he already had
            User databaseUser = userRepository.findByUsername(userInput.getUsername());
            if(databaseUser != null && currentUser.getUsername()!=databaseUser.getUsername()){
                String baseErrorMessage = "You cannot choose this Username. It has already been taken!";
                throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
            }else{
                currentUser.setUsername(userInput.getUsername());
            }
        }
        if(userInput.getBirthday()!=null){
            currentUser.setBirthday(userInput.getBirthday());
        }
        userRepository.save(currentUser);
        userRepository.flush();
        return currentUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        User userByName = userRepository.findByName(userToBeCreated.getName());

        String baseErrorMessage = "The username and user provided are not unique. Therefore, the user could not be created!";
        if (userByUsername != null && userByName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
        }
        else if (userByUsername != null) {
            baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
        }
        else if (userByName != null) {
            baseErrorMessage = "The name provided is not unique. Therefore, the user could not be created!";
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage));
        }
    }

    /**
     * This function is for logging out the User
     */
    public User setUserOffline(User user){
        // Get's the id of the user
        long id = user.getId();
        User logoutUser = userRepository.findById(id);
        logoutUser.setStatus(UserStatus.OFFLINE);
        userRepository.save(logoutUser);
        userRepository.flush();
        return logoutUser;
    }
}
