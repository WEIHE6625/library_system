package com.mycompany.library_system.boundary;

import java.util.Scanner;
import java.util.function.Supplier;
import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.adt.MapInterface;
import com.mycompany.library_system.control.StockOrderController;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.StockOrder;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;
import com.mycompany.library_system.utility.Pagination;

/**
 *
 * @author weihe
 */
public class StockOrderUI {
    
    static Scanner scanner = new Scanner(System.in);
    static Pagination<String, StockOrder> pagination = new Pagination<>();

    public static String inputOrderID(MapInterface<String, StockOrder> stockOrderTree) {
        boolean isValid = false;
        String id = "";
        do {
            System.out.print("Enter Order ID* (0 to exit) >> ");
            id = scanner.nextLine();
            if (id.equals("0")) return null;
            if (!stockOrderTree.containsKey(id)) {
                System.out.println("Order with ID " + id + " does not exist. Please try again.");
            }else{
                isValid = true;
            }
        } while (!isValid);
        return id;
    }

    public static int inputQty() {
        return UserPrompt.inputInteger("Enter Total Quantity Ordered* >> ", 1, 999);
    }
    
    public static int inputReduceQty(int qtyAvailable) {
        System.out.println("\n Current Available Quantity : " + qtyAvailable);
        return UserPrompt.inputInteger("Enter Total Quantity Reduced* >> ", 1, qtyAvailable);
    }

    public static void displayCart(MapInterface<String, Book> bookTree, MapInterface<String, Integer> cart) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is currently empty.\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        UIGenerator.appendCenteredLine(sb, "-".repeat(40) + " YOUR CART " + "-".repeat(40));
        UIGenerator.appendCenteredLine(sb, String.format("+-%-15s-+-%-30s-+-%-10s-+-%-10s-+-%-12s-+", "-".repeat(15), "-".repeat(30), "-".repeat(10), "-".repeat(10), "-".repeat(12)));
        UIGenerator.appendCenteredLine(sb, String.format("| %-15s | %-30s | %-10s | %-10s | %-12s |", "BOOK ID", "TITLE", "PRICE ($)", "QUANTITY", "SUBTOTAL ($)"));
        UIGenerator.appendCenteredLine(sb, String.format("+-%-15s-+-%-30s-+-%-10s-+-%-10s-+-%-12s-+", "-".repeat(15), "-".repeat(30), "-".repeat(10), "-".repeat(10), "-".repeat(12)));
        
        double totalPrice = 0.0;
        for (MapInterface.Entry<String, Integer> entry : cart) {
            String bookId = entry.getKey();
            int qty = entry.getValue();
            
            // Fetch book details to display the title
            Book book = bookTree.get(bookId);
            String title = DataProcessor.truncate(book.getTitle(), 28);
            double price = book.getPrice();
            double subtotal = price * qty;
            totalPrice += subtotal;

            UIGenerator.appendCenteredLine(sb, String.format("| %-15s | %-30s | $%9.2f | %10d | $%11.2f |", bookId, title, price, qty, subtotal));
        }
        UIGenerator.appendCenteredLine(sb, String.format("+-%-15s-+-%-30s-+-%-10s-+-%-10s-+-%-12s-+", "-".repeat(15), "-".repeat(30), "-".repeat(10), "-".repeat(10), "-".repeat(12)));
        UIGenerator.appendCenteredLine(sb, String.format("| %-15s   %-30s   %-10s   %-10s   $%11.2f |", "", "TOTAL:", "", "", totalPrice));
        UIGenerator.appendCenteredLine(sb, String.format("+-%-15s-+-%-30s-+-%-10s-+-%-10s-+-%-12s-+", "-".repeat(15), "-".repeat(30), "-".repeat(10), "-".repeat(10), "-".repeat(12)));
        System.out.print(sb.toString());
    }

    @SuppressWarnings("unchecked")
    public static void menu(StockOrderController rController) {
        boolean isExit = false;
        pagination.setDisplayTree(rController.getOrderTree());
        do {
            UIGenerator.clearScreen();
            orderListPage();
            isExit = UIGenerator.optionMenu(
                new String[]{
                    "[1] Order More Book",
                    "[2] Reduce Book In Stock",
                    "[3] Cancel Order",
                    "[4] Delete All Cancelled Orders",
                    "[5] View Order Details",
                    " ",
                    "[6] Refresh Orders",
                    " ",
                    "[7] <- Previous Page          [8] Next Page ->",
                    "[0] Back to Main Menu"
                },
                "Select an option >> ",
                new Supplier[] {
                        () -> { rController.orderBooks(); return false; },
                        () -> { rController.reduceBookInStock(); return false; },
                        () -> { rController.cancelOrder(); return false; },
                        () -> { rController.deleteAllCancelOrders();; return false; },
                        () -> { rController.viewOrderDetails(); return false; },
                        () -> { rController.refreshOrders(); return false; },
                        () -> { pagination.previousPage(); return false; },
                        () -> { pagination.nextPage(); return false; },
                        () -> true
                }
            );
        }while (!isExit);
    }

    public static void orderListPage() {
        UIGenerator.titleBuilder("Order List");
        System.out.println(pagination.getSearchingWord());
        ListInterface<StockOrder> displayList = pagination.getDisplayTree().values();

        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No orders found in the Order List.");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = pagination.getStartIndex();
        int endIndex = pagination.getEndIndex();

        StringBuilder sBuilder = new StringBuilder();
        UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-23s-+-%-30s-+-%-30s-+-%-9s-+-%-11s-+-%-15s-+", "-".repeat(23), "-".repeat(30), "-".repeat(30), "-".repeat(9), "-".repeat(11), "-".repeat(15)));
        UIGenerator.appendCenteredLine(sBuilder, String.format("| %-23s | %-30s | %-30s | %-9s | %-11s | %-15s |", "Order ID ", "Order Date", "Arrival Date", "Status", "Total Books", "Total Price ($)"));
        UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-23s-+-%-30s-+-%-30s-+-%-9s-+-%-11s-+-%-15s-+", "-".repeat(23), "-".repeat(30), "-".repeat(30), "-".repeat(9), "-".repeat(11), "-".repeat(15)));

        for (int i = startIndex; i <= endIndex; i++) {
            int actualIndex = pagination.isAscending() ? i : (totalRecords - 1 - i);
            StockOrder o = displayList.get(actualIndex);

            
            UIGenerator.appendCenteredLine(sBuilder, String.format("| %-20s | %-30s | %-30s | %-9s | %11d | $%14.2f |", o.getId(), o.getOrderDate(), o.getEta(), o.getStatus(), o.getTotalQuantity(), o.getTotalPrice()));
        }
        UIGenerator.appendCenteredLine(sBuilder, String.format("+-%-23s-+-%-30s-+-%-30s-+-%-9s-+-%-11s-+-%-15s-+", "-".repeat(23), "-".repeat(30), "-".repeat(30), "-".repeat(9), "-".repeat(11), "-".repeat(15)));
        UIGenerator.appendCenteredLine(sBuilder, String.format("Page: %d/%d ------ Total Records: %d\n\n", pagination.getCurrentPage(), pagination.getTotalPages(), displayList.size()));
        System.out.print(sBuilder.toString());
    }

    public static Pagination<String, StockOrder> getPagination() {
        return pagination;
    }
}
