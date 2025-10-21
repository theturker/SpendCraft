//
//  KoinHelper.swift
//  SpendCraftiOS
//
//  Koin DI bridge for iOS - Shared KMP module integration
//

import Foundation
import shared

/**
 * Koin Dependency Injection bridge for iOS
 * 
 * Bu class iOS tarafında Koin container'a erişim sağlar.
 * Shared KMP module'deki repository ve ViewModel'lere erişmek için kullanılır.
 * 
 * Usage:
 * ```swift
 * // AppDelegate veya App init'te
 * KoinHelper.shared.initialize()
 * 
 * // ViewModel'de
 * let repository = KoinHelper.shared.getTransactionsRepository()
 * let sharedViewModel = KoinHelper.shared.getSharedTransactionsViewModel()
 * ```
 */
class KoinHelper {
    static let shared = KoinHelper()
    
    private var koinApp: Koin_coreKoinApplication?
    
    private init() {}
    
    /**
     * Koin'i initialize et
     * 
     * SpendCraftiOSApp.swift'in init'inde çağrılmalı:
     * ```swift
     * init() {
     *     KoinHelper.shared.initialize()
     * }
     * ```
     */
    func initialize() {
        if koinApp == nil {
            koinApp = IosModuleKt.initKoinIOS()
            print("✅ Koin initialized for iOS")
        }
    }
    
    /**
     * Koin instance'ını al (generic get için)
     */
    private var koin: Koin_coreKoin {
        guard let app = koinApp else {
            fatalError("❌ Koin not initialized! Call KoinHelper.shared.initialize() first")
        }
        return app.koin
    }
    
    // MARK: - Repositories
    
    /**
     * TransactionsRepository instance'ını al
     * 
     * Returns: SQLDelight-based shared repository
     */
    func getTransactionsRepository() -> TransactionsRepository {
        return koin.get(objCClass: TransactionsRepository.self) as! TransactionsRepository
    }
    
    // MARK: - ViewModels
    
    /**
     * SharedTransactionsViewModel instance'ını al
     * 
     * Returns: Shared business logic içeren ViewModel
     */
    func getSharedTransactionsViewModel() -> SharedTransactionsViewModel {
        return koin.get(objCClass: SharedTransactionsViewModel.self) as! SharedTransactionsViewModel
    }
    
    // MARK: - Use Cases
    
    func getObserveTransactionsUseCase() -> ObserveTransactionsUseCase {
        return koin.get(objCClass: ObserveTransactionsUseCase.self) as! ObserveTransactionsUseCase
    }
    
    func getUpsertTransactionUseCase() -> UpsertTransactionUseCase {
        return koin.get(objCClass: UpsertTransactionUseCase.self) as! UpsertTransactionUseCase
    }
    
    func getDeleteTransactionUseCase() -> DeleteTransactionUseCase {
        return koin.get(objCClass: DeleteTransactionUseCase.self) as! DeleteTransactionUseCase
    }
    
    func getObserveCategoriesUseCase() -> ObserveCategoriesUseCase {
        return koin.get(objCClass: ObserveCategoriesUseCase.self) as! ObserveCategoriesUseCase
    }
    
    func getInsertCategoryUseCase() -> InsertCategoryUseCase {
        return koin.get(objCClass: InsertCategoryUseCase.self) as! InsertCategoryUseCase
    }
}

