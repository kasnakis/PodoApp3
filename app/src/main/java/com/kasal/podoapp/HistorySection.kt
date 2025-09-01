package com.kasal.podoapp.ui

import com.kasal.podoapp.data.PatientHistory

interface HistorySection {
    fun collectInto(aggr: PatientHistoryAggregator)
    fun prefill(history: PatientHistory?)
}
