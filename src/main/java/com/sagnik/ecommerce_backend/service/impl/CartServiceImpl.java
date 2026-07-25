package com.sagnik.ecommerce_backend.service.impl;
import com.sagnik.ecommerce_backend.dto.AddToCartRequest;
import com.sagnik.ecommerce_backend.dto.CartItemResponse;
import com.sagnik.ecommerce_backend.dto.CartResponse;
import com.sagnik.ecommerce_backend.dto.UpdateCartItemRequest;
import com.sagnik.ecommerce_backend.entity.Cart;
import com.sagnik.ecommerce_backend.entity.CartItem;
import com.sagnik.ecommerce_backend.entity.Product;
import com.sagnik.ecommerce_backend.entity.User;
import com.sagnik.ecommerce_backend.exception.CartItemNotFoundException;
import com.sagnik.ecommerce_backend.exception.CartNotFoundException;
import com.sagnik.ecommerce_backend.exception.ProductNotFoundException;
import com.sagnik.ecommerce_backend.exception.UserNotFoundException;
import com.sagnik.ecommerce_backend.repository.CartItemRepository;
import com.sagnik.ecommerce_backend.repository.CartRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.repository.UserRepository;
import com.sagnik.ecommerce_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void addToCart(Long userId, AddToCartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException("Product not found"));

        Optional<CartItem> existingItem =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        product.getId());

        if (existingItem.isPresent()) {

            CartItem item = existingItem.get();

            item.setQuantity(
                    item.getQuantity() + request.getQuantity());

        } else {

            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getCartItems().add(item);
        }

        recalculateCartTotal(cart);

        cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cart.getCartItems()) {
            total = total.add(calculateItemTotal(item));
        }

        cart.setTotalPrice(total);
    }

    private BigDecimal calculateItemTotal(CartItem item) {

        return item.getProduct()
                .getPrice()
                .multiply(
                        BigDecimal.valueOf(
                                item.getQuantity()
                        )
                );
    }

    @Override
    public CartResponse getCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        List<CartItemResponse> items = cart.getCartItems()
                .stream()
                .map(this::mapToCartItemResponse)
                .toList();

        return CartResponse.builder()
                .cartId(cart.getId())
                .totalItems(cart.getCartItems().size())
                .totalPrice(cart.getTotalPrice())
                .items(items)
                .build();
    }
    private CartItemResponse mapToCartItemResponse(
            CartItem item) {

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .unitPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(calculateItemTotal(item))
                .build();
    }

    @Override
    public void updateQuantity(
            Long cartItemId,
            UpdateCartItemRequest request) {

        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException("Cart item not found"));

        cartItem.setQuantity(request.getQuantity());

        recalculateCartTotal(cartItem.getCart());

        cartRepository.save(cartItem.getCart());
    }

    @Override
    public void removeItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository
                .findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException("Cart item not found"));

        Cart cart = cartItem.getCart();

        cart.getCartItems().remove(cartItem);

        recalculateCartTotal(cart);

        cartRepository.save(cart);
    }

    @Override
    public void clearCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        cart.getCartItems().clear();

        recalculateCartTotal(cart);

        cartRepository.save(cart);
    }
}