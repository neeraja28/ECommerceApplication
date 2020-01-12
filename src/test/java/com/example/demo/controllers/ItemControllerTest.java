package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemControllerTest {
    private Logger logger = LoggerFactory.getLogger(ItemControllerTest.class);

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }


    @Test
    public void getItemById_test() throws Exception {
        logger.info("Get item by id in Item controller test");
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        List<Item> itemLists = new ArrayList<>();
        itemLists.add(item);

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        ResponseEntity<Item> responseEntity = itemController.getItemById(item.getId());
        Item response = responseEntity.getBody();

        assertNotNull(response);
        assertEquals(item.getId(), response.getId());

    }

    @Test
    public void getItemsByName_test() throws Exception {
        logger.info("Get item by name in Item controller test");
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(BigDecimal.valueOf(2.99));
        List<Item> items = new ArrayList<Item>();
        items.add(item);

        when(itemRepository.findByName("Round Widget")).thenReturn(items);
        ResponseEntity<List<Item>> responseItems = itemController.getItemsByName("Round Widget");
        assertNotNull(responseItems);
        List<Item> response = responseItems.getBody();

        assertNotNull(response);
        assertEquals("Round Widget", response.get(0).getName());
    }

    @Test
    public void getItems_test() throws Exception {
        logger.info("Get items in Item controller test");
        assertNotNull(itemRepository);

        Item item = new Item();
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setId(1L);
        item.setPrice(BigDecimal.valueOf(2.99));

        List<Item> items = new ArrayList<Item>();
        items.add(item);
        items.add(item);
        try {
            items.add(item);
            when(itemRepository.findByName("Round Widget")).thenReturn(items);
            when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));

            ResponseEntity<List<Item>> responseItems = itemController.getItems();
            List<Item> itemList = responseItems.getBody();
            assertNotNull(itemList.get(0));

            assertEquals("Round Widget", itemList.get(0).getName());
            assertEquals("A widget that is round", itemList.get(0).getDescription());
        }catch(Exception e ) {}
    }


}
