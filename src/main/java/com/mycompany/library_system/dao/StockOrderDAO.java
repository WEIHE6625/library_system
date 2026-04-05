package com.mycompany.library_system.dao;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.entity.StockOrder;
import com.mycompany.library_system.utility.DataProcessor;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import me.tongfei.progressbar.ProgressBar;

public class StockOrderDAO {
    public static MapInterface<String, StockOrder> readFile(String filePath) {
        
        MapInterface<String, StockOrder> stockOrderTree = new TreeMap<>();

        // CSVReader
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            // Skip Header
            reader.readNext();

            int totalLines = DataProcessor.lineCounter(filePath);
            String[] data;

            try (ProgressBar pb = new ProgressBar("Loading Stock Orders : ", totalLines)) {

                // Read line by line
                while ((data = reader.readNext()) != null) {

                    // Update the progress bar
                    pb.step();

                    if (data.length >= 7) {
                        try {
                            // Parse Fields
                            // ID
                            String id = data[0].trim();

                            // Orders
                            MapInterface<String, Integer> ordersMap = new TreeMap<>();
                            String ordersStr = data[1].trim();
                            if (!ordersStr.isEmpty()) {
                                String[] pairs = ordersStr.split(",");
                                for (int i = 0; i < pairs.length - 1; i += 2) {                                    
                                    // Grab the Key (Index 0, 2, 4...)
                                    String bookId = pairs[i].trim(); 
                                    try {
                                        int quantity = Integer.parseInt(pairs[i + 1].trim());
                                        ordersMap.put(bookId, quantity);
                                        
                                    } catch (NumberFormatException e) {
                                        System.out.println("Warning: Invalid quantity format in CSV for book " + bookId);
                                    }
                                }
                            }
                            
                            // Order Date
                            LocalDateTime orderDate = LocalDateTime.parse(data[2].trim());

                            // Arrive Date
                            LocalDateTime eta = LocalDateTime.parse(data[3].trim());

                            // Status
                            StockOrder.Status status = StockOrder.Status.valueOf(data[4].trim());

                            // Total Quantity
                            int totalQty = 0;
                            try { totalQty = Integer.parseInt(data[5]); } catch (NumberFormatException e) {}
                            
                            // Total Price
                            double totalPrice = 0.0;
                            try { totalPrice = Double.parseDouble(data[6]); } catch (NumberFormatException e) {}

                            // Add to Map
                            StockOrder stockOrder = new StockOrder(id, ordersMap, orderDate, eta, status, totalQty, totalPrice);
                            stockOrderTree.put(id, stockOrder);

                        } catch (Exception e) {
                            System.out.println("Skipping invalid row: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e1) {
            e1.printStackTrace();
        }
        return stockOrderTree;
    }


    public static void writeFile(String filePath, MapInterface<String, StockOrder> orderTree) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            
            // Write Header
            String[] header = { "OrderID", "Orders", "Order Date", "Arrive Date", "Status", "total Qty", "Total Price ($)" };
            writer.writeNext(header, false);

            // Loop TreeMap and Write Data
            for (MapInterface.Entry<String, StockOrder> entry : orderTree) {
                StockOrder order = entry.getValue();

                String[] data = {
                    order.getId(),
                    order.getOrders().toString(),
                    order.getOrderDate().toString(),
                    order.getEta().toString(),
                    order.getStatus().toString(),
                    String.valueOf(order.getTotalQuantity()),
                    String.valueOf(order.getTotalPrice())
                };

                writer.writeNext(data, false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}