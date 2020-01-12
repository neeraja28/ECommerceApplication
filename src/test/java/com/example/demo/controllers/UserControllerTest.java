package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

import java.util.Optional;
import java.util.logging.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    Logger logger = LoggerFactory.getLogger(UserControllerTest.class);
    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        logger.info("Creating user in User Controller Test");
         ResponseEntity<User> createdUser = userController.createUser(createUserRequest);

         when(userRepository.findById(1L)).thenReturn(Optional.of(createdUser.getBody()));
         when(userRepository.findByUsername("test")).thenReturn(createdUser.getBody());
    }

    @Test
    public void findById_test() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        logger.info("Creating user in User Controller Test");
        ResponseEntity<User> createdUser = userController.createUser(createUserRequest);
        assertNotNull(createdUser);
        assertEquals(200, createdUser.getStatusCodeValue());

        ResponseEntity<User> response = userController.findById(createdUser.getBody().getId());

        User user = createdUser.getBody();
        assertNotNull(user);
        assertEquals(createUserRequest.getUsername(), user.getUsername());
    }

    @Test
    public void findByUsername_test() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        user = response.getBody();
        assertEquals("test", user.getUsername());
        assertEquals(1L, user.getId());
    }

    @Test
    public void create_user_happy_path() throws Exception {
        //Stubbing
        when(encoder.encode("testPassword")).thenReturn("This is hashed");
        //the above line is called stubbing, which replaces the code inside when with the value in thenReturn
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        User user = responseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("This is hashed", user.getPassword());
    }

    @Test
    public void usernameMismatch_test() throws Exception {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("wrongUser");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        final ResponseEntity<User> response = userController.findByUserName(responseEntity.getBody().getUsername());

        User u = response.getBody();
        assertNull(u);
        //assertNotNull(responseEntity);
       // assertEquals;

    }

    @Test
    public void passwordMismatch_test() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("Password");

        final ResponseEntity<User> responseEntity = userController.createUser(userRequest);
        assertNotNull(responseEntity);
        assertEquals(400, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findByUsername_happy_path()
    {
        String username = "test";

        Cart cart = new Cart();

        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPassword("thisIsHashed");
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        user = response.getBody();
        Assert.assertEquals("test", user.getUsername());
        assertEquals(1, user.getId());
    }

}
