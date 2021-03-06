package fima.services.transaction.write.aggregate

interface BankAccount : Aggregate {
    val accountNumber: String
    val balanceInCents: Long

    fun withVersion(version: Int): BankAccount
    fun withBalance(balanceInCents: Long): BankAccount
}

object UniniatilizedAccount : BankAccount {
    override val version: Int = 0
    override val balanceInCents: Long = 0
    override val accountNumber: String
        get() = throw IllegalStateException("The account has not been assigned an account number yet")

    override fun withVersion(version: Int): BankAccount = this
    override fun withBalance(balanceInCents: Long) = throw IllegalStateException("Cannot change balance on an account that is not open")

    override fun validate(): Set<String> = emptySet()
}

data class OnlineBankAccount(override val version: Int, override val accountNumber: String, override val balanceInCents: Long) : BankAccount {

    override fun withVersion(version: Int): BankAccount = this.copy(version = version)
    override fun withBalance(balanceInCents: Long): BankAccount {
        return this.copy(version = version, balanceInCents = balanceInCents)
    }

    override fun validate(): Set<String> {
        return mapOf<() -> Boolean, String>(
            ::hasNegativeBalance to "Account has a negative balance"
        ).filterKeys { f -> f() }.values.toSet()
    }

    private fun hasNegativeBalance(): Boolean {
        return this.balanceInCents < 0
    }
}


data class ClosedBankAccount(override val version: Int, override val accountNumber: String) : BankAccount {
    override val balanceInCents: Long = 0

    override fun withVersion(version: Int): BankAccount = this.copy(version = version)
    override fun withBalance(balanceInCents: Long) = throw IllegalStateException("Cannot change balance on an account that is closed")

    override fun validate(): Set<String> = emptySet()
}
