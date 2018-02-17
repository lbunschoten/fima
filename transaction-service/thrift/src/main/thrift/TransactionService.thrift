namespace scala fima.transactionservice.thriftscala

struct Transaction {
    1: i32 id
}

service TransactionService {
    Transaction getTransaction(1: i32 id)

    list<Transaction> getRecentTransactions(1: i32 limit)

    Transaction insertTransaction(1: Transaction transaction)

    void deleteTransaction(1: i32 id)
}