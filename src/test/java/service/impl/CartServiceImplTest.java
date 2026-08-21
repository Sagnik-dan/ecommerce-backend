package service.impl;

import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartItemResponse;
import com.sagnik.ecommerce_backend.entity.Cart;
import com.sagnik.ecommerce_backend.entity.CartItem;
import com.sagnik.ecommerce_backend.entity.Product;
import com.sagnik.ecommerce_backend.entity.User;
import com.sagnik.ecommerce_backend.exception.CartItemNotFoundException;
import com.sagnik.ecommerce_backend.exception.CartNotFoundException;
import com.sagnik.ecommerce_backend.repository.CartItemRepository;
import com.sagnik.ecommerce_backend.repository.CartRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.security.AuthenticationService;
import com.sagnik.ecommerce_backend.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.sagnik.ecommerce_backend.dto.UpdateCartItemRequest;
import com.sagnik.ecommerce_backend.dto.CartResponse;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

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
                .totalPrice(BigDecimal.ZERO)
                .cartItems(new ArrayList<>())
                .build();
    }

    @Test
    void addToCart_shouldAddNewItemToExistingCart() {

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(
                cart.getId(),
                product.getId()
        )).thenReturn(Optional.empty());

        cartService.addToCart(request);

        assertEquals(1, cart.getCartItems().size());

        CartItem item = cart.getCartItems().get(0);

        assertEquals(product, item.getProduct());
        assertEquals(2, item.getQuantity());

        assertEquals(
                new BigDecimal("200.00"),
                cart.getTotalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void addToCart_shouldIncreaseQuantity_whenProductAlreadyExists() {

        CartItem existingItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(existingItem);

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(1L);
        request.setQuantity(3);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(product.getId()))
                .thenReturn(Optional.of(product));

        when(cartItemRepository.findByCartIdAndProductId(
                cart.getId(),
                product.getId()
        )).thenReturn(Optional.of(existingItem));

        cartService.addToCart(request);

        assertEquals(1, cart.getCartItems().size());

        assertEquals(5, existingItem.getQuantity());

        assertEquals(
                new BigDecimal("500.00"),
                cart.getTotalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void updateQuantity_shouldUpdateQuantityAndRecalculateTotal() {

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(cartItem);
        cart.setTotalPrice(new BigDecimal("200.00"));

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        cartService.updateQuantity(1L, request);

        assertEquals(5, cartItem.getQuantity());

        assertEquals(
                new BigDecimal("500.00"),
                cart.getTotalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void updateQuantity_shouldThrowException_whenCartItemBelongsToAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();

        Cart anotherCart = Cart.builder()
                .id(2L)
                .user(anotherUser)
                .cartItems(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(anotherCart)
                .product(product)
                .quantity(2)
                .build();

        anotherCart.getCartItems().add(cartItem);

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                CartItemNotFoundException.class,
                () -> cartService.updateQuantity(1L, request)
        );

        assertEquals(2, cartItem.getQuantity());

        verify(cartRepository, never()).save(any());
    }

    @Test
    void removeItem_shouldRemoveItemAndRecalculateTotal() {

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(cartItem);
        cart.setTotalPrice(new BigDecimal("200.00"));

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        cartService.removeItem(1L);

        assertEquals(0, cart.getCartItems().size());

        assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void removeItem_shouldThrowException_whenCartItemBelongsToAnotherUser() {

        User anotherUser = User.builder()
                .id(2L)
                .email("another@example.com")
                .build();

        Cart anotherCart = Cart.builder()
                .id(2L)
                .user(anotherUser)
                .cartItems(new ArrayList<>())
                .totalPrice(new BigDecimal("200.00"))
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(anotherCart)
                .product(product)
                .quantity(2)
                .build();

        anotherCart.getCartItems().add(cartItem);

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartItemRepository.findById(1L))
                .thenReturn(Optional.of(cartItem));

        assertThrows(
                CartItemNotFoundException.class,
                () -> cartService.removeItem(1L)
        );

        assertEquals(1, anotherCart.getCartItems().size());

        verify(cartRepository, never()).save(any());
    }

    @Test
    void clearCart_shouldRemoveAllItemsAndResetTotal() {

        CartItem item1 = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        CartItem item2 = CartItem.builder()
                .id(2L)
                .cart(cart)
                .product(product)
                .quantity(3)
                .build();

        cart.getCartItems().add(item1);
        cart.getCartItems().add(item2);
        cart.setTotalPrice(new BigDecimal("500.00"));

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        cartService.clearCart();

        assertEquals(0, cart.getCartItems().size());

        assertEquals(
                BigDecimal.ZERO,
                cart.getTotalPrice()
        );

        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_shouldThrowException_whenCartDoesNotExist() {

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> cartService.clearCart()
        );

        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCart_shouldReturnCartResponse() {

        CartItem item = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(item);
        cart.setTotalPrice(new BigDecimal("200.00"));

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(cart));

        CartResponse response = cartService.getCart();

        assertNotNull(response);
        assertEquals(1L, response.getCartId());
        assertEquals(1, response.getTotalItems());
        assertEquals(
                new BigDecimal("200.00"),
                response.getTotalPrice()
        );

        assertEquals(1, response.getItems().size());

        CartItemResponse itemResponse = response.getItems().get(0);

        assertEquals(1L, itemResponse.getCartItemId());
        assertEquals(1L, itemResponse.getProductId());
        assertEquals("Test Product", itemResponse.getProductName());
        assertEquals(new BigDecimal("100.00"), itemResponse.getUnitPrice());
        assertEquals(2, itemResponse.getQuantity());
        assertEquals(new BigDecimal("200.00"), itemResponse.getSubtotal());
    }

    @Test
    void getCart_shouldThrowException_whenCartDoesNotExist() {

        when(authenticationService.getAuthenticatedUser())
                .thenReturn(user);

        when(cartRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CartNotFoundException.class,
                () -> cartService.getCart()
        );
    }
}