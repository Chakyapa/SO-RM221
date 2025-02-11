package org.example.solab1.remote;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MeteoAPI {
    HashMap<String, Double> meteoInfo = new HashMap<>();

    public HashMap<String, Double> getMeteoInfo() throws Exception {
        getInfoFromAPI();
        if(meteoInfo.isEmpty()) {
            System.out.println("Meteo info is empty");
            return null;
        }
        return meteoInfo;
    }

    private void getInfoFromAPI() throws Exception {
        URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=47.0056&longitude=28.8575&current=temperature_2m,relative_humidity_2m,precipitation&timezone=auto&forecast_days=1");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
        JsonObject current = jsonObject.getAsJsonObject("current");

        double temperature = current.get("temperature_2m").getAsDouble();
        double humidity = current.get("relative_humidity_2m").getAsDouble();
        double precipitation = current.get("precipitation").getAsDouble();

        meteoInfo.put("temperature", temperature);
        meteoInfo.put("humidity", humidity);
        meteoInfo.put("precipitation", precipitation);
    }
}
