package com.example.currencyconverter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.util.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.net.URL;


public class Controller implements Initializable{
    URL apiUrl;
    HttpURLConnection connection;
    Scanner scanner;
    JSONObject allCurrencies;

    @FXML
    private Label resultLabel;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> currencyFrom, currencyTo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            apiUrl = new URL("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies.json");
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if(connection.getResponseCode() == 200) {
                scanner = new Scanner(apiUrl.openStream());
                StringBuilder inline = new StringBuilder();

                while(scanner.hasNext()){
                    inline.append(scanner.nextLine());
                }

                scanner.close();

                JSONParser parse = new JSONParser();
                allCurrencies = (JSONObject) parse.parse(String.valueOf(inline));

                currencyFrom.getItems().addAll(allCurrencies.values());
                currencyTo.getItems().addAll(allCurrencies.values());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resetAmount(){
        resultLabel.setText("");
        amountField.setText("");
    }

    public void convertCurrency(){
        if(currencyFrom.getSelectionModel().getSelectedIndex() != -1 && currencyTo.getSelectionModel().getSelectedIndex() != -1 && (amountField.getText()).matches("-?\\d+(\\.\\d+)?")){
            double currencyAmount = Double.parseDouble(amountField.getText());
            try {
                apiUrl = new URL("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/" + allCurrencies.keySet().toArray()[currencyFrom.getSelectionModel().getSelectedIndex()] + ".json");
                connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if(connection.getResponseCode() == 200) {
                    scanner = new Scanner(apiUrl.openStream());
                    StringBuilder inline = new StringBuilder();

                    while(scanner.hasNext()){
                        inline.append(scanner.nextLine());
                    }

                    scanner.close();

                    JSONParser parse = new JSONParser();
                    JSONObject allRates = (JSONObject) parse.parse(String.valueOf(inline));
                    allRates = (JSONObject) allRates.get(allCurrencies.keySet().toArray()[currencyFrom.getSelectionModel().getSelectedIndex()]);

                    double rate = Double.parseDouble(allRates.get(allCurrencies.keySet().toArray()[currencyTo.getSelectionModel().getSelectedIndex()])+"");
                    resultLabel.setText(currencyAmount + (" " + allCurrencies.keySet().toArray()[currencyFrom.getSelectionModel().getSelectedIndex()]).toUpperCase() +
                            " = " + Math.round((currencyAmount * rate) * 10000.0)/10000.0 + (" " + allCurrencies.keySet().toArray()[currencyTo.getSelectionModel().getSelectedIndex()]).toUpperCase());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}