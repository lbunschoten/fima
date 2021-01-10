package fima.api.transaction

import fima.api.utils.FromProtoConvertable

data class MonthlyTransactionStatistics(
    val month: Int,
    val year: Int,
    val transactions: Int,
    val sum: Float,
    val balance: Float
) {
    companion object: FromProtoConvertable<fima.domain.transaction.MonthlyTransactionStatistics, MonthlyTransactionStatistics> {
        override fun fromProto(proto: fima.domain.transaction.MonthlyTransactionStatistics): MonthlyTransactionStatistics {
            return MonthlyTransactionStatistics(proto.month, proto.year, proto.transaction, proto.sum, proto.balance)
        }
    }

}