package com.mycompany.library_system.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.mycompany.library_system.adt.MapInterface;

public class StockOrder {
    
    public enum Status {
        PENDING,
        DELIVERED,
        CANCELLED
    }

    private String orderId;
    private MapInterface<String, Integer> orders; // Key = Book ID, Value = Quantity ordered
    private int totalQuantity; 
    private double totalPrice; 
    private LocalDateTime orderDate;
    private LocalDateTime eta; // Estimated Time of Arrival
    private Status status;

    public StockOrder(String orderId, MapInterface<String, Integer> orders, LocalDateTime orderDate, LocalDateTime eta, Status status, int totalQuantity, double totalPrice) {
        this.orderId = orderId;
        this.orders = orders;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.eta = eta;
        this.status = status;
    }   

    public StockOrder(String orderId, MapInterface<String, Integer> orders, int totalQuantity, double totalPrice, int daysToDeliver) {
        this(orderId, orders, LocalDateTime.now(), LocalDateTime.now().plusMinutes(daysToDeliver), Status.PENDING, totalQuantity, totalPrice);
    }

    public String getId() { return orderId; }
    public MapInterface<String, Integer> getOrders() { return orders; }
    public int getTotalQuantity() { return totalQuantity; }
    public double getTotalPrice() { return totalPrice; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public LocalDateTime getEta() { return eta; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        sb.append(String.format("%-15s: %s\n", "Order", orderId));
        sb.append(String.format("%-15s: %s\n", "Order Date", orderDate.format(formatter)));
        sb.append(String.format("%-15s: %s\n", "Arrive Date", eta.format(formatter)));
        sb.append(String.format("%-15s: %s\n", "Status", status));
        sb.append("--------------------------------------------------\n");
        int counter = 1;
        for (MapInterface.Entry<String, Integer> entry : orders) {
            sb.append(String.format("%d. Book ID: %-8s --- Quantity: %d\n", 
                counter, 
                entry.getKey(), 
                entry.getValue()
            ));
            counter++;
        }
        sb.append("--------------------------------------------------\n");
        sb.append(String.format("%-15s: %d\n", "Total Quantity", totalQuantity));
        sb.append(String.format("%-15s: $%,.2f", "Total Price", totalPrice));

        return sb.toString();
    }
}