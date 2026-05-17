package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private TextField cityInputField;
    private Button fetchWeatherButton;
    private Label resultLabel;
    private VBox rootLayout;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void start(Stage primaryStage) {
        // Updated app window name
        primaryStage.setTitle("City Weather Look Up");

        // UI Components
        Label titleLabel = new Label("City Weather Look Up");
        titleLabel.getStyleClass().add("title-label");

        cityInputField = new TextField();
        cityInputField.setPromptText("Enter city (e.g., Houston, Tokyo)");
        cityInputField.setMaxWidth(260);

        fetchWeatherButton = new Button("Get Weather");
        
        resultLabel = new Label("Enter a city to view current conditions.");
        resultLabel.getStyleClass().add("result-label");
        resultLabel.setWrapText(true);

        // Event Handling
        fetchWeatherButton.setOnAction(e -> handleFetchWeather());
        cityInputField.setOnAction(e -> handleFetchWeather());

        // Layout Container
        rootLayout = new VBox(20);
        rootLayout.setPadding(new Insets(30));
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.getChildren().addAll(titleLabel, cityInputField, fetchWeatherButton, resultLabel);

        rootLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #1e1e2f, #252542);");

        Scene scene = new Scene(rootLayout, 360, 320);
        
        // Link the External CSS stylesheet
        String cssPath = getClass().getResource("application.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void handleFetchWeather() {
        String cityName = cityInputField.getText().trim();
        if (cityName.isEmpty()) {
            resultLabel.setText("Please enter a valid city name.");
            return;
        }

        resultLabel.setText("Searching for city data...");
        fetchWeatherButton.setDisable(true);

        Thread networkThread = new Thread(() -> {
            try {
                String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
                String geocodeUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + encodedCity + "&count=1&language=en&format=json";
                
                String geocodeResponse = sendGetRequest(geocodeUrl);
                
                double latitude = parseJsonDouble(geocodeResponse, "\"latitude\":(\\d+\\.\\d+|-\\d+\\.\\d+)");
                double longitude = parseJsonDouble(geocodeResponse, "\"longitude\":(\\d+\\.\\d+|-\\d+\\.\\d+)");
                String resolvedName = parseJsonString(geocodeResponse, "\"name\":\"([^\"]+)\"");
                String country = parseJsonString(geocodeResponse, "\"country\":\"([^\"]+)\"");

                if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
                    updateUI("City not found.\nCheck spelling and try again.", Double.NaN);
                    return;
                }

                String weatherUrl = String.format(
                        "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m", 
                        latitude, longitude
                );
                
                String weatherResponse = sendGetRequest(weatherUrl);
                double tempCelsius = parseJsonDouble(weatherResponse, "\"temperature_2m\":(\\d+\\.\\d+|-\\d+\\.\\d+)");

                if (Double.isNaN(tempCelsius)) {
                    updateUI("Could not read temperature data.", Double.NaN);
                } else {
                    double tempFahrenheit = (tempCelsius * 9.0 / 5.0) + 32.0;
                    
                    String displayString = String.format(
                            "%s, %s\n\n%.1f °C\n%.1f °F", 
                            resolvedName, country, tempCelsius, tempFahrenheit
                    );
                    updateUI(displayString, tempCelsius);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                updateUI("Error loading weather details.", Double.NaN);
            }
        });
        
        networkThread.setDaemon(true);
        networkThread.start();
    }

    private String sendGetRequest(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private void updateUI(String platformMessage, double tempCelsius) {
        Platform.runLater(() -> {
            resultLabel.setText(platformMessage);
            fetchWeatherButton.setDisable(false);
            
            if (!Double.isNaN(tempCelsius)) {
                applyDynamicBackground(tempCelsius);
            }
        });
    }

    private void applyDynamicBackground(double tempCelsius) {
        String style;
        if (tempCelsius <= 10.0) {
            style = "-fx-background-color: linear-gradient(to bottom right, #0f172a, #1e3a8a, #1d4ed8);";
        } else if (tempCelsius > 10.0 && tempCelsius <= 25.0) {
            style = "-fx-background-color: linear-gradient(to bottom right, #1e1e2f, #2d2b55, #4c1d95);";
        } else {
            style = "-fx-background-color: linear-gradient(to bottom right, #111827, #7c2d12, #b91c1c);";
        }
        rootLayout.setStyle(style);
    }

    private double parseJsonDouble(String json, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : Double.NaN;
    }

    private String parseJsonString(String json, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : "Unknown";
    }

    public static void main(String[] args) {
        launch(args);
    }
}