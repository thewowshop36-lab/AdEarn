package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    AD_REWARD,
    VIP_UPGRADE,
    DEPOSIT,
    WITHDRAWAL
}

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // AD_REWARD, VIP_UPGRADE, DEPOSIT, WITHDRAWAL
    val amount: Double,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Completed", // Completed, Pending Review, Processing, Approved, Rejected
    val txHash: String? = null,
    val recipientAddress: String? = null
)
