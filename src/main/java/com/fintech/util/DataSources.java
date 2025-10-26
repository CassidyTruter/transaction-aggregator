package com.fintech.util;

import com.fintech.dto.RawTransactionDTO;
import com.fintech.enums.SourceType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataSources {

    private static final String CLIENT_ID = "CLIENT-001";
    private static final String[] ACCOUNTS = {
            "ACC-12345-001",
            "ACC-12345-002",
            "ACC-12345-003",
            "ACC-12345-004"
    };

    public List<RawTransactionDTO> getEftTransactions() {
        List<RawTransactionDTO> transactions = new ArrayList<>();

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.EFT, "Salary Payment - ABC Corp",
                new BigDecimal("45000.00"), LocalDateTime.now().minusDays(5)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.EFT, "Refund - Takealot Purchase",
                new BigDecimal("850.50"), LocalDateTime.now().minusDays(8)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.EFT, "Transfer from John Doe",
                new BigDecimal("2500.00"), LocalDateTime.now().minusDays(10)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.EFT, "Monthly Salary Deposit",
                new BigDecimal("38000.00"), LocalDateTime.now().minusDays(2)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.EFT, "Refund from Woolworths",
                new BigDecimal("125.80"), LocalDateTime.now().minusDays(12)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.EFT, "Payment Received - Freelance Work",
                new BigDecimal("5200.00"), LocalDateTime.now().minusDays(15)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.EFT, "Transfer In - Investment Return",
                new BigDecimal("1500.00"), LocalDateTime.now().minusDays(20)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.EFT, "Bonus Payment",
                new BigDecimal("8500.00"), LocalDateTime.now().minusDays(25)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.EFT, "Refund - Cancelled Flight",
                new BigDecimal("3200.00"), LocalDateTime.now().minusDays(30)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.EFT, "Payment from Client XYZ",
                new BigDecimal("12000.00"), LocalDateTime.now().minusDays(35)
        ));

        return transactions;
    }

    public List<RawTransactionDTO> getBankFees() {
        List<RawTransactionDTO> transactions = new ArrayList<>();

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.BANK_FEE, "Monthly Account Fee",
                new BigDecimal("-59.00"), LocalDateTime.now().minusDays(1)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.BANK_FEE, "ATM Withdrawal Fee - Other Bank",
                new BigDecimal("-8.50"), LocalDateTime.now().minusDays(3)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.BANK_FEE, "Service Fee - Card Replacement",
                new BigDecimal("-120.00"), LocalDateTime.now().minusDays(7)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.BANK_FEE, "ATM Fee",
                new BigDecimal("-6.00"), LocalDateTime.now().minusDays(9)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.BANK_FEE, "Monthly Service Charge",
                new BigDecimal("-75.00"), LocalDateTime.now().minusDays(14)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.BANK_FEE, "Overdraft Fee",
                new BigDecimal("-150.00"), LocalDateTime.now().minusDays(18)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.BANK_FEE, "Cash Deposit Fee",
                new BigDecimal("-25.00"), LocalDateTime.now().minusDays(22)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.BANK_FEE, "ATM Balance Inquiry Fee",
                new BigDecimal("-2.50"), LocalDateTime.now().minusDays(28)
        ));

        return transactions;
    }

    public List<RawTransactionDTO> getDebitOrders() {
        List<RawTransactionDTO> transactions = new ArrayList<>();

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.DEBIT_ORDER, "Netflix Subscription",
                new BigDecimal("-199.00"), LocalDateTime.now().minusDays(1)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.DEBIT_ORDER, "DSTV Premium Package",
                new BigDecimal("-829.00"), LocalDateTime.now().minusDays(2)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.DEBIT_ORDER, "Spotify Premium Subscription",
                new BigDecimal("-59.99"), LocalDateTime.now().minusDays(4)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.DEBIT_ORDER, "Discovery Life Insurance",
                new BigDecimal("-1250.00"), LocalDateTime.now().minusDays(5)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.DEBIT_ORDER, "City of Johannesburg - Municipal Services",
                new BigDecimal("-2350.00"), LocalDateTime.now().minusDays(6)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.DEBIT_ORDER, "Virgin Active Gym Membership",
                new BigDecimal("-699.00"), LocalDateTime.now().minusDays(8)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.DEBIT_ORDER, "Old Mutual Life Insurance",
                new BigDecimal("-980.00"), LocalDateTime.now().minusDays(11)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.DEBIT_ORDER, "Medical Aid - Discovery Health",
                new BigDecimal("-3500.00"), LocalDateTime.now().minusDays(13)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.DEBIT_ORDER, "Vodacom Contract Payment",
                new BigDecimal("-499.00"), LocalDateTime.now().minusDays(16)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.DEBIT_ORDER, "Car Insurance - Outsurance",
                new BigDecimal("-1850.00"), LocalDateTime.now().minusDays(19)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.DEBIT_ORDER, "Amazon Prime Subscription",
                new BigDecimal("-79.00"), LocalDateTime.now().minusDays(23)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.DEBIT_ORDER, "Electricity Prepaid - City Power",
                new BigDecimal("-1200.00"), LocalDateTime.now().minusDays(27)
        ));

        return transactions;
    }

    public List<RawTransactionDTO> getCardTransactions() {
        List<RawTransactionDTO> transactions = new ArrayList<>();

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Woolworths Sandton City",
                new BigDecimal("-1256.80"), LocalDateTime.now().minusDays(1)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.CARD, "Shell Garage - Rivonia",
                new BigDecimal("-850.00"), LocalDateTime.now().minusDays(2)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Uber Trip - Sandton to Rosebank",
                new BigDecimal("-125.50"), LocalDateTime.now().minusDays(3)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.CARD, "Checkers Hyper Fourways",
                new BigDecimal("-2340.50"), LocalDateTime.now().minusDays(4)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.CARD, "Nando's Rosebank",
                new BigDecimal("-385.00"), LocalDateTime.now().minusDays(5)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Engen Garage Morningside",
                new BigDecimal("-920.00"), LocalDateTime.now().minusDays(6)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.CARD, "Pick n Pay Melrose Arch",
                new BigDecimal("-876.30"), LocalDateTime.now().minusDays(7)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Bolt Ride - Home to Office",
                new BigDecimal("-89.00"), LocalDateTime.now().minusDays(8)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.CARD, "Mr Price Clothing Sandton",
                new BigDecimal("-1450.00"), LocalDateTime.now().minusDays(9)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.CARD, "McDonald's Drive Thru",
                new BigDecimal("-142.50"), LocalDateTime.now().minusDays(10)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Spur Steak Ranch Fourways",
                new BigDecimal("-520.00"), LocalDateTime.now().minusDays(11)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[3], SourceType.CARD, "Takealot Online Purchase",
                new BigDecimal("-2850.00"), LocalDateTime.now().minusDays(12)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[1], SourceType.CARD, "Uber Eats Delivery",
                new BigDecimal("-215.00"), LocalDateTime.now().minusDays(13)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[0], SourceType.CARD, "Sasol Garage - N1 North",
                new BigDecimal("-780.00"), LocalDateTime.now().minusDays(14)
        ));

        transactions.add(createTransaction(
                ACCOUNTS[2], SourceType.CARD, "Exclusive Books Sandton",
                new BigDecimal("-450.00"), LocalDateTime.now().minusDays(15)
        ));

        return transactions;
    }

    private RawTransactionDTO createTransaction(String accountNumber, SourceType sourceType,
                                             String description, BigDecimal amount,
                                             LocalDateTime transactionDate) {
        return new RawTransactionDTO(
                UUID.randomUUID().toString(),
                CLIENT_ID,
                accountNumber,
                sourceType,
                description,
                amount,
                transactionDate
        );
    }
}