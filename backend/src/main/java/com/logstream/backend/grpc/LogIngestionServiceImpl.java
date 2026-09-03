package com.logstream.backend.grpc;

import com.logstream.backend.model.LogRecord;
import com.logstream.backend.service.LogIngestionService;

import io.grpc.stub.StreamObserver;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LogIngestionServiceImpl
        extends LogIngestionServiceGrpc.LogIngestionServiceImplBase {

    private final LogIngestionService ingestionService;

    public LogIngestionServiceImpl(
            LogIngestionService ingestionService) {

        this.ingestionService = ingestionService;
    }

    /**
     * Existing single-log API.
     */
    @Override
    public void sendLog(
            LogMessage request,
            StreamObserver<LogResponse> responseObserver) {

        try {

            LogRecord logRecord =
                    new LogRecord(
                            request.getTimestamp(),
                            request.getService(),
                            request.getLevel(),
                            request.getMessage()
                    );

            ingestionService.ingest(logRecord);

            LogResponse response =
                    LogResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage(
                                    "Log indexed successfully"
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            LogResponse response =
                    LogResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(
                                    "Failed to index log: "
                                            + e.getMessage()
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * High-throughput batch API.
     */
    @Override
    public void sendLogs(
            LogBatch request,
            StreamObserver<LogResponse> responseObserver) {

        try {

            int logCount =
                    request.getLogsCount();

            if (logCount == 0) {

                LogResponse response =
                        LogResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage(
                                        "Log batch is empty"
                                )
                                .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();

                return;
            }

            /*
             * Convert protobuf logs into LogRecord objects.
             */
            List<LogRecord> logRecords =
                    new ArrayList<>(logCount);

            for (LogMessage logMessage :
                    request.getLogsList()) {

                logRecords.add(
                        new LogRecord(
                                logMessage.getTimestamp(),
                                logMessage.getService(),
                                logMessage.getLevel(),
                                logMessage.getMessage()
                        )
                );
            }

            /*
             * Send the complete batch to the service.
             */
            ingestionService.ingestBatch(
                    logRecords
            );

            /*
             * One response for the entire batch.
             */
            LogResponse response =
                    LogResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage(
                                    "Successfully indexed "
                                            + logCount
                                            + " logs"
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {

            LogResponse response =
                    LogResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(
                                    "Failed to index log batch: "
                                            + e.getMessage()
                            )
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}

