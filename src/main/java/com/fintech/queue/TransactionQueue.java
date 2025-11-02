package com.fintech.queue;

import com.fintech.dto.RawTransactionDTO;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TransactionQueue {

    private final BlockingQueue<RawTransactionDTO> queue = new LinkedBlockingQueue<>(1000);

    public void publish(RawTransactionDTO transaction) {
        try {
            queue.put(transaction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to publish transaction", e);
        }
    }

    public RawTransactionDTO consume() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
