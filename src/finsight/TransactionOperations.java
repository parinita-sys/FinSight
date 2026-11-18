package finsight;

public interface TransactionOperations {

    boolean addTransaction(
            int userId,
            double amount,
            String type,
            String category,
            String description
    );

}