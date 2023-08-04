package org.example;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v113.network.Network;
import org.openqa.selenium.devtools.v113.network.model.Request;
import org.openqa.selenium.devtools.v113.network.model.Response;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        new WebServer().start();

        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();


        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(),
                entry -> {
                    Request req = entry.getRequest();
                    log(String.format("HTTP > %s %s", req.getMethod(), req.getUrl()));
                });

        devTools.addListener(Network.responseReceived(),
                entry -> {
                    Response resp = entry.getResponse();
                    log(String.format("HTTP < %s %s", resp.getStatus(), resp.getUrl()));
                });

        devTools.addListener(Network.webSocketFrameSent(),
                entry -> {
                    log(String.format("WS > %s", entry.getResponse().getPayloadData()));
                });

        devTools.addListener(Network.webSocketFrameReceived(),
                entry -> {
                    log(String.format("WS < %s", entry.getResponse().getPayloadData()));
                });

        driver.get("http://localhost:8080");
    }

    public static void log(String msg) {
        System.out.println(msg);
    }
}