package com.sagnik.ecommerce_backend.service.impl;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.entity.*;
import com.sagnik.ecommerce_backend.exception.CartNotFoundException;
import com.sagnik.ecommerce_backend.exception.InsufficientStockException;
import com.sagnik.ecommerce_backend.exception.UserNotFoundException;
import com.sagnik.ecommerce_backend.repository.CartRepository;
import com.sagnik.ecommerce_backend.repository.OrderRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.repository.UserRepository;
import com.sagnik.ecommerce_backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public CheckoutResponse checkout(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Step 1: Validate stock
        for (CartItem cartItem : cart.getCartItems()) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {

                throw new InsufficientStockException(
                        product.getName() + " is out of stock."
                );
            }
        }

        // Step 2: Create Order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .totalAmount(cart.getTotalPrice())
                .build();

        // Step 3: Copy CartItems to OrderItems
        for (CartItem cartItem : cart.getCartItems()) {

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .price(cartItem.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .subtotal(
                            calculateSubtotal(
                                    cartItem.getProduct(),
                                    cartItem.getQuantity()
                            )
                    )
                    .build();

            order.getOrderItems().add(orderItem);
        }

        // Step 4: Reduce stock
        for (CartItem cartItem : cart.getCartItems()) {

            Product product = cartItem.getProduct();

            product.setStock(
                    product.getStock() - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        // Step 5: Save Order
        orderRepository.save(order);

        // Step 6: Clear Cart
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);

        cartRepository.save(cart);

        // Step 7: Return Response
        return CheckoutResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .message("Order placed successfully.")
                .build();
    }
    private BigDecimal calculateSubtotal(Product product, Integer quantity) {
        return product.getPrice()
                .multiply(BigDecimal.valueOf(quantity));
    }

}