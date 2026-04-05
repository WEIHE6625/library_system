/**
 *
 * @author Isaac Kuan Way Houng
 */
package com.mycompany.library_system.boundary;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.function.Supplier;

import com.mycompany.library_system.adt.ListInterface;
import com.mycompany.library_system.control.BookBorrowControl;
import com.mycompany.library_system.control.BookInfoController;
import com.mycompany.library_system.entity.Book;
import com.mycompany.library_system.entity.BookBorrowRecord;
import com.mycompany.library_system.entity.BorrowFineRecord;
import com.mycompany.library_system.utility.DataProcessor;
import com.mycompany.library_system.utility.IdGenerator;
import com.mycompany.library_system.utility.Pagination;
import com.mycompany.library_system.utility.UIGenerator;
import com.mycompany.library_system.utility.UserPrompt;

public class BorrowBookUI {
    private static Pagination<String, BookBorrowRecord> borrowRecordsPagination = new Pagination<>();
    private static Pagination<String, BorrowFineRecord> fineRecordsPagination = new Pagination<>();
    private static BookBorrowControl borrowController;
    private static BookInfoController bookInfoController = new BookInfoController();

     

    public static void startNewBorrowSession(String userId) {
        borrowController = new BookBorrowControl(userId, bookInfoController.getbookTree());

    }

    

    //==============================================================================================
    /*
    Member UI
     */

