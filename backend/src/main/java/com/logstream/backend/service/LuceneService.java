package com.logstream.backend.service;

import com.logstream.backend.model.LogRecord;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.MatchAllDocsQuery;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LuceneService {

    private final String indexPath;

    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter indexWriter;

    /*
     * Commit after this many documents.
     */
    private static final int COMMIT_BATCH_SIZE = 10_000;

    /*
     * Thread-safe counter.
     */
    private final AtomicInteger pendingDocuments =
            new AtomicInteger(0);

    /*
     * Used only to prevent multiple threads
     * from committing simultaneously.
     */
    private final Object commitLock =
            new Object();

    public LuceneService(
            @Value("${logstream.index.path}") String indexPath) {

        this.indexPath = indexPath;
    }

    @PostConstruct
    public void initialize() throws IOException {

        Path path = Path.of(indexPath);

        directory = FSDirectory.open(path);

        analyzer = new StandardAnalyzer();

        IndexWriterConfig config =
                new IndexWriterConfig(analyzer);

        config.setOpenMode(
                IndexWriterConfig.OpenMode.CREATE_OR_APPEND
        );

        /*
         * Allow Lucene to buffer more documents in memory
         * before flushing segments.
         */
        config.setRAMBufferSizeMB(256.0);

        indexWriter =
                new IndexWriter(directory, config);

        indexWriter.commit();

        pendingDocuments.set(0);
    }

    /**
     * Index a single log.
     *
     * Used by the existing SendLog API.
     */
    public void indexLog(
            LogRecord logRecord) {

        try {

            Document document =
                    createDocument(logRecord);

            /*
             * IndexWriter is thread-safe.
             * No synchronized keyword is required.
             */
            indexWriter.addDocument(document);

            int pending =
                    pendingDocuments.incrementAndGet();

            if (pending >= COMMIT_BATCH_SIZE) {
                commitIfNeeded();
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to index log",
                    e
            );
        }
    }

    /**
     * High-performance batch indexing.
     *
     * Multiple gRPC worker threads can call this
     * method concurrently.
     */
    public void indexLogs(
            List<LogRecord> logRecords) {

        if (logRecords == null ||
                logRecords.isEmpty()) {

            return;
        }

        try {

            List<Document> documents =
                    new ArrayList<>(logRecords.size());

            for (LogRecord logRecord :
                    logRecords) {

                documents.add(
                        createDocument(logRecord)
                );
            }

            /*
             * IndexWriter supports concurrent writes.
             */
            indexWriter.addDocuments(documents);

            int pending =
                    pendingDocuments.addAndGet(
                            documents.size()
                    );

            if (pending >= COMMIT_BATCH_SIZE) {
                commitIfNeeded();
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to index log batch",
                    e
            );
        }
    }

    /**
     * Commit when enough documents are pending.
     */
    private void commitIfNeeded()
            throws IOException {

        if (pendingDocuments.get()
                < COMMIT_BATCH_SIZE) {

            return;
        }

        synchronized (commitLock) {

            if (pendingDocuments.get()
                    >= COMMIT_BATCH_SIZE) {

                indexWriter.commit();

                pendingDocuments.set(0);
            }
        }
    }

    /**
     * Convert LogRecord into Lucene Document.
     */
    private Document createDocument(
            LogRecord logRecord) {

        Document document =
                new Document();

        document.add(
                new StringField(
                        "timestamp",
                        logRecord.getTimestamp(),
                        Field.Store.YES
                )
        );

        document.add(
                new StringField(
                        "service",
                        logRecord.getService(),
                        Field.Store.YES
                )
        );

        document.add(
                new StringField(
                        "level",
                        logRecord.getLevel(),
                        Field.Store.YES
                )
        );

        document.add(
                new TextField(
                        "message",
                        logRecord.getMessage(),
                        Field.Store.YES
                )
        );

        return document;
    }

    /**
     * Search logs.
     */
    public List<LogRecord> searchLogs(
            String keyword,
            String service,
            String level) {

        List<LogRecord> results =
                new ArrayList<>();

        try {

            /*
             * Make recently indexed documents visible
             * before searching.
             */
            synchronized (commitLock) {

                if (pendingDocuments.get() > 0) {

                    indexWriter.commit();

                    pendingDocuments.set(0);
                }
            }

            if (!DirectoryReader.indexExists(
                    directory)) {

                return results;
            }

            try (DirectoryReader reader =
                         DirectoryReader.open(directory)) {

                IndexSearcher searcher =
                        new IndexSearcher(reader);

                BooleanQuery.Builder builder =
                        new BooleanQuery.Builder();

                boolean hasQuery = false;

                /*
                 * Keyword search.
                 */
                if (keyword != null &&
                        !keyword.trim().isEmpty()) {

                    Query keywordQuery =
                            new org.apache.lucene.queryparser.classic.QueryParser(
                                    "message",
                                    analyzer
                            ).parse(keyword);

                    builder.add(
                            keywordQuery,
                            BooleanClause.Occur.MUST
                    );

                    hasQuery = true;
                }

                /*
                 * Service filter.
                 */
                if (service != null &&
                        !service.trim().isEmpty()) {

                    builder.add(
                            new TermQuery(
                                    new Term(
                                            "service",
                                            service
                                    )
                            ),
                            BooleanClause.Occur.MUST
                    );

                    hasQuery = true;
                }

                /*
                 * Level filter.
                 */
                if (level != null &&
                        !level.trim().isEmpty()) {

                    builder.add(
                            new TermQuery(
                                    new Term(
                                            "level",
                                            level
                                    )
                            ),
                            BooleanClause.Occur.MUST
                    );

                    hasQuery = true;
                }

                Query finalQuery;

                if (hasQuery) {

                    finalQuery =
                            builder.build();

                } else {

                    finalQuery =
                            new MatchAllDocsQuery();
                }

                /*
                 * Return top 100 results.
                 */
                var topDocs =
                        searcher.search(
                                finalQuery,
                                100
                        );

                for (var scoreDoc :
                        topDocs.scoreDocs) {

                    Document document =
                            searcher
                                    .storedFields()
                                    .document(
                                            scoreDoc.doc
                                    );

                    LogRecord log =
                            new LogRecord(
                                    document.get(
                                            "timestamp"
                                    ),
                                    document.get(
                                            "service"
                                    ),
                                    document.get(
                                            "level"
                                    ),
                                    document.get(
                                            "message"
                                    )
                            );

                    results.add(log);
                }
            }

            return results;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to search logs",
                    e
            );
        }
    }

    /**
     * Close Lucene safely.
     */
    @PreDestroy
    public void close() {

        try {

            synchronized (commitLock) {

                if (indexWriter != null) {

                    if (pendingDocuments.get() > 0) {

                        indexWriter.commit();

                        pendingDocuments.set(0);
                    }

                    indexWriter.close();
                }

                if (analyzer != null) {
                    analyzer.close();
                }

                if (directory != null) {
                    directory.close();
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}