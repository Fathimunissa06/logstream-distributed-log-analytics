
package com.logstream.backend.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcTestClient {

    public static void main(String[] args) {

        ManagedChannel channel =
                ManagedChannelBuilder
                        .forAddress("localhost", 9090)
                        .usePlaintext()
                        .build();

        LogIngestionServiceGrpc.LogIngestionServiceBlockingStub stub =
                LogIngestionServiceGrpc.newBlockingStub(channel);

        LogMessage log1 =
                LogMessage.newBuilder()
                        .setTimestamp("2026-09-01T23:40:00Z")
                        .setService("billing-api")
                        .setLevel("ERROR")
                        .setMessage("Database connection failed through gRPC")
                        .build();

        LogMessage log2 =
                LogMessage.newBuilder()
                        .setTimestamp("2026-09-01T23:41:00Z")
                        .setService("payment-api")
                        .setLevel("INFO")
                        .setMessage("Payment processed successfully")
                        .build();

        LogMessage log3 =
                LogMessage.newBuilder()
                        .setTimestamp("2026-09-01T23:42:00Z")
                        .setService("auth-service")
                        .setLevel("WARN")
                        .setMessage("Multiple failed login attempts detected")
                        .build();

        LogMessage log4 =
                LogMessage.newBuilder()
                        .setTimestamp("2026-09-01T23:43:00Z")
                        .setService("order-service")
                        .setLevel("ERROR")
                        .setMessage("Order processing failed")
                        .build();

        LogMessage log5 =
                LogMessage.newBuilder()
                        .setTimestamp("2026-09-01T23:44:00Z")
                        .setService("billing-api")
                        .setLevel("INFO")
                        .setMessage("Invoice generated successfully")
                        .build();

        LogMessage[] logs = {log1, log2, log3, log4, log5};

        for (int i = 0; i < logs.length; i++) {

            LogResponse response = stub.sendLog(logs[i]);

            System.out.println(
                    "Log " + (i + 1) +
                    " -> Success: " + response.getSuccess() +
                    " | Message: " + response.getMessage()
            );
        }

        channel.shutdown();
    }
}