    @SuppressWarnings("unchecked")
    public static void memberMenu(String userId) {
        startNewBorrowSession(userId);
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
                    "[0] Go Back"
                }, 
                "Select an option >> ", 
                new Supplier[] {
                    () -> {searchBookUI(); return false; },
                    () -> {displayBorrowRecordsUI(userId); return false;},
                    () -> {displayFineUI(userId); return false;},
                    () -> true
                }
            );
        }while(!quitMemberMenu);
    } 

    @SuppressWarnings("unchecked")
    public static void searchBookUI() {
        BookUI.getPagination().setDisplayTree(borrowController.getBookTree());
        BookUI.getPagination().setCurrentPage(1);
        boolean quitSearchBook = false;
        while(!quitSearchBook) {
            
            BookUI.bookListPage("member");
            quitSearchBook = UIGenerator.optionMenu(
                new String[] {
                    "[1] Search Books",
                    "[2] Sort Books",
                    "[3] View Book Details",
                    "[4] Borrow Book",
                    "",
                    "[5] <- Previous Page        [6] Next page ->",
                    "[0] Go Back"
                }, "Select an option >>> ", 
                new Supplier[] {
                    () -> {bookInfoController.searchBooks(); return false;},
                    () -> {bookInfoController.sortBooks(); return false;},
                    () -> {bookInfoController.viewBookDetails(); return false;},
                    () -> {borrowUI(borrowController.getUserId()); BookUI.getPagination().setDisplayTree(borrowController.getBookTree()); return false;},
                    () -> {BookUI.getPagination().previousPage(); return false;},
                    () -> {BookUI.getPagination().nextPage(); return false;},
                    () -> true
                }
            );
        }
    }
    
    public static void borrowUI(String userId) {

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

        displayBookDetailsUI(bookId);

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

    public static void displayBookDetailsUI(String bookId) {
        UIGenerator.clearScreen();
        UIGenerator.titleBuilder("Viewing details of Book " + bookId);
        System.out.println(borrowController.getBook(bookId) + "\n");
    }

    @SuppressWarnings("unchecked")
    public static void displayBorrowRecordsUI(String userId) {
        boolean quitDisplayRecordsMenu = false;
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowRecords(userId));
        borrowRecordsPagination.setSearchingWord(null);
        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();
        
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display All Borrow Records",
                    "[2] Search borrow records",
                    "[3] View borrow record details",
                    "[4] Display Current Borrow records",
                    "[5] Display Past Borrow records history",
                    "[6] Display Lost Book Borrow records",
                    "",
                    "[7] <- Previous page       [8] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >> ", 
                new Supplier[] {
                    () -> {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowRecords(userId)); return false;},
                    () -> {
                                final int[] searchOption = {0};
                                searchBorrowRecordUI("Search Borrow Records based on the following criteria", 
                                new String[] {
                                    "[1] Search by Borrow ID",
                                    "[2] Search by Book ID",
                                    "[3] Search by Borrow Date",
                                    "[4] Search by Return Date",
                                    "",
                                    "[0] Go Back"
                                }, "Select an option >>> ", 
                                searchOption, 
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> true
                                }, 
                                new boolean[]{true, true, true, true, false}, false, 0);
                            return false;},
                    () -> {viewBorrowRecordDetailUI(userId, true); return false;},
                    () -> {displayCurrentBorrowRecordsUI(userId, false) ;return false;},
                    () -> {displayCurrentBorrowRecordsUI(userId, true); return false;},
                    () -> {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserIsLostBookRecords(userId)); borrowRecordsPagination.setCurrentPage(1); return false;},
                    () -> {borrowRecordsPagination.previousPage();return false;},
                    () -> {borrowRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
        }while(!quitDisplayRecordsMenu);
    }
    
    @SuppressWarnings("unchecked")
    public static void displayCurrentBorrowRecordsUI(String userId, boolean alreadyReturn) {
        boolean quitDisplayRecordsMenu = false;
        final int[] filterAlreadyReturned = {1};
        String[] optionMenuString = {
            "[1] Search Current Borrow Records",
            "[2] View borrow record details",
            "",
            "[3] <- Previous page     [4] Next Page ->",
            "[0] Go Back"
        };
        String searchingWord = "Pending return";
        if (alreadyReturn) {searchingWord = "Already return";}
        borrowRecordsPagination.setSearchingWord(searchingWord);
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserAlreadyReturnRecords(userId, alreadyReturn));
        
        if (alreadyReturn) {
            optionMenuString[0] = "[1] Search Past Borrow Records";
            filterAlreadyReturned[0] = 2;
        }

        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();
        
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                optionMenuString, 
                "Select an option >> ", 
                new Supplier[] {
                    () -> {
                            final int[] searchOption = {0};
                            searchBorrowRecordUI("Search Borrow Records based on the following criteria", 
                            new String[] {
                                "[1] Search by Borrow ID",
                                "[2] Search by Book ID", 
                                "[3] Search by Borrow Date",
                                "",
                                "[0] Go Back"
                            }, 
                            "Select an option >>> ", 
                            searchOption, 
                            new Supplier[] {
                                () -> {searchOption[0] = 1; return false;},
                                () -> {searchOption[0] = 2; return false;},
                                () -> {searchOption[0] = 3; return false;},
                                () -> true
                            }, 
                            new boolean[]{true, true, true, false, false}, false, filterAlreadyReturned[0]);

                            return false;},
                    () -> {viewBorrowRecordDetailUI(userId, false, alreadyReturn);return false;},
                    () -> {borrowRecordsPagination.previousPage(); return false;},
                    () -> {borrowRecordsPagination.nextPage();return false;},
                    () -> true
                }
            );
        }while(!quitDisplayRecordsMenu);

        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowRecords(userId));
        borrowRecordsPagination.setSearchingWord(null);
    }

    @SuppressWarnings("unchecked")
    public static void viewBorrowRecordDetailUI(String userId, boolean ... returnAll) {
        boolean quit = false;
        String borrowId = null;
        while(!quit) {
            borrowId = UserPrompt.inputString("Enter borrow Id to view details* (0 to go back) >>> ", 8, false);
            if(borrowId.trim().equals("0")) {return;}
            

            BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowId);
            if((borrowRecord == null) || !borrowRecord.getUserId().equals(userId)) {
                System.out.print("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.\n");
                continue;
            }
            if((returnAll[0] == true)) {break;}
            if((returnAll[0] == false) && (borrowRecord.getAlreadyReturned() == returnAll[1])) {break;}
            else {System.out.print("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.\n");}
            
            
        }

        BookBorrowRecord record = borrowController.getBorrowRecord(borrowId);
        Book book = borrowController.getBook(record.getBookId());
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
        System.out.println("    Lost Book ?                       : " + record.isLostBookToString(record.getIsLostBook()));
        System.out.println("\n-------------------------------");
        System.out.println("    Book details:");
        System.out.println("-------------------------------");
        System.out.println("    Book ID                           : " + record.getBookId());
        System.out.println("    Book Title                        : " + DataProcessor.truncate(book.getTitle(), 50));
        System.out.println("    Book Publisher                    : " + DataProcessor.truncate(book.getPublisher(), 50));
        System.out.println("    Book description                  : " + DataProcessor.truncate(book.getDescription(), 50));
        System.out.println("    Book Price (RM)                   : " + book.getPrice());
        System.out.println("    Book Published Date (YYYY-MM)     : " + book.getPublishYear() + "-" + book.getPublishMonth());


        if(record.getIsLostBook()) {
            System.out.println("\nYou have been deemed to have lost this book");
            UIGenerator.pressEnterToContinue();
            return;
        }
        

        if(!record.getAlreadyReturned()) {
            final String finalBorrowId = borrowId;
            UIGenerator.optionMenu(
                new String[] {
                    "[1] Report Book Missing", 
                    "",
                    "[0] Go Back"}, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {reportBookMissingUI(finalBorrowId) ;return false;},
                    () -> true
                }
            );
        }
        
        UIGenerator.pressEnterToContinue();
        
    }

    public static void reportBookMissingUI(String borrowId) {
        BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowId);

        boolean validResponse = false;
        char response = 'N';
        while(!validResponse) {
            response = Character.toUpperCase(UserPrompt.inputChar("Are you sure you have lost this book (Y/N) >>> ", 'y', 'Y', 'n', 'N'));
            validResponse = (response == 'Y') || (response == 'N') ;
        }

        if (response == 'N') {return;}

        
        Book book = borrowController.getBook(borrowRecord.getBookId()); 
        borrowController.setBookLost(borrowId);
        System.out.println("You have been charged a fine of RM" + book.getPrice() + " for losing book " + book.getId() + " " + book.getTitle());
        System.out.println("Please pay the fine promptly.");
    }

    //Fines
    @SuppressWarnings("unchecked")
    public static void displayFineUI(String userId) {
        boolean quitDisplayFinesMenu = false;
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineRecords(userId));
        fineRecordsPagination.setSearchingWord(null);
        do {
            UIGenerator.clearScreen();
            displayFineRecordsSearchListUI();

            
            quitDisplayFinesMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Search fine records",
                    "[2] View fine record details",
                    "[3] Display current fine records",
                    "[4] Display past fine records", 
                    "", 
                    "[5] <- Previous page       [6] Next page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {
                        final int[] searchOption = {0};
                        searchFineRecordUI("Search Fine Records based on the following criteria",
                        new String[] {
                            "[1] Search By Borrow ID", 
                            "[2] Search by Fine Issued Date",
                            "[3] Search by Fine Amount",
                            "[4] Search by Payment Date",
                            "",
                            "[0] Go Back"
                        },"Select an option >>> ", 
                        searchOption, 
                        new Supplier[] {
                            () -> {searchOption[0] = 1; return false;},
                            () -> {searchOption[0] = 2; return false;},
                            () -> {searchOption[0] = 3; return false;},
                            () -> {searchOption[0] = 4; return false;},
                            () -> true
                        }, 
                        new boolean[]{true, false, true, true, true}, false, 0);
                        return false;},
                    () -> {viewFineDetailsUI(userId, true); return false;},
                    () -> {displayExistingFineUI(userId,  false); return false;},
                    () -> {displayExistingFineUI(userId,  true); return false;},
                    () -> {fineRecordsPagination.previousPage(); return false;},
                    () -> {fineRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
        }while(!quitDisplayFinesMenu);
    }

    @SuppressWarnings("unchecked")
    public static void displayExistingFineUI(String userId, boolean finePayed) {
        boolean quitDisplayRecordsMenu = false;
        final int[] filterAlreadyPaid = {1};
        String[] optionMenuString = {
            "[1] Search Current Fine Records",
            "[2] View fine record details",
            "",
            "[3] <- Previous page      [4] Next Page ->",
            "[0] Go Back"
        };
        String searchingWord = "Pending payment";
        if (finePayed) {searchingWord = "Received payment";}
        fineRecordsPagination.setSearchingWord(searchingWord);
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserAlreadyPaidRecords(userId, finePayed));
        if(finePayed) {
            optionMenuString[0] = "[1] Search Past Fine Records";
            filterAlreadyPaid[0] = 2;
        }

        do {
            UIGenerator.clearScreen();
            displayFineRecordsSearchListUI();

            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                optionMenuString, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {
                            final int[] searchOption = {0};
                            searchFineRecordUI("Search Fine Records based on the following criteria", 
                            new String[] {
                                "[1] Search by Borrow ID",
                                "[2] Search by Fine Issued Date",
                                "[3] Search by Fine Amount", 
                                "",
                                "[0] Go Back"

                            }, 
                            "Select an option >>> ", 
                            searchOption, 
                            new Supplier[] {
                                () -> {searchOption[0] = 1; return false;}, 
                                () -> {searchOption[0] = 2; return false;},                    
                                () -> {searchOption[0] = 3; return false;},
                                () -> true
                                
                            }, new boolean[] {true, false, true, true, false}, false, filterAlreadyPaid[0]);
                            return false;
                        },
                    () -> {viewFineDetailsUI(userId, false, finePayed); return false;},
                    () -> {fineRecordsPagination.previousPage(); return false;},
                    () -> {fineRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
        }while(!quitDisplayRecordsMenu);
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineRecords(userId));
        fineRecordsPagination.setSearchingWord(null);
    }

    public static void viewFineDetailsUI(String userId, boolean ... returnAll) {
        boolean quit = false;
        String borrowId = null;
        while(!quit) {
            borrowId = UserPrompt.inputString("Enter borrow Id to view details* (0 to go back) >>> ", 8, false);
            if(borrowId.trim().equals("0")) {return;}

            BorrowFineRecord fineRecord = borrowController.getFineRepo().getBorrowFineRecord(borrowId);
            if((fineRecord == null) || !fineRecord.getUserId().equals(userId)) {
                System.out.println("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.");
                continue;
            }

            if(returnAll[0] == true) {break;}
            if((returnAll[0] == false) && fineRecord.getFinePayed() == returnAll[1]) {break;}
            else {System.out.println("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.");}
        }
        
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
            System.out.println("You were charged RM" + fineRecord.getFine().getFineAmount() + " becuase you returned the book late.");
        }
        else if (borrowController.getBorrowRecord(borrowId).getIsLostBook() == true) {
            System.out.println("You were charged RM" + fineRecord.getFine().getFineAmount() + " because you have lost the book.");
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
        else {
            makeFinePaymentMenuUI(borrowId);
        }
        UIGenerator.pressEnterToContinue();
        System.out.println("\n\n");

    }

    @SuppressWarnings("unchecked")
    public static void makeFinePaymentMenuUI(String borrowId) {
        System.out.println("\n");
        UIGenerator.optionMenu(
            new String[] {
                "[1] Make payment", 
                "",
                "[0] Go Back" 
            }, 
            "Select an option >>> ", 
            new Supplier[] {
                () -> {paymentScreenUI(borrowId);return false;},
                () -> true
            }
        );
    }

    public static void paymentScreenUI(String borrowId) {
        System.out.println("What method would you like to pay by:");
        System.out.println("1. Touch N' Go E-wallet");
        System.out.println("2. FPX Online Banking");
        System.out.println("3. Cash\n");
        System.out.println("[0] Go Back\n");

        int option = UserPrompt.inputInteger("Select an option >>> ", 0, 3);

        if(option == 0) {
            System.out.println("Payment Cancelled.");
            return;
        }

        borrowController.receiveFinePayment(borrowId, LocalDate.now());
        System.out.println("Payment successful. Your fine " + borrowId + " have been paid\n");
    }

    //==============================================================================================



    //==============================================================================================
    /*
    Shared UI
     */


    public static int[] getDateInputUI(LocalDate startDate) {
        boolean validDate = false;
        String dateString;
        int year = -1;
        int month = -1;
        int day = -1;

        while(!validDate) {
            year = -1;
            month = -1;
            day = -1;
            try {
                System.out.println("Enter date to search (After " + startDate + " )");
                System.out.println("*(Leave blank to select all)");
                dateString = UserPrompt.inputString("Enter year to search (from 2017 -- present) >>> ", 4, true);
                if(!dateString.trim().isEmpty()) {
                    year = Integer.parseInt(dateString);
                    if (year < BookBorrowControl.getBorrowStartDate().getYear()) {
                        throw new NumberFormatException();
                    }
                }
                else {break;}

                dateString = UserPrompt.inputString("Enter month to search (1 = Jan ... 12 = Dec) >>> ", 2, true);
                if(!dateString.trim().isEmpty()) {
                    month = Integer.parseInt(dateString);
                    if(year == BookBorrowControl.getBorrowStartDate().getYear() && month < BookBorrowControl.getBorrowStartDate().getMonthValue()) {
                        throw new NumberFormatException();
                    }
                }
                else {break;}
                
                dateString = UserPrompt.inputString("Enter day to search >>> ", 2, true);
                if(!dateString.trim().isEmpty()) {
                    day = Integer.parseInt(dateString);
                    if(year == BookBorrowControl.getBorrowStartDate().getYear() && month == BookBorrowControl.getBorrowStartDate().getMonthValue() && day == BookBorrowControl.getBorrowStartDate().getDayOfMonth()) {
                        throw new NumberFormatException();
                    }
                }
                else {break;}
                
                
                try {
                    dateString = String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                    LocalDate.parse(dateString);
                    validDate = true;
                }
                catch(DateTimeException dateParseError) {
                    System.out.println("Invalid date. Please enter a valid date by following the instructions on the screen correctly.");
                    continue;
                }
                
                
            } catch(NumberFormatException e) {
                System.out.print("Invalid input. Please follow the instructions on the screen correctly\n");
                continue;
            }
    
        }

        return new int[]{year, month, day};
    }


    /* Fine Search
    Search Options:
    1. Search By Borrow ID
    2. Search By User ID (for admin)
    3. Search by Fine Issued Date
    4. Search by Fine Amount
    5. Search by Payment Date
     */

    public static void searchFineRecordUI(String searchTitle, String[] options, String optionInputMessage, final int searchOption[], Supplier<Boolean>[] actions, boolean[] availableSearchOptions, boolean isAdmin, int filterAlreadyPaid) {
        String userId = null;
        if(!isAdmin) {
            userId = borrowController.getUserId();
        }
        if(filterAlreadyPaid < 0 || filterAlreadyPaid > 2) {
            throw new IllegalArgumentException("filterAlreadyPaid argument should be should be 0 = \\\"no filter\\\" or 1 = \\\"filter not yet paid\\\", 2 =\\\" filter already paid\\\"");
        }

        boolean isBack = false;
        do {
            System.out.println(searchTitle);
            isBack = UIGenerator.optionMenu(options, optionInputMessage, actions);

            if(searchOption[0] != 0) {break;}
        }while(!isBack);

        if(isBack) {return;}

        int optionCount = 0;
        for(int i = 0; i < availableSearchOptions.length; i++) {
            if(availableSearchOptions[i] == true) {optionCount++;}
            if(optionCount == searchOption[0]) {searchOption[0] = i+1; break;} 
        }

        if(searchOption[0] == 1) {
            boolean validBorrowId = false;
            String borrowId = null;
            while(!validBorrowId) {
                
                borrowId = UserPrompt.inputString("Enter Borrow ID to search* (0 to go back) >>> ", 8, false);
                
                if(borrowId.trim().equals("0")) {
                    return;
                }

                validBorrowId = true;
            }
            
            borrowRecordsPagination.setSearchingWord("Borrow ID");
            if(isAdmin) {fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getBorrowFineRecords().getRange(borrowId, borrowId+Character.MAX_VALUE));
            } else {
                String keyToSearch = IdGenerator.getKeyId(userId, borrowId);

                if(filterAlreadyPaid == 0) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineRecords(userId).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));
                }
                else if(filterAlreadyPaid == 1) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserAlreadyPaidRecords(userId, false).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));
                }
                else if(filterAlreadyPaid == 2) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserAlreadyPaidRecords(userId, true).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));
                }
            }
            fineRecordsPagination.setCurrentPage(1);
        }
        else if(searchOption[0] == 2) {
            boolean validUserId = false;
            String userSearchId = null;

            while(!validUserId) {
                userSearchId = UserPrompt.inputString("Enter user ID to search* (0 to go back) >>> ", 5, false);
                if(userSearchId.trim().equals("0")) {return;}

                if(!borrowController.isUserExist(userSearchId)) {
                    System.out.println("Please enter a valid User ID to search.\n");
                    UIGenerator.pressEnterToContinue();
                    continue;
                }

                break;
            }

            fineRecordsPagination.setSearchingWord("User ID");
            if(filterAlreadyPaid == 0) {
                fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineRecords(userSearchId));
            }
            else if(filterAlreadyPaid == 1) {
                fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserAlreadyPaidRecords(userSearchId, false));
            }
            else if(filterAlreadyPaid == 2) {
                fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserAlreadyPaidRecords(userSearchId, true));
            }
            fineRecordsPagination.setCurrentPage(1);
            
        }
        else if(searchOption[0] == 3) {
            int[] fineIssuedDate = getDateInputUI(BookBorrowControl.getBorrowStartDate());
            int year = fineIssuedDate[0];
            int month = fineIssuedDate[1];
            int day = fineIssuedDate[2];

            fineRecordsPagination.setSearchingWord("Fine Issued Date");
            if(isAdmin) {fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getFineIssuedDateRecords(year, month, day));}            
            else {
                if(filterAlreadyPaid == 0) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineIssuedDateRecords(userId, year, month, day));
                } else if (filterAlreadyPaid == 1) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineIssuedDateRecords(userId, year, month, day, false));
                } else if (filterAlreadyPaid == 2) {
                    fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserFineIssuedDateRecords(userId, year, month, day, true));
                }
            }
            fineRecordsPagination.setCurrentPage(1);
            
        }
        else if(searchOption[0] == 4) {
            boolean validAmountRange = false;
            double minAmount = 0;
            double maxAmount = BookBorrowControl.getMaxFineChargeable();
            while(!validAmountRange) {
                System.out.println("Enter a range of fine amount to search ()");
                System.out.println("(min = 0.1, max = " + BookBorrowControl.getMaxFineChargeable());
                System.out.println("============================================");
                minAmount = UserPrompt.inputDouble("Enter min. amount to search* >>> ", 0.1, BookBorrowControl.getMaxFineChargeable());
                maxAmount = UserPrompt.inputDouble("Enter max. amount to search* >>> ", minAmount, BookBorrowControl.getMaxFineChargeable());
                validAmountRange = true;
            }

            fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getFineAmountRecords().getRange(IdGenerator.getKeyId(minAmount, ""), IdGenerator.getKeyId(maxAmount, String.valueOf(Character.MAX_VALUE))));
            fineRecordsPagination.setCurrentPage(1);
        }
        else if(searchOption[0] == 5) {
            int[] paymentDate = getDateInputUI(BookBorrowControl.getBorrowStartDate());
            int year = paymentDate[0];
            int month = paymentDate[1];
            int day = paymentDate[2];

            fineRecordsPagination.setSearchingWord("Payment Date");
            if(isAdmin) {fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getPaymentDateRecords(year, month, day));}
            else {
                fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getUserPaymentDateRecords(userId, year, month, day));
            }
        }
        fineRecordsPagination.setCurrentPage(1);
    }

    /* Borrow Search
    Search Options:
    1. Search By Borrow ID
    2. Search By Book ID
    3. Search By Borrow Date
    4. Search By Return Date
    5. Search By User ID (for admin)
     */

    public static void searchBorrowRecordUI(String searchTitle, String[] options, String optionInputMessage, final int searchOption[], Supplier<Boolean>[] actions, boolean[] availableSearchOptions, boolean isAdmin, int filterAlreadyReturned) {
        String userId = null;
        if(!isAdmin) {
            userId = borrowController.getUserId();
        } 
        if(filterAlreadyReturned < 0 || filterAlreadyReturned > 3) {
            throw new IllegalArgumentException("filterAlreadyReturned argument should be 0 = \"no filter\" or 1 = \"filter not yet return\", 2 =\"filter already return\", 3=\"filter is lost book\"");
        }

        boolean isBack = false;

        do{
            System.out.println(searchTitle);
            isBack = UIGenerator.optionMenu(options, optionInputMessage, actions);

            if(searchOption[0] != 0) {break;}

        }while(!isBack);

        if(isBack) {return;}
      
        int optionCount = 0;
        for(int i = 0; i < availableSearchOptions.length; i++) {
            if(availableSearchOptions[i] == true) {optionCount++;}
            if(optionCount == searchOption[0]) {searchOption[0] = i+1; break;} 
        }

        
        if (searchOption[0] == 1) {
            boolean validBorrowId = false;
            String borrowId = null;

            while(!validBorrowId) {
                
                borrowId = UserPrompt.inputString("Enter Borrow ID to search* (0 to go back) >>> ", 8, false);
                
                if(borrowId.trim().equals("0")) {
                    return;
                }

                validBorrowId = true;
            }
            
            borrowRecordsPagination.setSearchingWord("Borrow ID");
            if(isAdmin) {
                if(filterAlreadyReturned == 0) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords().getRange(borrowId, borrowId+Character.MAX_VALUE));}
                else if(filterAlreadyReturned == 1) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnRecords(false).getRange(IdGenerator.getKeyId("false", borrowId), IdGenerator.getKeyId("false", borrowId+Character.MAX_VALUE)));}
                else if(filterAlreadyReturned == 2) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnRecords(true).getRange(IdGenerator.getKeyId("true", borrowId), IdGenerator.getKeyId("true", borrowId+Character.MAX_VALUE)));}
                else if(filterAlreadyReturned == 3) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getLostBookRecords(true).getRange(IdGenerator.getKeyId(true, borrowId), IdGenerator.getKeyId(true, borrowId)+Character.MAX_VALUE));}
            } else {
                String keyToSearch = IdGenerator.getKeyId(userId, borrowId);
                if(filterAlreadyReturned == 0) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowRecords(userId).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));}
                else if(filterAlreadyReturned == 1) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserAlreadyReturnRecords(userId, false).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));}
                else if(filterAlreadyReturned == 2) {borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserAlreadyReturnRecords(userId, true).getRange(keyToSearch, keyToSearch+Character.MAX_VALUE));}
            }
            borrowRecordsPagination.setCurrentPage(1);
            
        }
        else if (searchOption[0] == 2) {
            boolean validBookId = false;
            String bookId = null;
            

            while(!validBookId) {
                bookId = UserPrompt.inputString("Enter book ID to search* (0 to go back) >>> ", 8, false);
                
                if(bookId.trim().equals("0")) {
                    return;
                }

                if(!borrowController.isBookExist(bookId)) {
                    System.out.println("Please enter a valid Book ID to search.\n");
                    UIGenerator.pressEnterToContinue();
                    continue;
                }
                break;
            }
            
            borrowRecordsPagination.setSearchingWord("Book ID");
            if(isAdmin) {
                if(filterAlreadyReturned == 0) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords(bookId));
                }
                else if(filterAlreadyReturned == 1) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords(bookId, false));
                }
                else if(filterAlreadyReturned == 2) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords(bookId, true));
                }
                else if(filterAlreadyReturned == 3) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookIsLostBookRecords(bookId));
                }
                
            } else {
                if (filterAlreadyReturned == 0) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBookRecords(userId, bookId));
                }
                else if(filterAlreadyReturned == 1) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBookRecords(userId, bookId, false));
                }
                else if(filterAlreadyReturned == 2) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBookRecords(userId, bookId, true));
                }
                
            }
            borrowRecordsPagination.setCurrentPage(1);
        }

        else if (searchOption[0] == 3) {
            int[] borrowDate = getDateInputUI(BookBorrowControl.getBorrowStartDate());
            int year = borrowDate[0];
            int month = borrowDate[1];
            int day = borrowDate[2];

            borrowRecordsPagination.setSearchingWord("Borrow Date");
            if(isAdmin) { 
                if(filterAlreadyReturned == 0) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBorrowDateRecords(year, month, day));
                }
                else if(filterAlreadyReturned == 1) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnedBorrowDateRecords(year, month, day, false));
                }
                else if(filterAlreadyReturned == 2) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnedBorrowDateRecords(year, month, day, true));
                }
                else if(filterAlreadyReturned == 3) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBorrowDateIsLostRecords(year, month, day));
                }
            } else {
                if(filterAlreadyReturned == 0) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowDateRecords(userId, year, month, day));
                }
                else if (filterAlreadyReturned == 1) { 
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowDateRecords(userId, year, month, day, false));
                } 
                else if (filterAlreadyReturned == 2) {
                    borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowDateRecords(userId, year, month, day, true));
                }
            }
            borrowRecordsPagination.setCurrentPage(1);
        }
        else if (searchOption[0] == 4) {
            int[] returnDate = getDateInputUI(BookBorrowControl.getBorrowStartDate());
            int year = returnDate[0];
            int month = returnDate[1];
            int day = returnDate[2];

            borrowRecordsPagination.setSearchingWord("Return Date");
            if(isAdmin) {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getReturnDateRecords(year, month, day));
            }else {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserReturnDateRecords(userId, year, month, day));
            }
            borrowRecordsPagination.setCurrentPage(1);
        } 

        if (searchOption[0] == 5) {
            boolean validUserId = false;
            String userSearchId = null;

            while(!validUserId) {
                userSearchId = UserPrompt.inputString("Enter user ID to search* (0 to go back) >>> ", 5, false);
                
                if(userSearchId.trim().equals("0")) {
                    return;
                }

                if(!borrowController.isUserExist(userSearchId)) {
                    System.out.println("Please enter a valid User ID to search.\n");
                    continue;
                }
                break;
            }

            borrowRecordsPagination.setSearchingWord("User ID");
            if(filterAlreadyReturned == 0) {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserBorrowRecords(userSearchId));
            }
            else if (filterAlreadyReturned == 1) {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserAlreadyReturnRecords(userSearchId, false));
            }
            else if (filterAlreadyReturned == 2) {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserAlreadyReturnRecords(userSearchId, true));
            }
            else if (filterAlreadyReturned == 3) {
                borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getUserIsLostBookRecords(userSearchId));
            }
            borrowRecordsPagination.setCurrentPage(1);

        }
    }

    public static void displayBorrowRecordsSearchListUI() {
        UIGenerator.clearScreen();
        ListInterface<BookBorrowRecord> displayList = borrowRecordsPagination.getDisplayTree().values();

        if(!borrowRecordsPagination.getSearchingWord().contains("null")) {
            System.out.print(borrowRecordsPagination.getSearchingWord() + "\n");
        }
        UIGenerator.titleBuilder("Displaying Borrow Records");
        
        if (displayList == null || displayList.isEmpty()) {
            System.out.println("No Matching Records found");
            return
            ;
        }

        int totalRecords = displayList.size();
        int startIndex = borrowRecordsPagination.getStartIndex();
        int endIndex = borrowRecordsPagination.getEndIndex();

        System.out.println("Borrow ID     Book ID    User ID    Borrow Date    Return status    Return Date    Book Lost ?    Return Deadline");
        System.out.println("=========     =======    =======    ===========    =============    ===========    ===========    ===============");

        for (int i = startIndex; i <= endIndex; i++) {
            int actualIndex = borrowRecordsPagination.isAscending() ? i : (totalRecords - 1 - i);

            BookBorrowRecord record = displayList.get(actualIndex);
            System.out.println(record + String.format("%19s", borrowController.calculateReturnDate(record.getBorrowId())));
        }

        System.out.println("-".repeat(160));
        System.out.printf("Page: %d/%d\n\n", borrowRecordsPagination.getCurrentPage(), borrowRecordsPagination.getTotalPages());
    }

    public static void displayFineRecordsSearchListUI() {
        UIGenerator.clearScreen();
        ListInterface<BorrowFineRecord> displayList = fineRecordsPagination.getDisplayTree().values();

        if(!fineRecordsPagination.getSearchingWord().contains("null")) {
            System.out.print(fineRecordsPagination.getSearchingWord() + "\n");
        }

        UIGenerator.titleBuilder("Display Fine Records");

        if(displayList == null || displayList.isEmpty()) {
            System.out.println("No Matching Records found");
            return;
        }

        int totalRecords = displayList.size();
        int startIndex = fineRecordsPagination.getStartIndex();
        int endIndex = fineRecordsPagination.getEndIndex();

        

        System.out.println("Borrow ID     User ID      Fine Issue Date      Fine Amount (RM)      Fine payed status      Payment Date");
        System.out.println("                            (yyyy-MM-dd)                                                     (yyyy-MM-dd)");
        System.out.println("=========     =======      ===============      ================      =================      =============");

        for (int i = startIndex; i <= endIndex; i++) {
            int actualIndex = fineRecordsPagination.isAscending() ? i : (totalRecords - 1 - i);

            BorrowFineRecord record = displayList.get(actualIndex);
            System.out.println(record);
        }

        System.out.println("-".repeat(160));
        System.out.printf("Page: %d/%d\n\n", borrowRecordsPagination.getCurrentPage(), borrowRecordsPagination.getTotalPages());

    }

    
    //==============================================================================================






    //==============================================================================================
    /*
    Admin UI
     */

    @SuppressWarnings("unchecked")
    public static void adminMenu(String adminId) {
        startNewBorrowSession(adminId);
        boolean quitAdminMenu = false;

        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Borrow Book Admin Menu. Manage all Book Borrow Records in one place.");
            quitAdminMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display Borrow Records",                    
                    "[2] Display Fine Records", 
                    "",
                    "[0] Go Back"
                }, 
                "Select an option >> ", 
                new Supplier[] {
                    () -> {adminBorrowMenu(); return false;},
                    () -> {adminFineMenu(); return false;},
                    () -> true
                }
            );
        }while(!quitAdminMenu);
    }

    @SuppressWarnings("unchecked")
    public static void adminBorrowMenu() {
        boolean quitAdminBorrowMenu = false;
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords());
        borrowRecordsPagination.setCurrentPage(1);
        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();
            quitAdminBorrowMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display All borrow records",
                    "[2] Display Current borrow records", 
                    "[3] Display Past borrow records history",
                    "[4] Display Lost Book borrow records",
                    "",
                    "[5] <- Previous page       [6] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {displayAllBorrowRecords();return false;},
                    () -> {displayAllCurrentOrPastBorrowRecordsUI(false); return false;},
                    () -> {displayAllCurrentOrPastBorrowRecordsUI(true); return false;},
                    () -> {displayIsLostBookRecordsUI(); return false;},
                    () -> {borrowRecordsPagination.previousPage(); return false;},
                    () -> {borrowRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
            
            
        }while(!quitAdminBorrowMenu);
    }

    @SuppressWarnings("unchecked")
    public static void displayAllBorrowRecords() {
        boolean quitDisplayRecordsMenu = false;
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords());
        borrowRecordsPagination.setSearchingWord(null);
        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Search borrow records",
                    "[2] View borrow record details",
                    "",
                    "[3] <- Previous page       [4] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ",
                new Supplier[] {
                    () -> {
                            final int[] searchOption = {0};
                            searchBorrowRecordUI("Search Borrow Records based on the following criteria",
                                new String[] {
                                    "[1] Search by Borrow ID",
                                    "[2] Search by Book ID",
                                    "[3] Search by Borrow Date",
                                    "[4] Search by Return Date",
                                    "[5] Search by User ID",
                                    "",
                                    "[0] Go Back"
                                },
                                "Select an option >>> ",
                                searchOption,
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> {searchOption[0] = 5; return false;},
                                    () -> true
                                },
                                new boolean[]{true, true, true, true, true}, true, 0);

                            return false;},
                    () -> {viewBorrowRecordDetailUI(true); return false;},
                    () -> {borrowRecordsPagination.previousPage(); return false;},
                    () -> {borrowRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
        }while(!quitDisplayRecordsMenu);

    }

    @SuppressWarnings("unchecked")
    public static void viewBorrowRecordDetailUI(boolean... returnAll) {
        boolean quit = false;
        String borrowId = null;
        while(!quit) {
            borrowId = UserPrompt.inputString("Enter borrow Id to view details* (0 to go back) >>> ", 8, false);
            if(borrowId.trim().equals("0")) {return;}

            BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowId);
            if(borrowRecord == null) {
                System.out.print("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.\n");
                continue;
            }

            if((returnAll.length == 1) && returnAll[0]) {break;}
            if(returnAll[0] && (borrowRecord.getIsLostBook() == returnAll[1])) {break;}
            if(!returnAll[0] && borrowRecord.getAlreadyReturned() == returnAll[1]) {break;}
            System.out.println("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.");

        }
    

        BookBorrowRecord record = borrowController.getBorrowRecord(borrowId);
        Book book = borrowController.getBook(record.getBookId());
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
        System.out.println("    Lost Book ?                       : " + record.isLostBookToString(record.getIsLostBook()));
        System.out.println("\n-------------------------------");
        System.out.println("    Book details:");
        System.out.println("-------------------------------");
        System.out.println("    Book ID                           : " + record.getBookId());
        System.out.println("    Book Title                        : " + DataProcessor.truncate(book.getTitle(), 50));
        System.out.println("    Book Publisher                    : " + DataProcessor.truncate(book.getPublisher(), 50));
        System.out.println("    Book description                  : " + DataProcessor.truncate(book.getDescription(), 50));
        System.out.println("    Book Price (RM)                   : " + book.getPrice());
        System.out.println("    Book Published Date (YYYY-MM)     : " + book.getPublishYear() + "-" + book.getPublishMonth());


        if(record.getIsLostBook()) {
            final String finalBorrowId = borrowId;
            System.out.println("\nThe book has been reported missing.\n");
            UIGenerator.optionMenu(
                new String[] {
                    "[1] Set Book Not Lost",
                    "",
                    "[0] Go back"
                }, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> { 
                            setBookNotLostUI(finalBorrowId); 
                            UIGenerator.pressEnterToContinue();
                            return false;},
                    () -> true
                }
            );
            return;
        }
        

        if(!record.getAlreadyReturned()) {
            final String finalBorrowId = borrowId;
            UIGenerator.optionMenu(
                new String[] {
                    "[1] Return Book", 
                    "",
                    "[0] Go Back"}, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {returnBookUI(finalBorrowId) ;return false;},
                    () -> true
                }
            );
        }
        
        UIGenerator.pressEnterToContinue();
        
    }
    
    @SuppressWarnings("unchecked")
    public static void displayAllCurrentOrPastBorrowRecordsUI(boolean alreadyReturn) {
        boolean quitDisplayRecordsMenu = false;
        final int[] filterAlreadyReturned = {1};
        String[] optionMenuString = {
            "[1] Search Current Borrow Records",
            "[2] View borrow record details",
            "",
            "[3] <- Previous page    [4] Next page ->",
            "[0] Go Back"
        };
        
        String searchingWord = "Pending return";
        if(alreadyReturn) {searchingWord = "Already return";}
        borrowRecordsPagination.setSearchingWord(searchingWord);
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnRecords(alreadyReturn));

        if(alreadyReturn) {
            optionMenuString[0] = "[1] Search Past Borrow Records";
            filterAlreadyReturned[0] = 2;
        }

        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();

            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                optionMenuString, 
                "Select an option >>> ", 
                new Supplier[] {
                        () -> {
                                final int[] searchOption = {0};
                                searchBorrowRecordUI("Search Borrow Records based on the following criteria", 
                                new String[] {
                                    "[1] Search By Borrow ID",
                                    "[2] Search by Book ID",
                                    "[3] Search by Borrow Date",
                                    "[4] Search by User ID",
                                    "",
                                    "[0] Go Back"
                                }, 
                                "Select an option >>> ", 
                                searchOption, 
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> true
                                }, 
                                new boolean[] {true, true, true, false, true}, true, filterAlreadyReturned[0]);
                                return false;
                            },
                        () -> {viewBorrowRecordDetailUI(false, alreadyReturn); borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getAlreadyReturnRecords(alreadyReturn)); return false;},
                        () -> {borrowRecordsPagination.previousPage();return false;},
                        () -> {borrowRecordsPagination.nextPage();return false;},
                        () -> true
                }
            );
            
        }while(!quitDisplayRecordsMenu);

        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords());   
        borrowRecordsPagination.setSearchingWord(null);
    }
      
    @SuppressWarnings("unchecked")
    public static void displayIsLostBookRecordsUI() {
        boolean quitDisplayRecordsMenu = false;
        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getLostBookRecords(true));
        borrowRecordsPagination.setSearchingWord("Lost Book");
        do {
            UIGenerator.clearScreen();
            displayBorrowRecordsSearchListUI();
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Search borrow records",
                    "[2] View borrow record details",
                    "",
                    "[3] <- Previous page       [4] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ",
                new Supplier[] {
                    () -> {
                            final int[] searchOption = {0};
                            searchBorrowRecordUI("Search Borrow Records based on the following criteria",
                                new String[] {
                                    "[1] Search by Borrow ID",
                                    "[2] Search by Book ID",
                                    "[3] Search by Borrow Date",
                                    "[4] Search by User ID",
                                    "",
                                    "[0] Go Back"
                                },
                                "Select an option >>> ",
                                searchOption,
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> true
                                },
                                new boolean[]{true, true, true, false, true}, true, 3);

                            return false;},
                    () -> {viewBorrowRecordDetailUI(true, true); borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getLostBookRecords(true)); return false;},
                    () -> {borrowRecordsPagination.previousPage(); return false;},
                    () -> {borrowRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
            
        }while(!quitDisplayRecordsMenu);

        borrowRecordsPagination.setDisplayTree(borrowController.getBorrowRepo().getBookBorrowRecords());
        borrowRecordsPagination.setSearchingWord(null);
    }

    @SuppressWarnings("unchecked")
    public static void adminFineMenu() {
        boolean quitAdminFineMenu = false;
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getBorrowFineRecords());
        fineRecordsPagination.setCurrentPage(1);
        do {
            UIGenerator.clearScreen();
            UIGenerator.titleBuilder("Manage Fine Records");
            displayFineRecordsSearchListUI();
            quitAdminFineMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Display All fine records",
                    "[2] Display Current fine records", 
                    "[3] Display Past fine records history",
                    "",
                    "[4] <- Previous page       [5] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ", 
                new Supplier[] {
                    () -> {displayAllFineRecordsUI() ;return false;},
                    () -> {displayAllCurrentOrPastFineRecordsUI(false);return false;},
                    () -> {displayAllCurrentOrPastFineRecordsUI(true);return false;},
                    () -> {fineRecordsPagination.previousPage(); return false;},
                    () -> {fineRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
            
            
        }while(!quitAdminFineMenu);
    }

    
    @SuppressWarnings("unchecked")
    public static void displayAllFineRecordsUI() {
        boolean quitDisplayRecordsMenu = false;
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getBorrowFineRecords());
        fineRecordsPagination.setSearchingWord(null);
        do {
            UIGenerator.clearScreen();
            displayFineRecordsSearchListUI();
            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                new String[] {
                    "[1] Search fine records",
                    "[2] View fine record details",
                    "",
                    "[3] <- Previous page       [4] Next Page ->",
                    "[0] Go Back"
                }, 
                "Select an option >>> ",
                new Supplier[] {
                    () -> {
                            final int[] searchOption = {0};
                            searchFineRecordUI("Search Fine Records based on the following criteria",
                                new String[] {
                                    "[1] Search by Borrow ID",
                                    "[2] Search by User ID",
                                    "[3] Search by Fine Issued Date",
                                    "[4] Search by Fine Amount",
                                    "[5] Search by Payment Date",
                                    "",
                                    "[0] Go Back"
                                },
                                "Select an option >>> ",
                                searchOption,
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> {searchOption[0] = 5; return false;},
                                    () -> true
                                },
                                new boolean[]{true, true, true, true, true}, true, 0);

                            return false;},
                    () -> {viewFineDetailsUI(true); return false;},
                    () -> {fineRecordsPagination.previousPage(); return false;},
                    () -> {fineRecordsPagination.nextPage(); return false;},
                    () -> true
                }
            );
        }while(!quitDisplayRecordsMenu);
    }

    @SuppressWarnings("unchecked")
    public static void displayAllCurrentOrPastFineRecordsUI(boolean finePayed) {
        boolean quitDisplayRecordsMenu = false;
        final int[] filterAlreadyReturned = {1};
        final int[] searchOption = {0};
        String[] optionMenuString = {
            "[1] Search Current Fine Records",
            "[2] View Fine Record Details",
            "",
            "[3] <- Previous page     [4] Next Page ->",
            "[0] Go Back"
        };
        
        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getFinePayedRecords(finePayed));
        String searchingWord = "Pending payment";
        if(finePayed) {searchingWord = "Received payment";}
        fineRecordsPagination.setSearchingWord(searchingWord);

        if(finePayed) {
            optionMenuString[0] = "[1] Search Past Fine Records";
            filterAlreadyReturned[0] = 2;
    
        }

        do {
            UIGenerator.clearScreen();
            displayFineRecordsSearchListUI();

            quitDisplayRecordsMenu = UIGenerator.optionMenu(
                optionMenuString, 
                "Select an option >>> ", 
                new Supplier[] {
                        () -> {
                                
                                searchFineRecordUI("Search Fine Records based on the following criteria", 
                                new String[] {
                                    "[1] Search By Borrow ID",
                                    "[2] Search by User ID",
                                    "[3] Search by Fine Issued Date",
                                    "[4] Search by Fine Amount",
                                    "",
                                    "[0] Go Back"
                                }, 
                                "Select an option >>> ", 
                                searchOption, 
                                new Supplier[] {
                                    () -> {searchOption[0] = 1; return false;},
                                    () -> {searchOption[0] = 2; return false;},
                                    () -> {searchOption[0] = 3; return false;},
                                    () -> {searchOption[0] = 4; return false;},
                                    () -> true
                                }, 
                                new boolean[] {true, true, true, true, false}, true, filterAlreadyReturned[0]);
                                return false;
                            },
                        () -> {viewFineDetailsUI(false, finePayed); fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getFinePayedRecords(finePayed)); return false;},
                        () -> {fineRecordsPagination.previousPage();return false;},
                        () -> {fineRecordsPagination.nextPage();return false;},
                        () -> true
                }
            );
            
        }while(!quitDisplayRecordsMenu);

        fineRecordsPagination.setDisplayTree(borrowController.getFineRepo().getBorrowFineRecords()); 
        fineRecordsPagination.setSearchingWord(null);  
    }

    public static void viewFineDetailsUI(boolean ... returnAll) {
        boolean quit = false;
        String borrowId = null;
        while(!quit) {
            borrowId = UserPrompt.inputString("Enter borrow Id to view details* (0 to go back) >>> ", 8, false);
            if(borrowId.trim().equals("0")) {return;}

            BorrowFineRecord fineRecord = borrowController.getFineRepo().getBorrowFineRecord(borrowId);
            if((fineRecord == null)) {
                System.out.println("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.");
                continue;
            }

            if(returnAll[0] == true) {break;}
            if((returnAll[0] == false) && fineRecord.getFinePayed() == returnAll[1]) {break;}
            else {System.out.println("Invalid borrow Id. Please enter a valid borrow Id, or enter '0' to go back.");}
        }
        
        BookBorrowRecord record = borrowController.getBorrowRecord(borrowId);
        BorrowFineRecord fineRecord = borrowController.getBorrowFineRecord(borrowId);
        UIGenerator.clearScreen();
        
        System.out.println("A fine was charged for the following borrow record:");
        System.out.println("    Borrow ID                         : " + borrowId);
        System.out.println("    Borrow Date (yyyy-MM-dd)          : " + record.getBorrowDate());
        System.out.println("    Return status                     : " + record.alreadyReturnToString(record.getAlreadyReturned()));
        System.out.println("    Return Date (yyyy-MM-dd)          : " + record.dateToString(record.getReturnDate()));
        System.out.println("    Return Deadline (yyyy-MM-dd)      : " + record.getBorrowDate().plusDays(BookBorrowControl.getBorrowDaysAllowed()));

        System.out.println("------------------------------------------------------------");
        if(borrowController.isLateReturn(borrowId)) {
            System.out.println("RM" + fineRecord.getFine().getFineAmount() + " was charged becuase the book was returned late.");
        }
        else if (borrowController.getBorrowRecord(borrowId).getIsLostBook() == true) {
            System.out.println("RM" + fineRecord.getFine().getFineAmount() + " was charged because the book was reported missing.");
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
        
        UIGenerator.pressEnterToContinue();
        System.out.println("\n\n");

    }

    public static void returnBookUI(String borrowId) {
        boolean validResponse = false;
        while(!validResponse) {
            String response = UserPrompt.inputString("\nReturn Book? (Y/N) >>> ", 1, false);

            validResponse = response.trim().toUpperCase().equals("Y") || response.trim().toUpperCase().equals("N");

            if(!validResponse) {System.out.println("Please enter 'Y' or 'N' to confirm action.\n");}

            if(response.trim().toUpperCase().equals("N")) return;
        }
        
        BookBorrowRecord borrowRecord = borrowController.getBorrowRecord(borrowId);
        
        borrowController.returnBook(borrowId, LocalDate.now());
        System.out.println("Book returned");
        if(borrowRecord.getLateReturn() == true) {
            BorrowFineRecord fineRecord = borrowController.getBorrowFineRecord(borrowId);
            System.out.println("The book has been returned past the deadline.");
            System.out.println("A fine of RM" + fineRecord.getFine().getFineAmount() + " has been charged to user " + borrowRecord.getUserId());
        }
    }
    
    public static void setBookNotLostUI(String borrowId) {
        boolean validResponse = false;
        String response = null;
        while(!validResponse) {
            response = UserPrompt.inputString("Are you sure you want to remove book lost status (Y/N)* >>> ", 1, false).toUpperCase();

            if(response.trim().equals("N")) {
                System.out.println("Action cancelled.");
                break;
            }

            validResponse = (response.equals("Y") || response.equals("N"));
        }

        borrowController.setBookNotLost(borrowId);
        System.out.println("The 'Book Lost ?' status of record " + borrowId + " have been set to No");

        
    }
    //==============================================================================================    
    

    
    
}
