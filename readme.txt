===========================================================
Project Title: TARUMT Library Services
===========================================================

Overview

The TARUMT Online Library System is a Java-based application
designed to manage a library efficiently. It provides functionalities
for managing users, books, borrowing records, fines, and notifications.
The system uses a SortedMap-based approach to keep data organized and
allow efficient operations like searching, insertion, deletion, and
generating sorted reports.

The data is stored as key-value pairs, where each key is unique and
mapped to a specific value. Keys are maintained in a sorted order to
support efficient range queries and orderly reporting. The TreeMap data
structure was chosen to implement this ADT due to its optimal balance
of efficiency and ordering features.

-----------------------------------------------------------
Program Instruction
-----------------------------------------------------------
User Module Instructions
    Login
        - First, log in with your account credentials.
    Access User Module
        - Select 1 from the main menu to enter the User Module.
    Admin Users
        Admins can:
            - View a list of all users.
            - Perform CRUD operations (Create, Read, Update, Delete).
            - Sort and search user records.
            - Generate and view reports.
            - Navigate through pages using pagination if the user list is too long.
    Member Users
        Members can:
            - Modify, delete, or view only their own information.
            - Have access limited to three options for personal account management.

-----------------------------------------------------------
Project Folder Structure:
-----------------------------------------------------------
│
├── readme.txt
├── database/
│   ├── reports/
│   │   ├── book_reports/
│   │   └── user_reposts/
│   │
│   ├── book.csv
│   ├── borrowBookRecords.csv
│   ├── borrowFineRecords.csv
│   ├── stock_orders.csv
│   └── users.csv
│
└── src/main/java/com/mycompany.library_system/
    ├── adt/
    │   ├── ArrayList.java
    │   ├── ListInterface.java
    │   ├── MapInterface.java
    │   └── Treemap.java
    │
    ├── boundary/
    │   ├── BookUI.java
    │   ├── BorrowBookUI.java
    │   ├── LibrarySystemUI.java
    │   ├── ReportUI.java
    │   ├── StockOrderUI.java
    │   └── UserUI.java
    │
    ├── control/
    │   ├── BookBorrowControl.java
    │   ├── BookInfoController.java
    │   ├── BookReportController.java
    │   ├── ReportInfoController.java
    │   ├── ReportService.java
    │   ├── StockOrderController.java
    │   ├── UserInfoController.java
    │   └── UserReportController.java
    │
    ├── dao/
    │   ├── BookBorrowDAO.java
    │   ├── BookBorrowFineDAO.java
    │   ├── BookDAO.java
    │   ├── ReportDAO.java
    │   ├── StockOrderDAO.java
    │   └── UserDAO.java
    │
    ├── entity/
    │   ├── Book.java
    │   ├── BookBorrowRecord.java
    │   ├── BookBorrowRepo.java
    │   ├── BorrowFineRecord.java
    │   ├── BorrowFineRepo.java
    │   ├── Fine.java
    │   ├── Report.java
    │   ├── StockOrder.java
    │   └── User.java
    │
    └── utility/
    │   ├── DataProcessor.java
    │   ├── DateUtil.java
    │   ├── IdGenerator.java
    │   ├── Pagination.java
    │   ├── UIGenerator.java
    │   └── UserPrompt.java
    │
    └── Library_system.java