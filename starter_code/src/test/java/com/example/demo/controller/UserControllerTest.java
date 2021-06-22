package com.example.demo.controller;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.util.TestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "testPassword";

    private static UserController userController;

    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final CartRepository cartRepository = mock(CartRepository.class);
    private static final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @BeforeAll
    public static void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserHappyPath() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USER);
        createUserRequest.setPassword(TEST_PASSWORD);
        createUserRequest.setConfirmPassword(TEST_PASSWORD);

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(user.getUsername(), TEST_USER);
        assertNull(user.getPassword()); //encoded password will not be returned in the response
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    public void testFindUserById() {
        //create new user first
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USER);
        createUserRequest.setPassword(TEST_PASSWORD);
        createUserRequest.setConfirmPassword(TEST_PASSWORD);
        ResponseEntity<User> responseEntityCreatedUser = userController.createUser(createUserRequest);
        User createdUser = responseEntityCreatedUser.getBody();
        when(userRepository.findById(TEST_ID)).thenReturn(java.util.Optional.ofNullable(createdUser));

        //retrieve the user
        ResponseEntity<User> responseEntityGetUser = userController.findById(TEST_ID);
        assertNotNull(responseEntityGetUser);

        User user = responseEntityGetUser.getBody();
        assertNotNull(user);
        assertEquals(user.getUsername(), TEST_USER);
        assertNull(user.getPassword()); //encoded password will not be returned in the response
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    public void testFindUserByUsername() {
        //create new user first
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(TEST_USER);
        createUserRequest.setPassword(TEST_PASSWORD);
        createUserRequest.setConfirmPassword(TEST_PASSWORD);
        ResponseEntity<User> responseEntityCreatedUser = userController.createUser(createUserRequest);
        User createdUser = responseEntityCreatedUser.getBody();
        when(userRepository.findByUsername(TEST_USER)).thenReturn(createdUser);

        //retrieve the user
        ResponseEntity<User> responseEntityGetUser = userController.findByUserName(TEST_USER);
        assertNotNull(responseEntityGetUser);

        User user = responseEntityGetUser.getBody();
        assertNotNull(user);
        assertEquals(user.getUsername(), TEST_USER);
        assertNull(user.getPassword()); //encoded password will not be returned in the response
        assertTrue(user.isEnabled());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());
    }
}
