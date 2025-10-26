package com.fintech.service;

import com.fintech.dto.RawTransactionDTO;
import com.fintech.entity.Transaction;
import com.fintech.enums.Category;
import com.fintech.enums.SourceType;
import com.fintech.enums.Subcategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class TransactionCategorizationService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionCategorizationService.class);

    public Transaction categorize(RawTransactionDTO raw) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(raw.getTransactionId());
        transaction.setClientId(raw.getClientId());
        transaction.setAccountNumber(raw.getAccountNumber());
        transaction.setSourceType(raw.getSourceType());
        transaction.setDescription(raw.getDescription());
        transaction.setAmount(raw.getAmount());
        transaction.setTransactionDate(raw.getTransactionDate());

        String description = raw.getDescription().toLowerCase();

        // Priority 1: High priority exact matches for income
        if (matchesPattern(description, "salary|wage|income|payment.*received")) {
            transaction.setCategory(Category.INCOME);
            transaction.setSubcategory(Subcategory.SALARY);
            transaction.setMerchantName(extractMerchantName(raw.getDescription()));
            logger.debug("Categorized as INCOME/SALARY: {}", raw.getDescription());
            return transaction;
        }

        if (matchesPattern(description, "refund")) {
            transaction.setCategory(Category.INCOME);
            transaction.setSubcategory(Subcategory.REFUND);
            transaction.setMerchantName(extractMerchantName(raw.getDescription()));
            logger.debug("Categorized as INCOME/REFUND: {}", raw.getDescription());
            return transaction;
        }

        // Priority 2: Source type rules
        if (raw.getSourceType() == SourceType.BANK_FEE) {
            return categorizeBankFee(transaction, description);
        }

        // Priority 3: Debit order categorization
        if (raw.getSourceType() == SourceType.DEBIT_ORDER) {
            return categorizeDebitOrder(transaction, description);
        }

        // Priority 4: Card transaction categorization
        if (raw.getSourceType() == SourceType.CARD) {
            return categorizeCardTransaction(transaction, description);
        }

        // Priority 5: Fallback rules based on amount
        if (raw.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            transaction.setCategory(Category.INCOME);
            transaction.setSubcategory(Subcategory.OTHER);
        } else {
            transaction.setCategory(Category.EXPENSES);
            transaction.setSubcategory(Subcategory.OTHER);
        }

        transaction.setMerchantName(extractMerchantName(raw.getDescription()));
        logger.debug("Categorized as fallback: {}", raw.getDescription());
        return transaction;
    }

    private Transaction categorizeBankFee(Transaction transaction, String description) {
        transaction.setCategory(Category.BANK_FEES);

        if (matchesPattern(description, "atm")) {
            transaction.setSubcategory(Subcategory.ATM_FEE);
        } else {
            transaction.setSubcategory(Subcategory.SERVICE_FEE);
        }

        transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
        logger.debug("Categorized as BANK_FEES: {}", transaction.getDescription());
        return transaction;
    }

    private Transaction categorizeDebitOrder(Transaction transaction, String description) {
        // Insurance
        if (matchesPattern(description, "insurance|discovery|old mutual|outsurance|medical aid")) {
            transaction.setCategory(Category.INSURANCE);

            if (matchesPattern(description, "medical|health")) {
                transaction.setSubcategory(Subcategory.MEDICAL);
            } else if (matchesPattern(description, "car|vehicle|auto")) {
                transaction.setSubcategory(Subcategory.VEHICLE);
            } else if (matchesPattern(description, "life")) {
                transaction.setSubcategory(Subcategory.LIFE);
            } else {
                transaction.setSubcategory(Subcategory.OTHER);
            }

            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as INSURANCE: {}", transaction.getDescription());
            return transaction;
        }

        // Utilities
        if (matchesPattern(description, "municipal|electricity|water|city of|city power|eskom|vodacom|mtn|telkom")) {
            transaction.setCategory(Category.UTILITIES);

            if (matchesPattern(description, "municipal|city of")) {
                transaction.setSubcategory(Subcategory.MUNICIPAL);
            } else if (matchesPattern(description, "electricity|eskom|city power")) {
                transaction.setSubcategory(Subcategory.ELECTRICITY);
            } else if (matchesPattern(description, "water")) {
                transaction.setSubcategory(Subcategory.WATER);
            } else {
                transaction.setSubcategory(Subcategory.OTHER);
            }

            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as UTILITIES: {}", transaction.getDescription());
            return transaction;
        }

        // Entertainment subscriptions
        if (matchesPattern(description, "netflix|dstv|showmax|spotify|amazon prime|apple music|youtube|subscription|gym|virgin active")) {
            transaction.setCategory(Category.ENTERTAINMENT);
            transaction.setSubcategory(Subcategory.SUBSCRIPTION);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as ENTERTAINMENT/SUBSCRIPTION: {}", transaction.getDescription());
            return transaction;
        }

        // Default for debit orders
        transaction.setCategory(Category.EXPENSES);
        transaction.setSubcategory(Subcategory.OTHER);
        transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
        logger.debug("Categorized as EXPENSES/OTHER: {}", transaction.getDescription());
        return transaction;
    }

    private Transaction categorizeCardTransaction(Transaction transaction, String description) {
        // Groceries
        if (matchesPattern(description, "woolworths|checkers|pick n pay|spar|shoprite|makro|game")) {
            transaction.setCategory(Category.SHOPPING);
            transaction.setSubcategory(Subcategory.GROCERIES);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as SHOPPING/GROCERIES: {}", transaction.getDescription());
            return transaction;
        }

        // Fuel
        if (matchesPattern(description, "shell|engen|bp|sasol|caltex|total|garage|petrol|fuel")) {
            transaction.setCategory(Category.TRANSPORT);
            transaction.setSubcategory(Subcategory.FUEL);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as TRANSPORT/FUEL: {}", transaction.getDescription());
            return transaction;
        }

        // Rideshare
        if (matchesPattern(description, "uber|bolt|taxify")) {
            transaction.setCategory(Category.TRANSPORT);
            transaction.setSubcategory(Subcategory.RIDESHARE);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as TRANSPORT/RIDESHARE: {}", transaction.getDescription());
            return transaction;
        }

        // Restaurants
        if (matchesPattern(description, "nando's|spur|steers|mcdonald|kfc|burger king|wimpy|pizza|restaurant|cafe|eats")) {
            transaction.setCategory(Category.FOOD_DRINK);
            transaction.setSubcategory(Subcategory.DINING);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as FOOD_DRINK/DINING: {}", transaction.getDescription());
            return transaction;
        }

        // Fast food
        if (matchesPattern(description, "mcdonald|kfc|burger|chicken|wings")) {
            transaction.setCategory(Category.FOOD_DRINK);
            transaction.setSubcategory(Subcategory.FAST_FOOD);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as FOOD_DRINK/FAST_FOOD: {}", transaction.getDescription());
            return transaction;
        }

        // Clothing and retail
        if (matchesPattern(description, "mr price|edgars|truworths|woolworths.*fashion|h&m|zara|cotton on|clothing|fashion")) {
            transaction.setCategory(Category.SHOPPING);
            transaction.setSubcategory(Subcategory.CLOTHING);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as SHOPPING/CLOTHING: {}", transaction.getDescription());
            return transaction;
        }

        // Online shopping
        if (matchesPattern(description, "takealot|amazon|ebay|online|exclusive books")) {
            transaction.setCategory(Category.SHOPPING);
            transaction.setSubcategory(Subcategory.OTHER);
            transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
            logger.debug("Categorized as SHOPPING/OTHER: {}", transaction.getDescription());
            return transaction;
        }

        // Default for card transactions
        transaction.setCategory(Category.UNCATEGORIZED);
        transaction.setSubcategory(Subcategory.OTHER);
        transaction.setMerchantName(extractMerchantName(transaction.getDescription()));
        logger.debug("Categorized as UNCATEGORIZED: {}", transaction.getDescription());
        return transaction;
    }

    private boolean matchesPattern(String text, String pattern) {
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).find();
    }

    private String extractMerchantName(String description) {
        // Extract merchant name from description
        // Remove common keywords and get the main merchant name
        String merchant = description.replaceAll("(?i)(payment|debit order|subscription|from|to|transfer|-)", "").trim();

        // Limit length
        if (merchant.length() > 50) {
            merchant = merchant.substring(0, 50);
        }

        return merchant.isEmpty() ? null : merchant;
    }
}