function getOrders(orderId) {
    if (document.getElementById("orders" + orderId).innerHTML == "") {
        fetch('http://localhost:8080/orders/' + orderId)
            .then(orders => orders.json())
            .then(function(orders) {
                var textToDisplay = "<br>";
                textToDisplay += "Date: " + orders.localD + "<br>";
                textToDisplay += "Time: " + orders.localT + "<br>";
                textToDisplay += "Quantity: " + orders.quantity + "<br>";
                textToDisplay += "On Hand? : " + orders.onHand + "<br>";
                
                // Changing Div Tab
                document.getElementById("orders" + orderId).innerHTML = textToDisplay;
            });
    } else {
        document.getElementById("orders" + orderId).innerHTML = "";
    }
}
