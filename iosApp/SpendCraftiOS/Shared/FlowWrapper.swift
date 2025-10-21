//
//  FlowWrapper.swift
//  SpendCraftiOS
//
//  Kotlin Flow → Swift Combine bridge
//

import Foundation
import Combine
import shared

/**
 * Kotlin Flow'u Swift Combine Publisher'a çevirir
 * 
 * Usage:
 * ```swift
 * class TransactionsViewModel: ObservableObject {
 *     @Published var transactions: [Transaction] = []
 *     
 *     private let flowWrapper: FlowWrapper<[Transaction]>
 *     private var cancellables = Set<AnyCancellable>()
 *     
 *     init() {
 *         let repository = KoinHelper.shared.getTransactionsRepository()
 *         flowWrapper = FlowWrapper(flow: repository.observeTransactions())
 *         
 *         flowWrapper.publisher
 *             .sink { [weak self] txs in
 *                 self?.transactions = txs
 *             }
 *             .store(in: &cancellables)
 *     }
 * }
 * ```
 */
class FlowWrapper<T> {
    private let flow: Kotlinx_coroutines_coreFlow
    private var task: Task<Void, Never>?
    
    var publisher: AnyPublisher<T, Never> {
        let subject = PassthroughSubject<T, Never>()
        
        task = Task {
            do {
                let sequence = try await FlowExtKt.asAsyncSequence(flow)
                for try await value in sequence {
                    if let typedValue = value as? T {
                        subject.send(typedValue)
                    }
                }
            } catch {
                print("❌ Flow collection error: \(error)")
            }
        }
        
        return subject.eraseToAnyPublisher()
    }
    
    init(flow: Kotlinx_coroutines_coreFlow) {
        self.flow = flow
    }
    
    deinit {
        task?.cancel()
    }
}

/**
 * StateFlow wrapper - @Published property binding için
 */
class StateFlowWrapper<T>: ObservableObject {
    @Published var value: T
    
    private var cancellable: AnyCancellable?
    
    init(stateFlow: Kotlinx_coroutines_coreStateFlow) {
        // Initial value
        self.value = stateFlow.value as! T
        
        // Observe changes
        let flowWrapper = FlowWrapper<T>(flow: stateFlow)
        cancellable = flowWrapper.publisher
            .sink { [weak self] newValue in
                self?.value = newValue
            }
    }
    
    deinit {
        cancellable?.cancel()
    }
}

