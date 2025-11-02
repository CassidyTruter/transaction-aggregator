package com.fintech.service;

import com.fintech.dto.RawTransactionDTO;
import com.fintech.entity.Transaction;
import com.fintech.queue.TransactionQueue;
import com.fintech.repository.TransactionRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TransactionConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumerService.class);

    private final TransactionQueue queue;
    private final TransactionCategorizationService categorizationService;
    private final TransactionRepository transactionRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AtomicBoolean running = new AtomicBoolean(true);

    public TransactionConsumerService(
            TransactionQueue queue,
            TransactionCategorizationService categorizationService,
            TransactionRepository transactionRepository) {
        this.queue = queue;
        this.categorizationService = categorizationService;
        this.transactionRepository = transactionRepository;
    }

    @PostConstruct
    public void startConsumers() {
        logger.info("Starting transaction consumers (4 threads)");

        for (int i = 0; i < 4; i++) {
            final int consumerId = i + 1;
            executor.submit(() -> consumeTransactions(consumerId));
        }
    }

    private void consumeTransactions(int consumerId) {
        logger.info("Consumer {} started", consumerId);

        while (running.get()) {
            try {
                RawTransactionDTO rawTransaction = queue.consume();
                logger.info("Consumer {} received transaction: {}",
                        consumerId, rawTransaction.getTransactionId());

                processAndSave(rawTransaction);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Consumer {} interrupted", consumerId);
                break;
            } catch (Exception e) {
                logger.error("Consumer {} failed to process transaction", consumerId, e);
            }
        }

        logger.info("Consumer {} stopped", consumerId);
    }

    private void processAndSave(RawTransactionDTO rawTransaction) {
        try {
            Transaction categorized = categorizationService.categorize(rawTransaction);
            Transaction saved = transactionRepository.save(categorized);

            logger.info("Saved categorized transaction: {} as {}/{}",
                    saved.getTransactionId(),
                    saved.getCategory(),
                    saved.getSubcategory());

        } catch (Exception e) {
            logger.error("Failed to process transaction: {}",
                    rawTransaction.getTransactionId(), e);
            throw new RuntimeException("Transaction processing failed", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        logger.info("Shutting down transaction consumers");
        running.set(false);
        executor.shutdownNow();
    }
}
