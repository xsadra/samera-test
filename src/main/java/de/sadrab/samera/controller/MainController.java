package de.sadrab.samera.controller;

import de.sadrab.samera.parser.NginxParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        /// Task 1
        nginxParser.getListOfRequests().forEach(System.out::println);

        /// Task 2
        List<String> listOfRequests = nginxParser.getListOfRequests();

        List<String> requests = filterByGetRequest(listOfRequests);

        requests.forEach(request -> {

            Map<String, String> response = sendGetRequest(request, v1Url, v2Url);

            /// Result for Task 2
//            printResponse(request, response);

            String stringCompareResult = stringCompare(response);

            /// Result for Task 3
            printResult(request, stringCompareResult);

        });

    }

    private void printResult(String path, String result) {
        System.out.println(" - For ( " + path + " ) -> " + result);
    }

    private String stringCompare(Map<String,String> response) {

        if (response.get("v1").contains("ERROR") || response.get("v1").contains("ERROR")) {
            return "V1 | V2 Has some Error! [ ERROR ]";
        }

        if (response.get("v1").equals(response.get("v2"))) {
            return "V1 & V2 Are Equal.";
        }
        return "V1 & V2 Are [ NOT ] Equal.";
    }

    private void printResponse(String path, Map<String,String> response) {
        response.forEach((key, value) -> {
            System.out.println(" [ " + key + " ] ( " + path + " ) -> " + value);
        });
    }

    private Map<String,String> sendGetRequest(String request, String v1Url, String v2Url) {
        Map<String,String> response = new HashMap<>();

        String v1Response = sendRequest(v1Url, request);
        String v2Response = sendRequest(v2Url, request);

        response.put("v1",v1Response);
        response.put("v2",v2Response);

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
