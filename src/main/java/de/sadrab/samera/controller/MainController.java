package de.sadrab.samera.controller;

import de.sadrab.samera.parser.NginxParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

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

    }
}
