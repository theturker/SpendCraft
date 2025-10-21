package com.alperen.spendcraft.shared.util

import kotlinx.coroutines.flow.Flow

/**
 * Flow extension for iOS AsyncSequence conversion
 * 
 * Swift'te Flow collection için kullanılır:
 * ```swift
 * let sequence = try await FlowExtKt.asAsyncSequence(flow)
 * for try await value in sequence {
 *     print(value)
 * }
 * ```
 */
suspend fun <T> Flow<T>.asAsyncSequence(): kotlinx.coroutines.flow.Flow<T> = this

