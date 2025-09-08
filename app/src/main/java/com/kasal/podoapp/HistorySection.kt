package com.kasal.podoapp.ui

import com.kasal.podoapp.data.PatientHistory

interface HistorySection {
    fun prefill(history: PatientHistory?) {}
    fun collectInto(aggr: PatientHistoryAggregator) {}
}
