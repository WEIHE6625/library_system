package com.mycompany.library_system.boundary;
import com.mycompany.library_system.control.BookBorrowControl;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.entity.User;
import com.mycompany.library_system.utility.*;

import java.time.LocalDate;

public class BorrowBookUI {
    private static BookBorrowControl borrowController;

    public static void startNewBorrowSession(String userId) {
        borrowController = new BookBorrowControl(userId);
    }

    public static void memberMenu(String userId) {
    
        boolean quitMemberMenu = false;

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Borrow new Books!! What would you like to read today?");
            quitMemberMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Borrow a new book", 
                    "[2] Display borrow records",
                    "[3] Display fine records",
                    " ",
                    "[0] Back to Main Menu"
                }, 
                "Select an option >> ", 
                new Runnable[] {
                    () -> borrowUI(userId),
                    () -> displayBorrowRecordsUI(userId),
                    () -> displayFineUI(userId),
                    () -> {}
                }
            );
        }while(!quitMemberMenu);
    } 

    public static void borrowUI(String userId) {
        //display and search for books

        String bookId;
        do {
            System.out.println("\n(Enter 0 to go back)");
            bookId = UserPrompt.inputString("Enter Book ID* >>> ", 8, false);

            if(bookId.charAt(0) == '0') {
                System.out.println("Cancelling borrow book action...");
                UIGenerator.pressEnterToContinue();
                return;
            }

            if (!borrowController.isBookExist(bookId)) {
                System.out.println("Book ID not found. \nPlease enter the correct Book ID. \nYou can find the Book ID by searching the book's details.");
            }
        }while(!borrowController.isBookExist(bookId));

        displayBookDetailsUI(bookId, borrowController);

        boolean validResponse;
        do{
            String response = UserPrompt.inputString("Do you confirm to borrow this book* (Y/N) >>> ", 1, false).toUpperCase();
            validResponse = response.trim().equals("Y") || response.trim().equals("N");

            if(!validResponse) {
                System.out.println("Please enter either \"Y\" or \"N\" to confirm your action.\n");
            }

            if(response.trim().equals("N")) {
                System.out.println("Cancelling borrow book action...");
                UIGenerator.pressEnterToContinue();
                return;
            }

        }while(!validResponse);

        LocalDate borrowDate = LocalDate.now();
        if(!borrowController.borrow(userId, bookId, borrowDate)) {
            UIGenerator.pressEnterToContinue();
            return;
        }
        
        UIGenerator.clearScreen();
        System.out.println("You have sucessfully borrowed this book!! Enjoy and Have fun reading it!!");
        System.out.println("Friendly reminder: Please return the book by " + borrowDate.plusDays(BookBorrowControl.getBorrowDaysAllowed()));

        UIGenerator.pressEnterToContinue();
         
    }

    public static void displayBookDetailsUI(String bookId, BookBorrowControl bookBorrowController) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Viewing details of Book " + bookId);
        System.out.println(bookBorrowController.getBook(bookId) + "\n");
    }

    public static void displayBorrowRecordsUI(String userId) {
        boolean quitDisplayRecordsMenu = false;
        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Displaying Borrowing Records");
        
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display all borrow records",
                    "[2] Display current borrow records",
                    "[3] Display past borrow records",
                    "",
                    "[0] Back to Main Menu"
                }, 
                "Select an option >> ", 
                new Runnable[] {
                    () -> {displayAllBorrowRecordsUI(userId, borrowController);},
                    () -> {displayCurrentBorrowRecordsUI(userId, borrowController);},
                    () -> {displayPastBorrowRecordsUI(userId, borrowController);},
                    () -> {}
                }
            );
        }while(!quitDisplayRecordsMenu);
    }

    // Stop here, Problem: when showing details, users can search for other borrow records that are not theirs.
    public static void displayAllBorrowRecordsUI(String userId, BookBorrowControl borrowControl) {
        
        boolean quitCheckRecordDetails = false;

        while(!quitCheckRecordDetails) {
            printBorrowRecordsHeaderUI("Displaying all borrow records"); 
            borrowController.displayUserBorrowRecords(userId); 
            System.out.println("\n");
            String borrowId = UserPrompt.inputString("Enter Borrow ID to view details* (0 to go back): ", 8, false);

            if(borrowId.trim().equals("0")) {
                return;
            }

            if(borrowControl.isRecordExist(borrowId) && borrowControl.getBorrowRecord(borrowId).getUserId().equals(userId)) {
                viewBorrowRecordDetailUI(borrowId, borrowControl);
                UIGenerator.pressEnterToContinue();
            }
            else {
                System.out.println("Invalid Borrow ID. Unable to view details of non-existing record. \nPlease try entering a valid Borrow ID");
            }
        }

        
    }

    public static void displayCurrentBorrowRecordsUI(String userId, BookBorrowControl borrowControl) {
        
        boolean quitCheckRecordDetails = false;

        while(!quitCheckRecordDetails) {
            printBorrowRecordsHeaderUI("Displaying all current borrow records"); 
            borrowController.displayCurrentOrPastRecords(userId, false);
            System.out.println("\n");
            String borrowId = UserPrompt.inputString("Enter Borrow ID to view details* (0 to go back): ", 8, false);

            if(borrowId.trim().equals("0")) {
                return;
            }
            
            if(borrowControl.isRecordExist(borrowId) && borrowControl.getBorrowRecord(borrowId).getUserId().equals(userId) && borrowControl.getBorrowRecord(borrowId).getAlreadyReturned() == false) {
                viewBorrowRecordDetailUI(borrowId, borrowControl);
                UIGenerator.pressEnterToContinue();
            }
            else {
                System.out.println("Invalid Borrow ID. Unable to view details of non-existing record. \nPlease try entering a valid Borrow ID");
            }
        }

    }

    public static void displayPastBorrowRecordsUI(String userId, BookBorrowControl borrowControl) {
        
        boolean quitCheckRecordDetails = false;

        while(!quitCheckRecordDetails) {
            printBorrowRecordsHeaderUI("Displaying borrow records history"); 
            borrowController.displayCurrentOrPastRecords(userId, true);
            System.out.println("\n");
            String borrowId = UserPrompt.inputString("Enter Borrow ID to view details* (0 to go back): ", 8, false);

            if(borrowId.trim().equals("0")) {
                return;
            }
            
            if(borrowControl.isRecordExist(borrowId) && borrowControl.getBorrowRecord(borrowId).getUserId().equals(userId) && borrowControl.getBorrowRecord(borrowId).getAlreadyReturned() == true) {
                viewBorrowRecordDetailUI(borrowId, borrowControl);
                UIGenerator.pressEnterToContinue();
            }
            else {
                System.out.println("Invalid Borrow ID. Unable to view details of non-existing record. \nPlease try entering a valid Borrow ID");
            }
        }
    }

    public static void viewBorrowRecordDetailUI(String borrowId, BookBorrowControl borrowControl) {
        BookBorrowRecord record = borrowControl.getBorrowRecord(borrowId);
        Book book = borrowControl.getBook(record.getBookId());
        UIGenerator.clearScreen();
        System.out.println("===================");
        System.out.println("|| Borrow Details ||");
        System.out.println("===================");
        System.out.println("    Borrow ID                         : " + borrowId);
        System.out.println("    User ID                           : " + record.getUserId());
        System.out.println("    Borrow Date (yyyy-MM-dd)          : " + record.getBorrowDate());
        System.out.println("    Return status                     : " + record.alreadyReturnToString(record.getAlreadyReturned()));
        System.out.println("    Return Date (yyyy-MM-dd)          : " + record.dateToString(record.getReturnDate()));
        System.out.println("    Return Deadline (yyyy-MM-dd)      : " + record.getBorrowDate().plusDays(BookBorrowControl.getBorrowDaysAllowed()));
        System.out.println("    Lost Book ?                       : " + record.getIsLostBook());
        System.out.println("\n-------------------------------");
        System.out.println("    Book details:");
        System.out.println("-------------------------------");
        System.out.println("    Book ID                           : " + record.getBookId());
        System.out.println("    Book Title                        : " + DataProcessor.truncate(book.getTitle(), 50));
        System.out.println("    Book Publisher                    : " + DataProcessor.truncate(book.getPublisher(), 50));
        System.out.println("    Book description                  : " + DataProcessor.truncate(book.getDescription(), 50));
        System.out.println("    Book Price (RM)                   : " + book.getPrice());
        System.out.println("    Book Published Date (YYYY-MM)     : " + book.getPublishYear() + "-" + book.getPublishMonth());
        
    }

    public static void printBorrowRecordsHeaderUI(String title) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder(title);
        System.out.println("Borrow ID     Book ID    User ID    Borrow Date    Return status    Return Date    Return Deadline");
        System.out.println("=========     =======    =======    ===========    =============    ===========    ===============");
    }


    public static void displayFineUI(String userId) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Displaying Fine Records");
        
        UIGenerator.optionMenu(
            new String[] {
                "[1] View existing fines",
                "[2] Display fine payment history",
                "",
                "[0] Back to Main Menu"
            }, 
            "Select an option >> ", 
            new Runnable[] {
                () -> displayExistingFineUI(userId),
                () -> displayFinePaymentHistoryUI(userId),
                () -> {}
            }
        );

    }

    public static void printFineRecordsHeaderUI(String title) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder(title);
        System.out.println("Borrow ID      Fine Issue Date      Fine Amount (RM)      Fine payed status      Payment Date");
        System.out.println("                (yyyy-MM-dd)                                                     (yyyy-MM-dd)");
        System.out.println("=========      ===============      ================      =================      =============\n");
    }

    public static void displayExistingFineUI(String userId) {
        
        boolean exitFineMenu = false;

        while(!exitFineMenu) {
            printFineRecordsHeaderUI("Displaying all fine records");
            borrowController.displayPendingOrPayedFineRecords(userId, false);
            String response = UserPrompt.inputString("Enter borrow Id to view fine details* (0 to go back) : ", 8, false);

            if(response.trim().equals("0")) {
                return;
            }

            if(borrowController.isFineRecordExist(response) && borrowController.getBorrowRecord(response).getUserId().equals(userId) && borrowController.getBorrowFineRecord(response).getFinePayed() == false) {
                viewFineDetailsUI(response);
                UIGenerator.pressEnterToContinue();
                makeFinePaymentMenuUI(response);
            } else {
                System.out.println("Invalid Borrow ID. Unable to view details of non-existing fine record. \nPlease try entering a valid Borrow ID to view fine details.");
                UIGenerator.pressEnterToContinue();
            }
            
        }
        
    }

    public static void displayFinePaymentHistoryUI(String userId) {
        printFineRecordsHeaderUI("Displaying all fine records");
        borrowController.displayPendingOrPayedFineRecords(userId, true);
        return;
    }

    public static void viewFineDetailsUI(String borrowId) {
        BookBorrowRecord record = borrowController.getBorrowRecord(borrowId);
        BorrowFineRecord fineRecord = borrowController.getBorrowFineRecord(borrowId);
        UIGenerator.clearScreen();
        
        System.out.println("You were charged a fine for the following borrow record:");
        System.out.println("    Borrow ID                         : " + borrowId);
        System.out.println("    Borrow Date (yyyy-MM-dd)          : " + record.getBorrowDate());
        System.out.println("    Return status                     : " + record.alreadyReturnToString(record.getAlreadyReturned()));
        System.out.println("    Return Date (yyyy-MM-dd)          : " + record.dateToString(record.getReturnDate()));
        System.out.println("    Return Deadline (yyyy-MM-dd)      : " + record.getBorrowDate().plusDays(BookBorrowControl.getBorrowDaysAllowed()));

        System.out.println("------------------------------------------------------------");
        if(borrowController.isLateReturn(borrowId)) {
            System.out.println("You were charged RM" + fineRecord.getFine().getFineAmount() + "becuase you returned the book late.");
        }
        else if (borrowController.getBorrowRecord(borrowId).getIsLostBook() == true) {
            System.out.println("You were charged RM" + fineRecord.getFine().getFineAmount() + "because you have lost the book.");
        }
        System.out.println("------------------------------------------------------------\n");
        
        System.out.println("==================");
        System.out.println("|| Fine Details ||");
        System.out.println("==================\n");
        System.out.println("    Fine issued date                  : " + fineRecord.getFine().getFineDate());
        System.out.println("    Fine amount (RM)                  : " + fineRecord.getFine().getFineAmount());
        System.out.println("    Fine payment status               : " + fineRecord.finePayedToString(fineRecord.getFinePayed()));

        if(fineRecord.getFinePayed() == true) {
        System.out.println("    Fine payment date (yyyy-MM-dd)    : " + fineRecord.getPaymentDate());
        }
        System.out.println("\n\n");

    }

    public static void makeFinePaymentMenuUI(String borrowId) {
        UIGenerator.clearScreen();
        UIGenerator.optionMenu(
            new String[] {
                "[1] Make payment", 
                "",
                "[0] Back to Main Menu" 
            }, 
            "Select an option >>> ", 
            new Runnable[] {
                () -> {paymentScreenUI(borrowId);},
                () -> {}
            }
        );
    }

    public static void paymentScreenUI(String borrowId) {
        BorrowFineRecord fineRecord = borrowController.getBorrowFineRecord(borrowId);
        System.out.println("What method would you like to pay by:");
        System.out.println("1. Touch N' Go E-wallet");
        System.out.println("2. FPX Online Banking");
        System.out.println("3. Cash\n");
        System.out.println("[0] Back to Main Menu\n");

        int option = UserPrompt.inputInteger("Select an option >>> ", 0, 3);

        if(option == 0) {
            System.out.println("Payment Cancelled.");
            UIGenerator.pressEnterToContinue();
            return;
        }

        fineRecord.receiveFinePayment(LocalDate.now());
        System.out.println("Payment successful. Your fine " + borrowId + " have been paid\n");
        UIGenerator.pressEnterToContinue();
    }

    public static void tempUI(String userId) {
        UIGenerator.clearScreen();
        displayCurrentBorrowRecordsUI(userId, borrowController);

        String borrowID = UserPrompt.inputString("Enter borrow ID* (0 to go back): ", 8, false);
        BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowID);
        if(borrowRecord != null && borrowRecord.getUserId().equals(userId) && borrowRecord.getAlreadyReturned() == false) {
            returnBookUI(borrowID);
        }
        

        
    }

    public static void returnBookUI(String borrowId) {
        BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowId);
        
        borrowController.returnBook(borrowId, LocalDate.now());
        System.out.println("Book returned");
        if(borrowRecord.getLateReturn() == true) {
            BorrowFineRecord fineRecord = borrowController.getBorrowFineRecord(borrowId);
            System.out.println("You have borrowed this book past the return deadline.");
            System.out.println("A fine of RM" + fineRecord.getFine().getFineAmount() + " has been charged to your account.");
        }
        UIGenerator.pressEnterToContinue();
    }
    
    public static void adminMenu(String adminId) {
        boolean quitAdminMenu = false;

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Borrow Book Admin Menu. Manage all Book Borrow Records in one place.");
            quitAdminMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display past borrow records", 
                    "[2] Display current borrow records",
                    "[3] Display past borrow fine records",
                    "[4] Display pending borrow fine records",
                    " ",
                    "[0] Back to Main Menu"
                }, 
                "Select an option >> ", 
                new Runnable[] {
                    () -> {},
                    () -> {},
                    () -> {},
                    () -> {},
                    () -> {}
                }
            );
        }while(!quitAdminMenu);
    }

    
    public static void displayAllBorrowRecordsUI() {

    }

}
