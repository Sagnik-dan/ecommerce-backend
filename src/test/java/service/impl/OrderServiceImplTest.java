package service.impl;

import com.sagnik.ecommerce_backend.dto.OrderDetailResponse;
import com.sagnik.ecommerce_backend.entity.*;
import com.sagnik.ecommerce_backend.exception.CartEmptyException;
import com.sagnik.ecommerce_backend.exception.InsufficientStockException;
import com.sagnik.ecommerce_backend.exception.InvalidOrderStateException;
import com.sagnik.ecommerce_backend.exception.OrderNotFoundException;
import com.sagnik.ecommerce_backend.repository.CartRepository;
import com.sagnik.ecommerce_backend.repository.OrderRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.security.AuthenticationService;
import com.sagnik.ecommerce_backend.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sagnik.ecommerce_backend.dto.CheckoutResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .stock(10)
                .version(0L)
                .build();

        cart = Cart.builder()
                .id(1L)
                .user(user)
                .totalPrice(new BigDecimal("200.00"))
                .cartItems(new ArrayList<>())
                .build();
    }

    @Test
    void checkout_shouldThrowException_whenCartIsEmpty() {

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        assertThrows(
                CartEmptyException.class,
                () -> orderService.checkout()
        );

        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void checkout_shouldThrowException_whenStockIsInsufficient() {

        product.setStock(2);

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(5)
                .build();

        cart.getCartItems().add(cartItem);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.checkout()
        );

        verify(orderRepository, never()).save(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void checkout_shouldCreateOrderAndReduceStock() {

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(cartItem);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        CheckoutResponse response = orderService.checkout();

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(
                new BigDecimal("200.00"),
                response.getTotalAmount()
        );

        assertEquals(8, product.getStock());

        assertEquals(0, cart.getCartItems().size());
        assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );

        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void getOrder_shouldThrowException_whenOrderBelongsToAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(anotherUser)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("200.00"))
                .build();

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(1L)
        );
    }

    @Test
    void getOrder_shouldReturnOrder_whenOrderBelongsToUser() {

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("200.00"))
                .orderItems(new ArrayList<>())
                .build();

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderDetailResponse response = orderService.getOrder(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals(
                new BigDecimal("200.00"),
                response.getTotalAmount()
        );
    }

    @Test
    void cancelOrder_shouldRestoreStock_whenOrderIsActive() {

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(2)
                .subtotal(new BigDecimal("200.00"))
                .build();

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("200.00"))
                .orderItems(new ArrayList<>())
                .build();

        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        // Product currently has 8 because 2 were consumed during checkout
        product.setStock(8);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));


        OrderDetailResponse response = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(10, product.getStock());

        assertNotNull(response);
        assertEquals(OrderStatus.CANCELLED, response.getStatus());

        verify(productRepository).findById(product.getId());

        verify(productRepository).save(
                argThat(savedProduct ->
                        savedProduct.getStock() == 10
                )
        );
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_shouldThrowException_whenAlreadyCancelled() {

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.CANCELLED)
                .orderItems(new ArrayList<>())
                .build();

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.cancelOrder(1L)
        );

        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_shouldThrowException_whenOrderIsDelivered() {

        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.DELIVERED)
                .orderItems(new ArrayList<>())
                .build();

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        assertThrows(
                InvalidOrderStateException.class,
                () -> orderService.cancelOrder(1L)
        );

        verify(productRepository, never()).findById(anyLong());
        verify(orderRepository, never()).save(any());
    }
}