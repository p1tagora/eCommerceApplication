package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.util.TestUtils;
import org.assertj.core.util.Lists;

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

public class OrderControllerTest {
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "testPassword";

    private static final Long TEST_CART_ID = 1L;

    private static final Item TEST_ITEM_1 = new Item(1L, "Circle", new BigDecimal("3.55"), "a normal circle");
    private static final Item TEST_ITEM_2 = new Item(2L, "Square", new BigDecimal("4.55"), "a normal square");
    private static final List<Item> TEST_ITEMS = Lists.list(TEST_ITEM_1, TEST_ITEM_2);
    private static final BigDecimal TEST_ITEMS_TOTAL = new BigDecimal("8.10");

    private OrderController orderController;
    private final UserRepository userRepository = mock(UserRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    @BeforeEach
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void testSubmit() {
        Cart cart = new Cart();
        cart.setId(TEST_CART_ID);
        cart.setItems(TEST_ITEMS);
        cart.setTotal(TEST_ITEMS_TOTAL);
        User user = new User(TEST_USER_ID, TEST_USER, TEST_PASSWORD, cart);
        cart.setUser(user);
        when(userRepository.findByUsername(TEST_USER)).thenReturn(user);

        ResponseEntity<UserOrder> responseEntity =  orderController.submit(TEST_USER);

        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        
        UserOrder userOrder = responseEntity.getBody();

        assertNotNull(userOrder);
        assertEquals(userOrder.getItems(), TEST_ITEMS);
        assertEquals(userOrder.getTotal(), TEST_ITEMS_TOTAL);
        assertEquals(userOrder.getUser(), user);
    }

    @Test
    public void testGetOrdersForUser() {
        Cart cart = new Cart();
        cart.setId(TEST_CART_ID);
        cart.setItems(TEST_ITEMS);
        cart.setTotal(TEST_ITEMS_TOTAL);
        User user = new User(TEST_USER_ID, TEST_USER, TEST_PASSWORD, cart);
        cart.setUser(user);
        when(userRepository.findByUsername(TEST_USER)).thenReturn(user);

        ResponseEntity<UserOrder> response =  orderController.submit(TEST_USER);
        UserOrder userOrder = response.getBody();

        when(orderRepository.findByUser(user)).thenReturn(Lists.list(userOrder));
        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(TEST_USER);

        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        List<UserOrder> userOrders = responseEntity.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());
        assertEquals(userOrders.get(0).getItems(), TEST_ITEMS);
        assertEquals(userOrders.get(0).getTotal(), TEST_ITEMS_TOTAL);
        assertEquals(userOrders.get(0).getUser(), user);
    }
}
