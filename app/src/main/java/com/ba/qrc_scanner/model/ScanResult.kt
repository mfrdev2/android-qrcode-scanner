package com.ba.qrc_scanner.model

import org.threeten.bp.LocalDate


data class ScanResult(
    val visitorCode: String,
    val transactionId: String,
    val date: LocalDate
)
