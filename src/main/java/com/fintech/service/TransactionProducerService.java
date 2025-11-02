package com.fintech.service;

import com.fintech.dto.RawTransactionDTO;
import com.fintech.enums.SourceType;
import com.fintech.queue.TransactionQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class TransactionProducerService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProducerService.class);

    private final TransactionQueue queue;
    private final Random random = new Random();

    public TransactionProducerService(TransactionQueue queue) {
        this.queue = queue;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 5000) // Every 10 seconds
    public void publishSampleTransactions() {
        logger.info("Publishing sample transactions to queue");

        // Publish 2 EFT transactions
        for (int i = 0; i < 2; i++) {
            RawTransactionDTO eft = createEftTransaction();
            queue.publish(eft);
            logger.debug("Published EFT transaction: {}", eft.getTransactionId());
        }

        // Publish 1 bank fee
        RawTransactionDTO fee = createBankFee();
        queue.publish(fee);
        logger.debug("Published bank fee: {}", fee.getTransactionId());

        // Publish 2 debit orders
        for (int i = 0; i < 2; i++) {
            RawTransactionDTO debitOrder = createDebitOrder();
            queue.publish(debitOrder);
            logger.debug("Published debit order: {}", debitOrder.getTransactionId());
        }

        // Publish 3 card transactions
        for (int i = 0; i < 3; i++) {
            RawTransactionDTO card = createCardTransaction();
            queue.publish(card);
            logger.debug("Published card transaction: {}", card.getTransactionId());
        }

        logger.info("Published batch of 8 transactions. Queue size: {}", queue.size());
    }

    private RawTransactionDTO createEftTransaction() {
        return new RawTransactionDTO(
                "eft-" + UUID.randomUUID().toString().substring(0, 8),
                "CLIENT-" + String.format("%03d", random.nextInt(1000)),
                "ACC-" + String.format("%010d", random.nextLong(1000000000)),
                SourceType.EFT,
                "Payment from Acme Corp - Salary",
                BigDecimal.valueOf(45000 + random.nextInt(20000)),
                LocalDateTime.now()
        );
    }

    private RawTransactionDTO createBankFee() {
        return new RawTransactionDTO(
                "fee-" + UUID.randomUUID().toString().substring(0, 8),
                "CLIENT-" + String.format("%03d", random.nextInt(1000)),
                "ACC-" + String.format("%010d", random.nextLong(1000000000)),
                SourceType.BANK_FEE,
                "Monthly service fee",
                BigDecimal.valueOf(-59.00),
                LocalDateTime.now()
        );
    }

    private RawTransactionDTO createDebitOrder() {
        String[] providers = {"Discovery Health", "Old Mutual Life", "DSTV Premium", "City of Cape Town"};
        return new RawTransactionDTO(
                "do-" + UUID.randomUUID().toString().substring(0, 8),
                "CLIENT-" + String.format("%03d", random.nextInt(1000)),
                "ACC-" + String.format("%010d", random.nextLong(1000000000)),
                SourceType.DEBIT_ORDER,
                providers[random.nextInt(providers.length)],
                BigDecimal.valueOf(-(200 + random.nextInt(1500))),
                LocalDateTime.now()
        );
    }

    private RawTransactionDTO createCardTransaction() {
        String[] merchants = {"Woolworths Sandton", "Checkers Rosebank", "Shell V-Power", "Uber Trip #123"};
        return new RawTransactionDTO(
                "card-" + UUID.randomUUID().toString().substring(0, 8),
                "CLIENT-" + String.format("%03d", random.nextInt(1000)),
                "ACC-" + String.format("%010d", random.nextLong(1000000000)),
                SourceType.CARD,
                merchants[random.nextInt(merchants.length)],
                BigDecimal.valueOf(-(50 + random.nextInt(500))),
                LocalDateTime.now()
        );
    }
}
