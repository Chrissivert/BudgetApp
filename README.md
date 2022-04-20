# Personal Economy App
IDATA1002_2023_g08



## About this project
This application is created to help users with their economy. 
User can add all their transactions and accounts, and set up budget to help them keep track of their money.
This application even support Nordigen, that can automatically get selected accounts and all transactions from the users bank.
Our application uses Database to store all the users data, so they can connect from different computers.
(They need to be connected to the NTNU network, or use local database. But local database cannot be accessed from other computers).
All the users data is stored securely on NTNUS sql server, and the user can whenever delete all its data, or even get a CSV file of it.

## How to setup
There are two ways to use this application.
You can use Maven -> javafx -> javafx:run.
Or create a executable Jar file:
```
mvn clean comput assemble:single
Java -jar "jarFileName".jar
```
Or you can double-click the jar file.


## How to use
First you select if you want to use NTNUs database, or local database. 
Then you create an account, you can choose to connect to BankId when signup or after in settings.
When signing you can create new transactions, account, category's and budgets.

