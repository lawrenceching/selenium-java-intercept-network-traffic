package org.example;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WebServer {

    private static final int PORT = 8080;
    private Vertx vertx;

    public void start() {

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        vertx = Vertx.vertx();

        Router router = Router.router(vertx);
        router.get("/*").handler(StaticHandler.create("./static"));
        vertx.createHttpServer()
                .requestHandler(router)
                .webSocketHandler(ws -> {

                    ws.textMessageHandler((text) -> {
                    });
                    ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                        ws.writeTextMessage("Hello world!");
                    }, 1, 1, TimeUnit.SECONDS);

                    ws.closeHandler((v) -> {
                        scheduledFuture.cancel(true);
                    });
                })
                .listen(PORT);

        System.out.printf("HTTP server started on port %d%n", PORT);
    }

    public void stop() {
        vertx.close();
    }

}
