package com.mycompany.library_system.control;

import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.adt.TreeMap;
import com.mycompany.library_system.boundary.BookUI;
import com.mycompany.library_system.boundary.StockOrderUI;
import com.mycompany.library_system.dao.BookDAO;
import com.mycompany.library_system.dao.StockOrderDAO;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.StockOrder;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class StockOrderController {

    private MapInterface<String, StockOrder> stockOrderTree;
    private MapInterface<String, Book> bookTree;
    private MapInterface<String, Book> bookQtyATree;
    private MapInterface<String, Book> bookStoackLeftTree;
    private MapInterface<String, Book> bookQtyTTree;
    public static final String ORDER_FILE_PATH = "database/stock_orders.csv";

    public StockOrderController(MapInterface<String, Book> bookTree, MapInterface<String, Book> bookQtyATree, MapInterface<String, Book> bookQtyTTree) {
        this.stockOrderTree = StockOrderDAO.readFile(ORDER_FILE_PATH);
        refreshOrders();
        this.bookTree = bookTree;
        this.bookQtyATree = bookQtyATree;
        this.bookQtyTTree = bookQtyTTree;
        this.bookStoackLeftTree = sortByStock();
    }

    public void orderBooks() {
        MapInterface<String, Integer> cart = new TreeMap<>();
        boolean isExit = false;

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("STOCK ORDER CART");
            StockOrderUI.displayCart(bookTree, cart);
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Add Book to Cart",
                    "[2] Remove Book from Cart",
                    " ",
                    "[0] Cancel & Back           [3] Confirm Order",
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { addBookToCart(cart); return false; },
                        () -> { removeBookFromCart(cart); return false; },
                        () -> { 
                            if (cart.isEmpty()) {
                                System.out.println("Your cart is empty! Please add books before placing an order.");
                                UserPrompt.inputString("Press Enter to continue...", 0, true);
                            } else {
                                confirmOrder(cart);
                                return true;
                            }
                            return false;
                        },
                        () -> { return true; }
                }
            );
        }while (!isExit);
    }

    public void reduceBookInStock() {
        Book book = null;
        boolean isValid = false;
        do {
            String id = UserPrompt.inputString("Enter Book ID to reduce stock (or '0' to cancel) >> ", 8, false);
            if (id.equals("0")) return;
            book = bookTree.get(id);
            if (book == null) {
                System.out.println("Error: Book ID not found in the library system.");
            }
            isValid = true;
        } while (!isValid);

        int oldQtyA = book.getQtyAvailable();
        int oldQtyT = book.getQtyTotal();
        if (oldQtyA <= 0) {
            System.out.println("The available book in stock is empty. Cannot reduce anymore.");
        } else {
            int qty = StockOrderUI.inputReduceQty(oldQtyA);
            String confirmation = UserPrompt.inputString("Are you sure you want to reduce the stock of '" + book.getId() + "' by " + qty + "? (y/n) >> ", 1, false);
            if (!confirmation.equalsIgnoreCase("y")) {
                return;
            }
            book.reduceQty(qty);

            double oldStockLeftPercentage = book.getQtyTotal() == 0 ? 0 : (double) oldQtyA / oldQtyT;
            double stockLeftPercentage = book.getQtyTotal() == 0 ? 0 : (double) book.getQtyAvailable() / book.getQtyTotal();

            bookStoackLeftTree.remove(IdGenerator.getKeyId(oldStockLeftPercentage, book.getId()));
            bookQtyATree.remove(IdGenerator.getKeyId(oldQtyA, book.getId()));
            bookQtyTTree.remove(IdGenerator.getKeyId(oldQtyT, book.getId()));
            bookStoackLeftTree.put(IdGenerator.getKeyId(stockLeftPercentage, book.getId()), book);
            bookQtyATree.put(IdGenerator.getKeyId(book.getQtyAvailable(), book.getId()), book);
            bookQtyTTree.put(IdGenerator.getKeyId(book.getQtyTotal(), book.getId()), book);
        }
        saveToFile();
    }

    public MapInterface<String, StockOrder> getOrderTree() {
        return stockOrderTree;
    }

    private void addBookToCart(MapInterface<String, Integer> cart) {
        BookUI.getPagination().setDisplayTree(bookStoackLeftTree);
        BookUI.getPagination().setIsAscending(true);
        BookUI.getPagination().setCurrentPage(1);
        BookUI.bookListPage("order");
        boolean isValid = false;
        Book book = null;

        do {
            String bookId = UserPrompt.inputString("\nEnter Book ID to order (or '0' to cancel)  >> ", 8, false);
            if (bookId.equals("0")) return;

            book = bookTree.get(bookId);
            if (book != null) {
                isValid = true;
            } else {
                System.out.println("Error: Book ID not found in the library system.");
            }
        }while(!isValid);

        System.out.println("Selected Book: " + book.getTitle());
        System.out.println("Quantity in Stock: " + book.getQtyAvailable() + " / " + book.getQtyTotal());
        int qty = UserPrompt.inputInteger("Enter quantity to order (or '0' to cancel) >> ", 0, 100);
        if (qty == 0) return;

        if(book.getQtyTotal() + qty > 999) {
            System.out.println("Error: Total quantity cannot exceed 999. Please enter a smaller quantity.");
            return;
        }
    
        cart.put(book.getId(), qty);
        
        System.out.println("Successfully added to cart!");
        UserPrompt.inputString("Press Enter to continue...", 0, true);
    }

    private void removeBookFromCart(MapInterface<String, Integer> cart) {
        if (cart.isEmpty()) return;

        boolean isValid = false;
        do {
            String bookId = UserPrompt.inputString("Enter Book ID to remove from cart (or '0' to cancel) >> ", 8, false);
            if (bookId.equals("0")) return;

            if (cart.get(bookId) != null) {
                isValid = true;
                cart.remove(bookId);
                System.out.println("Book removed from cart.");
            } else {
                System.out.println("Error: Book ID not found in the cart.");
            }
        } while (!isValid);
        UserPrompt.inputString("Press Enter to continue...", 0, true);
    }

    public void viewOrderDetails() {
        UIGenerator.titleBuilder("View Order Details");
        String id = StockOrderUI.inputOrderID(stockOrderTree);
        StockOrder order = stockOrderTree.get(id);
        System.out.println(order.toString());
        UIGenerator.pressEnterToContinue();
    }

    private boolean confirmOrder(MapInterface<String, Integer> cart) {
        int daysToDeliver = (int)(Math.random() * (2.5) + 0.5);
        LocalDateTime etaDate = LocalDateTime.now().plusDays(daysToDeliver);
        
        MapInterface<String, Integer> orders = new TreeMap<>();
        int totalQuantity = 0;
        double totalPrice = 0.0;

        for (MapInterface.Entry<String, Integer> entry : cart) {
            String bookId = entry.getKey();
            int qty = entry.getValue();
            
            totalQuantity += qty;
            totalPrice += bookTree.get(bookId).getPrice() * qty;

            orders.put(bookId, qty);
        }

        placeOrder(orders, totalQuantity, totalPrice, etaDate, daysToDeliver);
        
        return true; // Returning true tells the cart loop to exit back to the main menu
    }

    public void placeOrder(MapInterface<String, Integer> orders, int totalQuantity, double totalPrice, LocalDateTime etaDate, int daysToDeliver) {
        String orderId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + "-" + IdGenerator.generateId(stockOrderTree, "ORD", 6);

        StockOrder newOrder = new StockOrder(orderId, orders, totalQuantity, totalPrice, daysToDeliver);
        stockOrderTree.put(orderId, newOrder);
        saveToFile();
        
        System.out.println("\nOrder placed successfully! ID: " + orderId);
        System.out.println("Expected Arrival Date: " + etaDate.toLocalDate().toString());
        UserPrompt.inputString("Press Enter to continue...", 0, true);
    }

    public boolean receiveOrder(StockOrder order) {
        if (order != null) {
            order.setStatus(StockOrder.Status.DELIVERED);
            
            // Find the actual book in the library and increase its stock
            for (MapInterface.Entry<String, Integer> entry : order.getOrders()) {
                String bookId = entry.getKey();
                int quantity = entry.getValue();
                Book book = bookTree.get(bookId);
                int oldQtyA = book.getQtyAvailable();
                int oldQtyT = book.getQtyTotal();

                if (book != null) {
                    book.addQty(quantity);

                    double oldStockLeftPercentage = book.getQtyTotal() == 0 ? 0 : (double) oldQtyA / oldQtyT;
                    double stockLeftPercentage = book.getQtyTotal() == 0 ? 0 : (double) book.getQtyAvailable() / book.getQtyTotal();

                    bookStoackLeftTree.remove(IdGenerator.getKeyId(oldStockLeftPercentage, book.getId()));
                    bookQtyATree.remove(IdGenerator.getKeyId(oldQtyA, book.getId()));
                    bookQtyTTree.remove(IdGenerator.getKeyId(oldQtyT, book.getId()));
                    bookStoackLeftTree.put(IdGenerator.getKeyId(stockLeftPercentage, book.getId()), book);
                    bookQtyATree.put(IdGenerator.getKeyId(book.getQtyAvailable(), book.getId()), book);
                    bookQtyTTree.put(IdGenerator.getKeyId(book.getQtyTotal(), book.getId()), book);

                    saveToFile();
                }
            }
            return true;
        }
        return false;
    }

    public boolean cancelOrder() {
        boolean isValid = false;
        do {
            String id = StockOrderUI.inputOrderID(stockOrderTree);
            StockOrder order = stockOrderTree.get(id);
            
            if (order != null && order.getStatus() == StockOrder.Status.PENDING) {
                String comfirm = UserPrompt.inputString("Are you sure you want to cancel Order " + id + "? (y/n) >> ", 1, false);
                if (!comfirm.equalsIgnoreCase("y")) {
                    return false;
                }
                order.setStatus(StockOrder.Status.CANCELLED);
                saveToFile();
                isValid = true;
                System.out.println("Order " + id + " has been cancelled.");
                UIGenerator.pressEnterToContinue();
            }
        } while (!isValid);
        
        return true;
    }
    
    public void deleteAllCancelOrders() {
        int deteledCount = 0;
        for (MapInterface.Entry<String, StockOrder> entry : stockOrderTree) {
            StockOrder order = entry.getValue();
            if (order.getStatus() == StockOrder.Status.CANCELLED) {
                stockOrderTree.remove(entry.getKey());
                deteledCount++;
            }
        }
        if (deteledCount > 0) {
            System.out.println("\n" + deteledCount + " cancelled orders are deleted.");
            saveToFile();
        }else{
            System.out.println("\nNo cancelled orders found in the system.");
        }
        UIGenerator.pressEnterToContinue();
    }
    
    public void refreshOrders() {
        LocalDateTime now = LocalDateTime.now();
        int arrivedCount = 0;

        for (MapInterface.Entry<String, StockOrder> entry : stockOrderTree) {
            StockOrder order = entry.getValue();

            if (order.getStatus() == StockOrder.Status.PENDING) {
                if (order.getEta().isBefore(now) || order.getEta().isEqual(now)) {
                    boolean success = receiveOrder(order); 
                    if (success) {
                        arrivedCount++;
                    }
                }
            }
        }

        if (arrivedCount > 0) {
            System.out.println("\n" + arrivedCount + " newly arrived orders are updated.");
            saveToFile();
        }
    }

    private MapInterface<String, Book> sortByStock() {
        MapInterface<String, Book> sortedTree = new TreeMap<>();
        for (MapInterface.Entry<String, Book> entry : bookTree) {
            Book book = entry.getValue();
            double stockLeftPercentage = book.getQtyTotal() == 0 ? 0 : (double) book.getQtyAvailable() / book.getQtyTotal();
            sortedTree.put(IdGenerator.getKeyId(stockLeftPercentage, book.getId()), book);
        }
        return sortedTree;
    }

    public void saveToFile() {
        BookDAO.writeFile(BookInfoController.BOOK_FILE_PATH, bookTree);
        StockOrderDAO.writeFile(ORDER_FILE_PATH, stockOrderTree);
    }
}