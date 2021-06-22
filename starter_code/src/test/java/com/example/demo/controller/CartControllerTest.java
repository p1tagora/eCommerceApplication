package com.example.demo.controller;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.util.TestUtils;
import org.assertj.core.util.Lists;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "testPassword";

    private static final Long TEST_CART_ID = 1L;

    private static final Item TEST_ITEM_1 = new Item(1L, "Circle", new BigDecimal("3.55"), "a normal circle");
    private static final Item TEST_ITEM_2 = new Item(2L, "Square", new BigDecimal("4.55"), "a normal square");
    private static final List<Item> TEST_ITEMS = Lists.list(TEST_ITEM_1, TEST_ITEM_2);
    private static final BigDecimal TEST_ITEMS_TOTAL = new BigDecimal("8.10");


    private static CartController cartController;

    private static final UserRepository userRepository = mock(UserRepository.class);
    private static final CartRepository cartRepository = mock(CartRepository.class);
    private static final ItemRepository itemRepository = mock(ItemRepository.class);

    @BeforeAll
    public static void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void testAddToCart() {
        Cart cart = new Cart();
        cart.setId(TEST_CART_ID);
        cart.setItems(TEST_ITEMS);
        cart.setTotal(TEST_ITEMS_TOTAL);
        User user = new User(TEST_USER_ID, TEST_USER, TEST_PASSWORD, cart);
        cart.setUser(user);

        when(userRepository.findByUsername(TEST_USER)).thenReturn(user);
        when(itemRepository.findById(TEST_ITEM_1.getId())).thenReturn(java.util.Optional.of(TEST_ITEM_1));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(TEST_ITEM_1.getId());
        request.setQuantity(1);
        request.setUsername(TEST_USER);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Cart retrievedCart = response.getBody();

        assertNotNull(retrievedCart);
        assertEquals(retrievedCart.getId(), TEST_CART_ID);
        List<Item> items = retrievedCart.getItems();
        assertNotNull(items);
        assertEquals(items.size(), TEST_ITEMS.size());
        Item retrievedItem = items.get(0);
        assertNotNull(retrievedItem);
        assertEquals(retrievedItem, TEST_ITEM_1);
        assertEquals(retrievedCart.getTotal(), new BigDecimal("11.65"));
        assertEquals(retrievedCart.getUser(), user);
    }

    @Test
    public void removeFromCart() {
        Cart cart = new Cart();
        cart.setId(TEST_CART_ID);
        cart.setItems(TEST_ITEMS);
        cart.setTotal(TEST_ITEMS_TOTAL);
        User user = new User(TEST_USER_ID, TEST_USER, TEST_PASSWORD, cart);
        cart.setUser(user);

        when(userRepository.findByUsername(TEST_USER)).thenReturn(user);
        when(itemRepository.findById(TEST_ITEM_1.getId())).thenReturn(java.util.Optional.of(TEST_ITEM_1));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(TEST_ITEM_1.getId());
        request.setQuantity(1);
        request.setUsername(TEST_USER);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        Cart retrievedCart = response.getBody();

        assertNotNull(retrievedCart);
        assertEquals(retrievedCart.getId(), TEST_CART_ID);
        List<Item> items = retrievedCart.getItems();
        assertNotNull(items);
        assertEquals(retrievedCart.getTotal(), TEST_ITEM_2.getPrice());
        assertEquals(retrievedCart.getUser(), user);
    }
}
