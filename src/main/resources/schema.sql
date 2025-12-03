CREATE TABLE orders (
    orderId INT PRIMARY KEY AUTO_INCREMENT,
    localD DATE,
    localT TIME,
    items VARCHAR(100),
    quantity INT,
    onHand BOOLEAN
);
