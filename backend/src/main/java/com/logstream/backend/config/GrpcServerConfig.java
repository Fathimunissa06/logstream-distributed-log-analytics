package com.logstream.backend.config;

import com.logstream.backend.grpc.LogIngestionServiceImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Value("${grpc.server.port:9090}")
    private int port;

    private final LogIngestionServiceImpl service;

    private Server server;

    public GrpcServerConfig(
            LogIngestionServiceImpl service) {

        this.service = service;
    }

    @PostConstruct
    public void start() throws IOException {

        server =
                ServerBuilder
                        .forPort(port)
                        .addService(service)
                        .build()
                        .start();

        System.out.println(
                "gRPC server started on port "
                        + port
        );
    }

    @PreDestroy
    public void stop() {

        if (server != null) {

            server.shutdown();
        }
    }
}