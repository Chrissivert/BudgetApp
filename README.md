# Personal Economy App
![image](https://github.com/user-attachments/assets/1dc1d1fc-6bca-4f35-8ed4-db23451de120)
![image](https://github.com/user-attachments/assets/3879503b-358e-40c3-b1b1-e4ec5b87b6e0)

# Authors & Creation

Created Spring 2023

Created by:
- [Anders Lund](https://github.com/AnderzL7)
- [Daniel Neset](https://github.com/DanielNeset)
- [Jonas Olsen](https://github.com/jonasOlsenNTNU)
- [Brage Solem](https://github.com/BrageSolem)
- Chris Sivert


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

