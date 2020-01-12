package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import static org.junit.Assert.assertEquals;
import  static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderControllerTest {
    Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        Item item = new Item();
        item.setDescription("A widget that is round");
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));

        Cart cart = new Cart();
        cart.addItem(item);

        User user = new User();
        user.setCart(cart);
        user.setId(1L);
        user.setPassword("testPassword");
        user.setUsername("test");

        List<UserOrder> orderList = new ArrayList<>();
        UserOrder order = new UserOrder();
        order.setId(1L);
        order.setUser(user);
        orderList.add(order);

        when(orderRepository.findByUser(any(User.class))).thenReturn(orderList);
        when(userRepository.findByUsername("test")).thenReturn(user);

    }

    @Test
    public void submit_NullUser() throws Exception {
        logger.info("NullUser in Order Controller Test");
        ResponseEntity<?> responseEntity = orderController.submit("");
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void submit_userOrder() throws Exception {
       logger.info("Submit Order For User in Order Controller Test");
       ResponseEntity<?> responseEntity = orderController.submit("test");
       assertNotNull(responseEntity);
       assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getOrders_forUser() throws Exception {
        logger.info("Get orders for user in Order Controller test");
        ResponseEntity<List<UserOrder>> userOrders = orderController.getOrdersForUser("test");
        assertNotNull(userOrders);
        assertEquals(200, userOrders.getStatusCodeValue());
    }
}
