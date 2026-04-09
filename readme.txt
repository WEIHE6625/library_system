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

Features
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


-----------------------------------------------------------
3. Program
-----------------------------------------------------------
3.1 Setup:
    1. Initialize service:
        In hduser@PC25:~$:
            start-dfs.sh
            start-yarn.sh
            zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties &
            kafka-server-start.sh $KAFKA_HOME/config/server.properties &

            JPS service sample:
            ```
            7110 Jps
            4454 ResourceManager
            3640 SecondaryNameNode
            3402 DataNode
            6506 Kafka
            5964 QuorumPeerMain
            4605 NodeManager
            3213 NameNode
            ```

    2. Create student account in HDFS (If already have a student account, skip this step):
        In hduser@PC25:~$:
            hdfs dfs -mkdir /user/student
            hdfs dfs -chown student:hduser /user/student

    3. Login to student:
        hduser@PC25:~$: su - student
        Password: password

    4. Setup env:
        In student@PC25:~$:
            nano ~/.profile
            Add Contents:
                export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
                export HADOOP_HOME=/home/hduser/hadoop3
                export PATH=$PATH:$HADOOP_HOME/bin
                export PATH=$PATH:$HADOOP_HOME/sbin
                export HADOOP_STREAMING=$HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-3.3.6.jar

                export SPARK_HOME=/home/hduser/spark
                export PATH=$PATH:$SPARK_HOME/bin
                export KAFKA_HOME=/home/hduser/kafka
                export PATH=$PATH:$KAFKA_HOME/bin
            source ~/.profile

    5. Config new venv (If already have this venv, skip this step):
        In student@PC25:~$:
            mkdir assignment-venv
            python3 -m venv assignment-venv
            assignment-venv/bin/python -m pip install -U --quiet pip wheel setuptools ipykernel
            assignment-venv/bin/python -m ipykernel install --user --name "assignment-venv" --display-name "assignment-venv"
            source assignment-venv/bin/activate

    6. Install dependices:
        (assignment-venv) student@PC25:~$ pip install -r requirements.txt

    7. Create folder in student:
        In (assignment-venv) student@PC25:~$:
            mkdir Financial-Transactions-Fraud-Signals
            cp -r /mnt/{your window drive}/{your path}/Financial-Transactions-Fraud-Signals/* ~/Financial-Transactions-Fraud-Signals

    8. Kafka topic manipulation
        a. Create topics
        ./manage_topics.sh create

        b. List out all topics
        ./manage_topics.sh list

        c. Delete all topics
        ./manage_topics.sh delete

        Warning: if occurring this kind of errors:
        -bash: ./manage_topics.sh: /bin/bash^M: bad interpreter: No such file or directory  (due to invisible Windows carriage return (\r)
        found at the end of a line)

        Enter this:
        sed -i -e 's/\r$//' manage_topics.sh (remove the invisible \r and save immediately)

3.2 Prepare the data
    1. Ensure under the student account.

    2. Ensure using the (assignment-venv) virtual environment.

    3. Ensure under the Financial-Transactions-Fraud-Signals directory

    4. Check for the json contents is exist:
        (assignment-venv) student@PC25:~/Financial-Transactions-Fraud-Signals $ head -5 ingestion/transactions.json

        If the file not exist, run the setup function to install the json file:
            (assignment-venv) student@PC25:~/Financial-Transactions-Fraud-Signals $ python main.py setup

3.3 Start Kafka Consumer and Producer
    1. Start Producer Service:
        (assignment-venv) student@PC25:~/Financial-Transactions-Fraud-Signals $ python main.py producer

    2. Open another Powershell and login to student account.

    3. Start Consumer Service:
        (assignment-venv) student@PC25:~/Financial-Transactions-Fraud-Signals $ python main.py consumer {subsribe topic}
        hdfs dfs -cat /user/data/file.json | tail -n 5
        hdfs dfs -cat /user/data/file.json | wc -l

3.4 Run the demo:
    Inside the (assignment-venv) student@PC25:~/Financial-Transactions-Fraud-Signals $
    It is recommended to create a PowerShell for each part.

    3.4.1 Ingestion Part
        Run Consumer with provided topics:
        Run command: python main.py consumer usa/sg/fraud/legit

        Run Producer:
        Run command: python main.py producer

    3.4.2 Stream Processing Part
        When the producer is running:
        Run command: python main.py stream
        Ps. The screen will display the received data stream.

    3.4.3 Batch Processing Part
        When the stream processing is running:
        Run command: python main.py batch
        Ps. The screen will display the received data intermittently.

        To view the report:
        Run command: python main.py analysis

    3.4.4 Serving Layer Part
        When the stream processing is running:
        Run command: python main.py mongodb
        Run command: python main.py neo4j

3.5 Stop Service
    In hduser@PC25:~$
        1. kafka-server-stop.sh
        2. zookeeper-server-stop.sh
        3. stop-yarn.sh
        4. stop-dfs.sh