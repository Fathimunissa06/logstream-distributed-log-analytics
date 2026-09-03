package com.logstream.backend.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class GrpcPerformanceTest {

    private static final int TOTAL_LOGS = 100_000;

    private static final int BATCH_SIZE = 100;

    private static final int THREADS = 10;

    public static void main(String[] args)
            throws InterruptedException {

        System.out.println("Starting high-throughput test...");
        System.out.println("Total logs : " + TOTAL_LOGS);
        System.out.println("Batch size : " + BATCH_SIZE);
        System.out.println("Threads    : " + THREADS);

        CountDownLatch latch =
                new CountDownLatch(THREADS);

        AtomicInteger successLogs =
                new AtomicInteger(0);

        AtomicInteger failedBatches =
                new AtomicInteger(0);

        long startTime = System.nanoTime();

        int logsPerThread =
                TOTAL_LOGS / THREADS;

        for (int t = 0; t < THREADS; t++) {

            Thread worker = new Thread(() -> {

                ManagedChannel channel =
                        ManagedChannelBuilder
                                .forAddress(
                                        "localhost",
                                        9090
                                )
                                .usePlaintext()
                                .build();

                try {

                    LogIngestionServiceGrpc
                            .LogIngestionServiceBlockingStub stub =
                            LogIngestionServiceGrpc
                                    .newBlockingStub(channel);

                    for (
                            int i = 0;
                            i < logsPerThread;
                            i += BATCH_SIZE
                    ) {

                        int currentBatchSize =
                                Math.min(
                                        BATCH_SIZE,
                                        logsPerThread - i
                                );

                        LogBatch.Builder batch =
                                LogBatch.newBuilder();

                        for (
                                int j = 0;
                                j < currentBatchSize;
                                j++
                        ) {

                            LogMessage log =
                                    LogMessage.newBuilder()
                                            .setTimestamp(
                                                    "2026-09-02T12:00:00Z"
                                            )
                                            .setService(
                                                    "billing-api"
                                            )
                                            .setLevel(
                                                    "INFO"
                                            )
                                            .setMessage(
                                                    "High throughput test log "
                                                    + i
                                                    + "-"
                                                    + j
                                            )
                                            .build();

                            batch.addLogs(log);
                        }

                        LogResponse response =
                                stub.sendLogs(
                                        batch.build()
                                );

                        if (response.getSuccess()) {

                            /*
                             * Add the number of logs
                             * successfully processed in
                             * this batch.
                             */
                            successLogs.addAndGet(
                                    currentBatchSize
                            );

                        } else {

                            failedBatches.incrementAndGet();

                            System.out.println(
                                    "Batch failed: "
                                    + response.getMessage()
                            );
                        }
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Worker failed: "
                            + e.getMessage()
                    );

                    e.printStackTrace();

                } finally {

                    channel.shutdown();

                    latch.countDown();
                }

            });

            worker.start();
        }

        /*
         * Wait for all worker threads.
         */
        latch.await();

        long endTime =
                System.nanoTime();

        double seconds =
                (endTime - startTime)
                        / 1_000_000_000.0;

        double throughput;

        if (seconds > 0) {

            throughput =
                    successLogs.get() / seconds;

        } else {

            throughput = 0;
        }

        System.out.println();
        System.out.println("==============================");
        System.out.println("PERFORMANCE RESULT");
        System.out.println("==============================");

        System.out.println(
                "Total logs      : "
                        + TOTAL_LOGS
        );

        System.out.println(
                "Success logs    : "
                        + successLogs.get()
        );

        System.out.println(
                "Failed batches  : "
                        + failedBatches.get()
        );

        System.out.printf(
                "Time taken      : %.3f sec%n",
                seconds
        );

        System.out.printf(
                "Throughput      : %.2f logs/sec%n",
                throughput
        );

        System.out.println("==============================");
    }
}