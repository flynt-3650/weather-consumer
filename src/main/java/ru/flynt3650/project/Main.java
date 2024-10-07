package ru.flynt3650.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    private static HttpEntity<Map<String, Object>> getMapHttpEntity(double temperature, boolean isRaining) {
        // create 'sensor' object
        Map<String, String> sensorMap = new HashMap<>();
        sensorMap.put("name", "sensor new");

        // create 'measurement' object
        Map<String, Object> measurementMap = new HashMap<>();
        measurementMap.put("temperature", temperature);
        measurementMap.put("sensor", sensorMap); // insert 'sensor' object into 'measurement' object
        measurementMap.put("raining", isRaining);

        return new HttpEntity<>(measurementMap);
    }

    public static String getMeasurements() {
        String getUrl = "http://localhost:8080/measurements";
        return REST_TEMPLATE.getForObject(getUrl, String.class);
    }

    public static List<Double> extractTemperatures(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<Double> temperatures = new ArrayList<>();
        for (JsonNode node : rootNode) {
            double temperature = node.path("temperature").asDouble();
            temperatures.add(temperature);
        }

        return temperatures;
    }

    public static void plotTemperatures(List<Double> temperatures) {
        List<Integer> xData = new ArrayList<>();
        for (int i = 0; i < temperatures.size(); i++) {
            xData.add(i + 1);
        }

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Temperature Measurements")
                .xAxisTitle("Measurement Number")
                .yAxisTitle("Temperature")
                .build();

        chart.addSeries("Temperature", xData, temperatures);

        new SwingWrapper<>(chart).displayChart();
    }

    public static void main(String[] args) {
        postNewSensor();
        post100RandomMeasurements();
        String response = getMeasurements();
        List<Double> temperatures = extractTemperatures(response);
        plotTemperatures(temperatures);
    }
}