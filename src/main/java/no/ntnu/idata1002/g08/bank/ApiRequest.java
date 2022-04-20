package no.ntnu.idata1002.g08.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.AccessException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import no.ntnu.idata1002.g08.data.Account;
import no.ntnu.idata1002.g08.data.Transaction;
import no.ntnu.idata1002.g08.data.TransactionType;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class is used to get data from the API of Nordigen.
 * It contains all the required methods to connect to the
 * banks and receive banking data.
 *
 * @author Daniel Neset
 * @version 27.04.2023
 */
public class ApiRequest {
    private String secretId;
    private String secretKey;
    private String accessKey;

    /**
     * Gets the secretKey and secretId from the database,
     * creates and set the accessKey for the API.
     */
    public ApiRequest() {
        try{
            getIdAndKey();
            this.accessKey = getAccessKey();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    /**
     * Get the secretKey and secretId from the Database.
     *
     * @throws SQLException throws an SQLException if It's unable to connect to the database.
     */
    private void getIdAndKey() {
        //secretKey and Id
        this.secretKey = "97e913f95afff36ac344e1568d589c4be048dc922d19925ce1599b4568b6f2dfbad3ea18fc27dca7acc03ec442eb0fb5777a2f2b6f33fd76f6b5a828d5dcd824";
        this.secretId = "96f9bbdb-437d-47d4-842c-c095c0cab8a1";
    }

    /**
     * Return all the transactions in a LIST
     *
     * @param account The account number object
     * @return return the
     */
    public List<Transaction> getTransactionsAsObject(Account account){
        List<Transaction> transactions = new ArrayList<Transaction>();
        try {
            String url = "https://ob.nordigen.com/api/v2/accounts/" + account.getAccountNumber() + "/transactions/";
            String body = "";
            String transactionList = sendRequest(url, "GET", false, this.accessKey, body);

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject)jsonParser.parse(transactionList);
            JsonElement transactionsJson = jsonObject.get("transactions");
            JsonArray jArray = transactionsJson.getAsJsonObject().get("booked").getAsJsonArray();

            for (int i = 0; i < jArray.size(); i++) {
                JsonObject transactionAmount = jArray.get(i).getAsJsonObject().get("transactionAmount").getAsJsonObject();
                JsonObject booked = jArray.get(i).getAsJsonObject();

                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(booked.get("bookingDate").getAsString());

                double amount;
                String amountAsString = transactionAmount.get("amount").getAsString();
                TransactionType transactionType;
                if(amountAsString.charAt(0) == '-'){
                    transactionType = TransactionType.EXPENSE;
                    amount = Double.parseDouble(transactionAmount.get("amount").getAsString().substring(1));
                }else{
                    transactionType = TransactionType.INCOME;
                    amount = transactionAmount.get("amount").getAsDouble();
                }

                Transaction transaction = new Transaction();
                transaction.setAccount(account);
                transaction.setBankTransactionId(booked.get("internalTransactionId").getAsString());
                transaction.setDate(date);
                transaction.setType(transactionType);
                transaction.setDescription(booked.get("remittanceInformationUnstructured").getAsString());
                transaction.setAmount(amount);
                transaction.setCurrency(transactionAmount.get("currency").getAsString());
                transactions.add(transaction);
            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return transactions;
    }

    /**
     * Return the balance of the account.
     *
     * @param account The accountNumber
     * @return Return the balance of the selected Account
     */
    public String getBalance(String account)
    {
        String balance = null;
        try {
            String url = "https://ob.nordigen.com/api/v2/accounts/" + account + "/balances/";
            String body = "";
            balance = sendRequest(url, "GET", false, this.accessKey, body);
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return balance;
    }

    /**
     * Used to get the balance of the selected account from the API.
     *
     * @param account the account to get the balance from
     * @return Return the balance of the selected account as a double
     */
    public double getBalanceAsDouble(Account account)
    {
        String balance = null;
        double accountBalance = 0;
        try {
            String url = "https://ob.nordigen.com/api/v2/accounts/" + account.getAccountNumber() + "/balances/";
            String body = "";
            balance = sendRequest(url, "GET", false, this.accessKey, body);
            JsonParser jsonParser2 = new JsonParser();

            JsonObject jsonObject = (JsonObject)jsonParser2.parse(balance);
            JsonArray transactions2 = jsonObject.get("balances").getAsJsonArray();
            accountBalance = transactions2.get(0).getAsJsonObject().get("balanceAmount").getAsJsonObject().get("amount").getAsDouble();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return accountBalance;
    }

    /**
     * Return the details of the account
     *
     * @param account The accountNumber
     * @return Return the details of the selected Account
     */
    public String getDetails(String account)
    {
        String details = null;
        try {
            String url = "https://ob.nordigen.com/api/v2/accounts/" + account + "/details/";
            String body = "";
            details = sendRequest(url, "GET", false, this.accessKey, body);
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return details;
    }

    /**
     * Return all the accounts to the user.
     *
     * @param userId The userId to get transactions and accounts
     * @return return all the accounts to the user
     */
    public String getAccount(String userId){
        String account = null;
        try {
            String url = "https://ob.nordigen.com/api/v2/requisitions/" + userId + "/";
            String body = "";
            account = sendRequest(url, "GET", false, this.accessKey, body);

            try{
                Object obj = JSONValue.parse(account);
                JSONObject jsonObject = (JSONObject) obj;
                if(jsonObject.get("status_code").equals(401)){
                    //"Accesstoken out of date"
                }
            }catch (NullPointerException npe){
                //No error
            }

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return account;
    }

    /**
     * Return a link that the user can use to log into the bank and give us permission to get their
     * banking data. This include accounts, account details and transaction data.
     *
     * @param website The website the user wil land on after the bank login
     * @param instituteId The bank id off the users bank
     * @param reference The internal reference to the user, like an uniq id or AUTO INCREMENT from sql
     * @param agreement The user agreement id that can be retrieved from the getEndUserAgreement()
     * @param language The user language, like EN or NO
     * @return return the bank id used to retrieve transactions and accounts
     */
    public String getLink(String website, String instituteId, String reference, String agreement, String language)
    {
        String link = null;
        try{
            String url = "https://ob.nordigen.com/api/v2/requisitions/";
            String body = "{\n    \"redirect\": \"" +website + "\",\n    \"institution_id\": \"" + instituteId + "\",\n    \"reference\": \""+reference+"\",\n    \"agreement\": \"" + agreement + "\",\n    \"user_language\": \""+language+"\"\n}";
            link = sendRequest(url, "POST", true, this.accessKey, body);

        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return link;
    }

    /**
     * Return Json data that contains the endUserAgreement.
     *
     * @param instituteId The instituteId of the Bank
     * @param history The history of how many days back you can get data from the bank
     * @param expire The expiry date off when the end user agreement need to be renewed
     * @return return json data that contains the end user agreement ID used in the getLink()
     */
    public String getEndUserAgreement(String instituteId, String history, String expire) throws AccessException
    {
        String endUserAgreement = null;
        try{
            String url = "https://ob.nordigen.com/api/v2/agreements/enduser/";
            String body = "{\n    \"institution_id\": \""+instituteId+"\",\n    \"max_historical_days\": \""+history+"\",\n    \"access_valid_for_days\": \""+expire+"\",\n    \"access_scope\": [\n        \"balances\",\n        \"details\",\n        \"transactions\"\n    ]\n}";
            endUserAgreement = sendRequest(url, "POST", true, this.accessKey, body);
            try{
                Object obj = JSONValue.parse(endUserAgreement);
                JSONObject jsonObject = (JSONObject) obj;
                endUserAgreement = (String) jsonObject.get("id");
            } catch (Exception e) {
                throw new AccessException("Was not able to get the UserAgreement.");
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return endUserAgreement;
    }

    /**
     * Return a list of all available banks in the form of JSON-data.
     *
     * @return return the list of all available banks
     */
    public String getBankList()
    {
        String bankList = null;
        try{
            bankList = sendRequest("https://ob.nordigen.com/api/v2/institutions/?country=no", "GET", false, this.accessKey, "");
            try{
                Object obj = JSONValue.parse(bankList);
                JSONObject jsonObject = (JSONObject) obj;
                if(jsonObject.get("status_code").equals(401)){
                    getAccessKey();
                    bankList = sendRequest("https://ob.nordigen.com/api/v2/institutions/?country=no", "GET", false, this.accessKey, "");
                }
            }catch (ClassCastException cce){}
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return bankList;
    }

    /**
     * Return a Json-List that contains the accessKey. The accessKey is used
     * in other request to get data from the API.
     *
     * @return return the accessKey
     */
    public String getAccessKey() throws IllegalArgumentException, IOException {
        String accessKey = null;
        try {
            accessKey = sendRequest("https://ob.nordigen.com/api/v2/token/new/", "POST", true, "", "{\n    \"secret_id\": \"" + this.secretId + "\",\n    \"secret_key\": \"" + this.secretKey + "\"\n}");
        }catch (IOException ioe){
            throw new IOException("Unable to send request to the API");
        }
        Object obj = JSONValue.parse(accessKey);
        JSONObject jsonObject = (JSONObject) obj;
        if((String) jsonObject.get("summary") == "Authentication failed"){
            throw new IllegalArgumentException("SecretKey or SecretID is incorrect.");
        }else {
            accessKey = (String) jsonObject.get("access");
        }
        return accessKey;
    }


    /**
     * Helper class that is used to post or get request.
     *
     * @param site The URL to the API Site
     * @param type The type of request to send, POST or GET
     * @param contentType The content type is application/json or none
     * @param accessKey The accessKey for the API request
     * @param write The body of the request
     * @return Return data from the API in form off JSON data
     * @throws IOException Throws IOException if it cannot do the send the request
     */
    private String sendRequest(String site, String type, Boolean contentType, String accessKey, String write) throws IOException {
            URL url = new URL(site);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod(type);

            httpConn.setRequestProperty("Accept", "application/json");
            if (contentType) {
                httpConn.setRequestProperty("Content-Type", "application/json");
            }
            if (!accessKey.isBlank()) {
                httpConn.setRequestProperty("Authorization", "Bearer " + accessKey);
            }
            if (!write.isBlank()) {
                httpConn.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
                writer.write(write);
                writer.flush();
                writer.close();
                httpConn.getOutputStream().close();
            }

            InputStream responseStream = httpConn.getResponseCode() / 100 == 2
                    ? httpConn.getInputStream()
                    : httpConn.getErrorStream();
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            String response = s.hasNext() ? s.next() : "";

        return response;
    }

}

