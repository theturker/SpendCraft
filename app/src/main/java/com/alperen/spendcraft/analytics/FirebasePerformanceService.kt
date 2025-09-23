package com.alperen.spendcraft.analytics

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePerformanceService @Inject constructor() {
    private val performance = FirebasePerformance.getInstance()
    
    fun startTrace(traceName: String): Trace {
        return performance.newTrace(traceName).apply {
            start()
        }
    }
    
    fun stopTrace(trace: Trace) {
        trace.stop()
    }
    
    fun recordScreenLoadTime(screenName: String, loadTimeMs: Long) {
        val trace = startTrace("screen_load_${screenName}")
        trace.putMetric("load_time_ms", loadTimeMs)
        stopTrace(trace)
    }
    
    fun recordDatabaseOperation(operation: String, durationMs: Long) {
        val trace = startTrace("database_${operation}")
        trace.putMetric("duration_ms", durationMs)
        stopTrace(trace)
    }
    
    fun recordApiCall(endpoint: String, responseTimeMs: Long, success: Boolean) {
        val trace = startTrace("api_call_${endpoint}")
        trace.putMetric("response_time_ms", responseTimeMs)
        trace.putMetric("success", if (success) 1 else 0)
        stopTrace(trace)
    }
}
