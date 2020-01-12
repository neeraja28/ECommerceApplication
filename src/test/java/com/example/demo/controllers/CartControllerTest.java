package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

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
import java.util.Optional;
import java.util.logging.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CartControllerTest {
    Logger logger = LoggerFactory.getLogger(CartControllerTest.class);

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

    }

    @Test
    public void addToCart() throws Exception {
        logger.info("Add to cart in Cart controller test");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        Cart cart = new Cart();
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        Optional<Item> optionalItem = Optional.of(item);

        when(itemRepository.findById(1L)).thenReturn(optionalItem);
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().getItems().size());
    }

    @Test
    public void addToCart_userNotFound() throws Exception {
        logger.info("Add to cart in Cart controller test: User not found");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        User user = null;
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        Optional<Item> optionalItem = itemRepository.findById(0L);

        when(itemRepository.findById(1L)).thenReturn(optionalItem);
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    public void addToCart_itemNotFound() throws Exception {
        logger.info("Add to cart in Cart controller test: Item not found");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        Cart cart = new Cart();
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.addTocart(request);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void removeFromCart() throws Exception {
        logger.info("Remove from cart in Cart controller test");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        Optional<Item> optionalItem = Optional.of(item);

        Cart cart = new Cart();
        user.setCart(cart);
        cart.setId(1L);
        List<Item> items = cart.getItems();
        if(items == null) {
            items = new ArrayList<Item>();
        }
        items.add(item);
        items.add(item);
        cart.setItems(items);
        cart.setTotal(BigDecimal.valueOf(2.99 * 3));

        when(itemRepository.findById(1L)).thenReturn(optionalItem);
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().getItems().size());
    }

    @Test
    public void removeFromCart_userNotFound() throws Exception {
        logger.info("Remove from cart in Cart controller test : User not found");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        User user = null;
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        Optional<Item> optionalItem = Optional.of(item);

        when(itemRepository.findById(1L)).thenReturn(optionalItem);
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    public void removeFromCart_itemNotFound() throws Exception {
        logger.info("Remove from cart in Cart controller test : Item not found");
        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1L);
        request.setQuantity(1);
        request.setUsername("test");

        Cart cart = new Cart();
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(request);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
