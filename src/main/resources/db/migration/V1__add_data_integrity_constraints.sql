ALTER TABLE cart_items
    MODIFY quantity INT NOT NULL,
    ADD CONSTRAINT chk_cart_item_quantity_positive
    CHECK (quantity > 0);

ALTER TABLE order_items
    MODIFY quantity INT NOT NULL,
    ADD CONSTRAINT chk_order_item_quantity_positive
    CHECK (quantity > 0);

ALTER TABLE order_items
    MODIFY price DECIMAL(38,2) NOT NULL,
    ADD CONSTRAINT chk_order_item_price_non_negative
    CHECK (price >= 0);

ALTER TABLE order_items
    MODIFY subtotal DECIMAL(38,2) NOT NULL,
    ADD CONSTRAINT chk_order_item_subtotal_non_negative
    CHECK (subtotal >= 0);