package ru.flynt3650.project;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    public static void postNewSensor() {
        Map<String, String> newSensor = new HashMap<>();
        newSensor.put("name", "sensor new");

        String sensorPostUrl = "http://localhost:8080/sensors/registration";
        HttpEntity<Map<String, String>> postSensorRequest = new HttpEntity<>(newSensor);

        String postResponse = REST_TEMPLATE.postForObject(sensorPostUrl, postSensorRequest, String.class);
        System.out.println("Post response: " + postResponse);
    }

    public static void post100RandomMeasurements() {
        Random rand = new Random();
        String measurementPostUrl = "http://localhost:8080/measurements/add";

        for (int i = 0; i < 100; i++) {
            double temperature = rand.nextDouble(-100, 100);
            boolean isRaining = rand.nextBoolean();
            HttpEntity<Map<String, Object>> postMeasurementRequest = getMapHttpEntity(temperature, isRaining);

            String postResponse = REST_TEMPLATE.postForObject(measurementPostUrl, postMeasurementRequest, String.class);
            System.out.println("Post response for measurement " + (i + 1) + ": " + postResponse);
        }
    }

    public static void get100RandomMeasurements() {
        String getUrl = "http://localhost:8080/measurements";
        String getResponse = REST_TEMPLATE.getForObject(getUrl, String.class);
        System.out.println("Get response: " + getResponse);
    }

    private static HttpEntity<Map<String, Object>> getMapHttpEntity(double temperature, boolean isRaining) {
        Map<String, Object> sensorMap = new HashMap<>();
        sensorMap.put("name", "sensor new");

        Map<String, Object> measurementMap = new HashMap<>();
        measurementMap.put("temperature", temperature);
        measurementMap.put("sensor", sensorMap);
        measurementMap.put("raining", isRaining);

        return new HttpEntity<>(measurementMap);
    }

    public static void main(String[] args) {
        postNewSensor();
        post100RandomMeasurements();
        get100RandomMeasurements();
    }
}