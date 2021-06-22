package com.example.demo.controller;


import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.util.TestUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private static final Item TEST_ITEM_1 = new Item(1L, "Circle", new BigDecimal("3.55"), "a normal circle");
    private static final Item TEST_ITEM_2 = new Item(2L, "Square", new BigDecimal("4.55"), "a normal square");
    private static final List<Item> TEST_ITEMS = Lists.list(TEST_ITEM_1, TEST_ITEM_2);
    private static final BigDecimal TEST_ITEMS_TOTAL = new BigDecimal("8.10");

    private ItemController itemController;
    private final ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void testGetItems() {
        when(itemRepository.findAll()).thenReturn(TEST_ITEMS);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<Item> retrievedItems = response.getBody();
        assertNotNull(retrievedItems);
        assertEquals(retrievedItems.size(), TEST_ITEMS.size());
        assertEquals(retrievedItems.get(0), TEST_ITEM_1);
        assertEquals(retrievedItems.get(1), TEST_ITEM_2);
    }

    @Test
    public void testGetItemById() {
        when(itemRepository.findById(TEST_ITEM_1.getId())).thenReturn(java.util.Optional.of(TEST_ITEM_1));

        ResponseEntity<Item> response = itemController.getItemById(TEST_ITEM_1.getId());

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        Item retrievedItem = response.getBody();
        assertNotNull(retrievedItem);
        assertEquals(retrievedItem, TEST_ITEM_1);
        assertEquals(retrievedItem.getName(), TEST_ITEM_1.getName());
        assertEquals(retrievedItem.getId(), TEST_ITEM_1.getId());
        assertEquals(retrievedItem.getDescription(), TEST_ITEM_1.getDescription());
    }

    @Test
    public void testGetItemsByName() {
        when(itemRepository.findByName(TEST_ITEM_2.getName())).thenReturn(Lists.list(TEST_ITEM_2));

        ResponseEntity<List<Item>> response = itemController.getItemsByName(TEST_ITEM_2.getName());

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<Item> retrievedItems = response.getBody();

        assertNotNull(retrievedItems);
        assertEquals(retrievedItems.size(), 1);
        assertEquals(retrievedItems.get(0), TEST_ITEM_2);
    }

}