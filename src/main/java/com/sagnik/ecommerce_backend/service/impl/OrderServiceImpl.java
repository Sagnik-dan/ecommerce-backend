package com.sagnik.ecommerce_backend.service.impl;

import com.sagnik.ecommerce_backend.dto.CheckoutResponse;
import com.sagnik.ecommerce_backend.dto.OrderDetailResponse;
import com.sagnik.ecommerce_backend.dto.OrderItemResponse;
import com.sagnik.ecommerce_backend.dto.OrderSummaryResponse;
import com.sagnik.ecommerce_backend.entity.*;
import com.sagnik.ecommerce_backend.exception.*;
import com.sagnik.ecommerce_backend.repository.CartRepository;
import com.sagnik.ecommerce_backend.repository.OrderRepository;
import com.sagnik.ecommerce_backend.repository.ProductRepository;
import com.sagnik.ecommerce_backend.repository.UserRepository;
import com.sagnik.ecommerce_backend.security.AuthenticationService;
import com.sagnik.ecommerce_backend.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final AuthenticationService authenticationService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public CheckoutResponse checkout() {

        User user = authenticationService.getAuthenticatedUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        // Step 1: Validate stock
        for (CartItem cartItem : cart.getCartItems()) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
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

    @Override
    public List<OrderSummaryResponse> getOrders() {

        User user = authenticationService.getAuthenticatedUser();

        List<Order> orders = orderRepository.findByUserId(user.getId());

        return orders.stream()
                .map(this::mapToOrderSummary)
                .toList();
    }

    private OrderSummaryResponse mapToOrderSummary(Order order) {

        return OrderSummaryResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .build();
    }
    @Override
    public OrderDetailResponse getOrder(Long orderId) {

        User user = authenticationService.getAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotFoundException(
                    "Order not found");
        }

        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(
                        order.getOrderItems()
                                .stream()
                                .map(this::mapToOrderItemResponse)
                                .toList()
                )
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(
            OrderItem item) {

        return OrderItemResponse.builder()
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    @Override
    @Transactional
    public OrderDetailResponse cancelOrder(Long orderId) {

        User user = authenticationService.getAuthenticatedUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotFoundException(
                    "Order not found");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new InvalidOrderStateException(
                    "Order is already cancelled."
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderStateException(
                    "Delivered orders cannot be cancelled."
            );
        }

        restoreStock(order);

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return getOrder(orderId);
    }

    private void restoreStock(Order order) {

        for (OrderItem item : order.getOrderItems()) {

            Product product = productRepository.findById(
                    item.getProductId()
            ).orElseThrow(() ->
                    new ProductNotFoundException(
                            "Product not found"
                    )
            );

            product.setStock(
                    product.getStock() + item.getQuantity()
            );

            productRepository.save(product);
        }
    }

}