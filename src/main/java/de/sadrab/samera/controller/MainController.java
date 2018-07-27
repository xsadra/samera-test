package de.sadrab.samera.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sadrab.samera.parser.NginxParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private RestTemplate rest;

    private NginxParser nginxParser;

    @Value("${baseUrl.v1}")
    private String v1Url;

    @Value("${baseUrl.v2}")
    private String v2Url;

    public MainController(RestTemplate rest, NginxParser nginxParser) {
        this.rest = rest;
        this.nginxParser = nginxParser;
    }

    public void run() {

        /// Task 1 (Result)
        nginxParser.getListOfRequests().forEach(System.out::println);

        /// Task 2
        List<String> listOfRequests = nginxParser.getListOfRequests();

        List<String> requests = filterByGetRequest(listOfRequests);

        requests.forEach(request -> {

            System.out.println("----[ " + request + " ]----");

            Map<String, String> response = sendGetRequest(request, v1Url, v2Url);

            /// Result for Task 2
//            printResponse(request, response);

            String stringCompareResult = stringCompare(response);

            /// Result for Task 3
//            printResult(request, stringCompareResult);

            /// Task 4/5
            Optional<List<String>> jsonCompareResult = jsonCompare(response);

            /// Result for Task 4/5
            if (jsonCompareResult.isPresent()) {
                jsonCompareResult.get().forEach(System.out::println);
            }

            System.out.println();
        });

    }

    private Optional<List<String>> jsonCompare(Map<String, String> response) {

        if ((response.get("v1").equals("ERROR")) || (response.get("v2").equals("ERROR"))) {
            return Optional.of(Arrays.asList("V1 | V2 Has some Error! [ ERROR ]"));
        }

        JsonNode jsonNode1 = getNodes(response.get("v1"));

        JsonNode jsonNode2 = getNodes(response.get("v2"));

        if (jsonNode1 == null || jsonNode2 == null) {
            return Optional.empty();
        }

        Iterator<String> nodeFieldNames1 = jsonNode1.fieldNames();

        Iterator<String> nodeFieldNames2 = jsonNode2.fieldNames();

        List<String> resultBaseOnV1 = nodeComparison(jsonNode1, jsonNode2, nodeFieldNames1);

        List<String> resultBaseOnV2 = nodeComparison(jsonNode2, jsonNode1, nodeFieldNames2);

        List<String> result = new ArrayList<>();

        /// Task 5
        if (resultBaseOnV1.size() == resultBaseOnV2.size()) {
            result.addAll(resultBaseOnV1);
        } else if (resultBaseOnV1.size() > resultBaseOnV2.size()) {

            resultBaseOnV1.forEach(item -> {
                if (resultBaseOnV2.contains(item)) {
                    result.add(item);
                } else {
                    result.add(item + " { THIS NODE IS NOT IN V2 }");
                }
            });

        } else {

            resultBaseOnV2.forEach(item -> {
                if (resultBaseOnV1.contains(item)) {
                    result.add(item);
                } else {
                    result.add(item + " { THIS NODE IS NOT IN V1 }");
                }
            });

        }

        return Optional.of(result);
    }

    private List<String> nodeComparison(JsonNode source, JsonNode comparison, Iterator<String> nodeFieldNames) {

        List<String> result = new ArrayList<>();

        nodeFieldNames.forEachRemaining(fieldName -> {

            if (source.get(fieldName).isContainerNode()) {

                Iterator<String> subNodeFieldNames = source.get(fieldName).fieldNames();

                subNodeFieldNames.forEachRemaining(subFieldName -> {
                    if (source.get(fieldName).get(subFieldName).equals(comparison.get(fieldName).get(subFieldName))) {
                        result.add(" '" + fieldName + "." + subFieldName + "' field is Equal.");
                    } else {
                        result.add(" '" + fieldName + "." + subFieldName + "' field is [ NOT ] Equal.");
                    }
                });

            } else {

                if (source.get(fieldName).equals(comparison.get(fieldName))) {
                    result.add("'" + fieldName + "' field is Equal.");
                } else {
                    result.add("'" + fieldName + "' field is [ NOT ] Equal.");
                }

            }
        });

        return result;
    }

    private JsonNode getNodes(String json) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void printResult(String path, String result) {
        System.out.println(" - For ( " + path + " ) -> " + result);
    }

    private String stringCompare(Map<String, String> response) {

        if (response.get("v1").contains("ERROR") || response.get("v1").contains("ERROR")) {
            return "V1 | V2 Has some Error! [ ERROR ]";
        }

        if (response.get("v1").equals(response.get("v2"))) {
            return "V1 & V2 Are Equal.";
        }
        return "V1 & V2 Are [ NOT ] Equal.";
    }

    private void printResponse(String path, Map<String, String> response) {

        response.forEach((key, value) -> {
            System.out.println(" [ " + key + " ] ( " + path + " ) -> " + value);
        });

    }

    private Map<String, String> sendGetRequest(String request, String v1Url, String v2Url) {

        Map<String, String> response = new HashMap<>();

        String v1Response = sendRequest(v1Url, request);

        String v2Response = sendRequest(v2Url, request);

        response.put("v1", v1Response);

        response.put("v2", v2Response);

        return response;
    }

    private String sendRequest(String url, String path) {

        String requestUrl1 = url + path;

        try {
            return rest.getForObject(requestUrl1, String.class);
        } catch (HttpClientErrorException e) {
            return "ERROR";
        }
    }

    private List<String> filterByGetRequest(List<String> listOfRequests) {

        return listOfRequests
                .stream()
                .filter(item -> item.contains("GET"))
                .map(item -> item.split(" /")[1])
                .collect(Collectors.toList());
    }
}
