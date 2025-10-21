# 🗺️ SQLDelight Migration Roadmap

**Tarih**: 19 Ekim 2025  
**Süre Tahmini**: 2-3 hafta  
**Öncelik**: P0 (Kritik - Kod tekrarını %70 azaltacak)

---

## 📊 Mevcut Durum Analizi

### ✅ Hazır Olan Alt Yapı

1. **SQLDelight Yapılandırması** ✅
   - `shared/build.gradle.kts` → SQLDelight plugin aktif
   - Database adı: `SpendCraftDatabase`
   - Package: `com.alperen.spendcraft.shared.database`
   - Version: 2.0.2

2. **SQL Schema Dosyaları** ✅
   ```
   shared/src/commonMain/sqldelight/.../database/
   ├── Transaction.sq     (11 queries)
   ├── Category.sq        (6 queries)
   ├── Account.sq         (6 queries)
   ├── Budget.sq          (6 queries)
   └── Streak.sq          (6 queries)
   ```

3. **Platform-Specific Drivers** ✅
   - `DatabaseDriverFactory.kt` (common - expect class)
   - `DatabaseDriverFactory.android.kt` (AndroidSqliteDriver)
   - `DatabaseDriverFactory.ios.kt` (NativeSqliteDriver)

4. **Repository Interfaces** ✅
   ```kotlin
   shared/domain/repository/
   ├── TransactionsRepository.kt   ✅ Interface tanımlı
   ├── CategoryRepository.kt       ✅ Interface tanımlı
   ├── BudgetRepository.kt         ✅ Interface tanımlı
   ├── StreakRepository.kt         ✅ Interface tanımlı
   └── AnalyticsRepository.kt      ✅ Interface tanımlı
   ```

### ❌ Eksik Olan / Sorunlar

1. **Repository Implementations BOŞŞŞ** ❌
   - `AndroidTransactionsRepository.kt`: Sadece placeholder (75 satır boş implementasyon)
   - `IosTransactionsRepository.kt`: YOK!
   - **Hiçbiri SQLDelight kullanmıyor!**

2. **iOS Hala Core Data Kullanıyor** ❌
   ```swift
   // iosApp/SpendCraftiOS/CoreDataStack.swift
   lazy var container: NSPersistentContainer = {
       let container = NSPersistentContainer(name: "SpendCraft")
       // 7 entity: Transaction, Category, Account, Budget, etc.
   }()
   ```

3. **Android Hala Room Kullanıyor** ❌
   ```kotlin
   // data/db/AppDatabase.kt
   @Database(entities = [
       TransactionEntity::class,
       CategoryEntity::class,
       AccountEntity::class,
       BudgetEntity::class,
       // ...
   ])
   abstract class AppDatabase : RoomDatabase()
   ```

4. **%70 Kod Tekrarı** ❌
   - iOS ViewModels: 7 adet (her biri Core Data ile konuşuyor)
   - Android ViewModels: 10+ adet (her biri Room ile konuşuyor)
   - Business logic tamamen tekrarlanmış

---

## 🎯 Migration Hedefleri

### Kısa Vadeli (1 hafta)
1. ✅ Shared repository implementasyonları yaz (SQLDelight-based)
2. ✅ Transaction, Category, Account için tam implementasyon
3. ✅ Android'de Room ile paralel çalıştır (test için)
4. ✅ Unit testler ekle

### Orta Vadeli (2 hafta)
5. ✅ iOS'ta Core Data ile paralel çalıştır
6. ✅ Android ve iOS'ta migration testleri
7. ✅ Room ve Core Data'yı devre dışı bırak
8. ✅ Tam geçiş tamamla

### Uzun Vadeli (3 hafta)
9. ✅ ViewModels'i shared'a taşı
10. ✅ Business logic paylaşımı %80'e çıkar
11. ✅ Production deployment

---

## 📋 Detaylı İmplementasyon Planı

### Phase 1: Shared Repository Implementation (3-4 gün)

#### Step 1.1: SQL Schema İyileştirmeleri
**Mevcut Eksikler**:
- ❌ `type` field Transaction.sq'da TEXT (String) - olmalı ENUM
- ❌ `isIncome` field yok - iOS'ta var
- ❌ Recurring transaction fields eksik
- ❌ Achievement entities eksik

**Yapılacaklar**:
```sql
-- Transaction.sq güncelle
CREATE TABLE TransactionEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amountMinorUnits INTEGER NOT NULL,
    timestampUtcMillis INTEGER NOT NULL,
    note TEXT,
    categoryId INTEGER,
    accountId INTEGER,
    type TEXT NOT NULL CHECK(type IN ('INCOME', 'EXPENSE')),  -- ENUM gibi
    isRecurring INTEGER DEFAULT 0,                             -- Boolean (0/1)
    recurringFrequency TEXT,                                   -- 'DAILY', 'WEEKLY', 'MONTHLY'
    FOREIGN KEY (categoryId) REFERENCES CategoryEntity(id) ON DELETE SET NULL,
    FOREIGN KEY (accountId) REFERENCES AccountEntity(id) ON DELETE SET NULL
);

-- Category.sq güncelle
CREATE TABLE CategoryEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    color TEXT NOT NULL,
    icon TEXT,
    type TEXT NOT NULL CHECK(type IN ('INCOME', 'EXPENSE')),  -- iOS: isIncome field
    isDefault INTEGER DEFAULT 0
);

-- Yeni: RecurringTransaction.sq
CREATE TABLE RecurringTransactionEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amountMinorUnits INTEGER NOT NULL,
    note TEXT,
    categoryId INTEGER,
    accountId INTEGER,
    type TEXT NOT NULL,
    frequency TEXT NOT NULL,  -- 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'
    lastExecutionDate INTEGER,
    nextExecutionDate INTEGER,
    isEnabled INTEGER DEFAULT 1,
    FOREIGN KEY (categoryId) REFERENCES CategoryEntity(id) ON DELETE CASCADE,
    FOREIGN KEY (accountId) REFERENCES AccountEntity(id) ON DELETE CASCADE
);

-- Yeni: Achievement.sq
CREATE TABLE AchievementEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    icon TEXT NOT NULL,
    category TEXT NOT NULL,
    maxProgress INTEGER NOT NULL,
    progress INTEGER DEFAULT 0,
    points INTEGER NOT NULL,
    isUnlocked INTEGER DEFAULT 0,
    unlockedAt INTEGER
);

-- Yeni: DailyEntry.sq (Streak tracking)
CREATE TABLE DailyEntryEntity (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date INTEGER NOT NULL UNIQUE,  -- Unix timestamp (day precision)
    hasTransaction INTEGER NOT NULL DEFAULT 0
);
```

#### Step 1.2: Repository Implementation
**Dosya**: `shared/src/commonMain/kotlin/.../data/repository/SharedTransactionsRepository.kt`

```kotlin
package com.alperen.spendcraft.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.alperen.spendcraft.shared.database.SpendCraftDatabase
import com.alperen.spendcraft.shared.domain.model.*
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SharedTransactionsRepository(
    private val database: SpendCraftDatabase
) : TransactionsRepository {
    
    private val queries = database.transactionEntityQueries
    private val categoryQueries = database.categoryEntityQueries
    private val accountQueries = database.accountEntityQueries
    
    // Transactions
    override fun observeTransactions(): Flow<List<Transaction>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override fun observeTransactionsByAccount(accountId: Long): Flow<List<Transaction>> {
        return queries.selectByAccount(accountId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun upsert(transaction: Transaction) = withContext(Dispatchers.Default) {
        if (transaction.id == null) {
            queries.insert(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name
            )
        } else {
            queries.update(
                amountMinorUnits = transaction.amount.minorUnits,
                timestampUtcMillis = transaction.timestampUtcMillis,
                note = transaction.note,
                categoryId = transaction.categoryId,
                accountId = transaction.accountId,
                type = transaction.type.name,
                id = transaction.id
            )
        }
    }
    
    override suspend fun delete(transactionId: Long) = withContext(Dispatchers.Default) {
        queries.delete(transactionId)
    }
    
    // Categories
    override fun observeCategories(): Flow<List<Category>> {
        return categoryQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun insertCategory(category: Category): Long = withContext(Dispatchers.Default) {
        categoryQueries.insert(
            name = category.name,
            color = category.color,
            icon = category.icon
        )
        categoryQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun deleteCategory(categoryId: Long) = withContext(Dispatchers.Default) {
        categoryQueries.delete(categoryId)
    }
    
    // Accounts
    override fun observeAccounts(): Flow<List<Account>> {
        return accountQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    override suspend fun insertAccount(account: Account): Long = withContext(Dispatchers.Default) {
        accountQueries.insert(
            name = account.name,
            type = account.type,
            currency = account.currency,
            isDefault = if (account.isDefault) 1 else 0,
            archived = if (account.archived) 1 else 0
        )
        accountQueries.getLastInsertRowId().executeAsOne()
    }
    
    override suspend fun updateAccount(account: Account) = withContext(Dispatchers.Default) {
        accountQueries.update(
            name = account.name,
            type = account.type,
            currency = account.currency,
            isDefault = if (account.isDefault) 1 else 0,
            archived = if (account.archived) 1 else 0,
            id = account.id!!
        )
    }
    
    override suspend fun deleteAccount(accountId: Long) = withContext(Dispatchers.Default) {
        accountQueries.delete(accountId)
    }
    
    override suspend fun getDefaultAccount(): Account? = withContext(Dispatchers.Default) {
        accountQueries.selectDefault().executeAsOneOrNull()?.toDomainModel()
    }
    
    // CSV Export
    override suspend fun getAllAscending(): List<Transaction> = withContext(Dispatchers.Default) {
        queries.selectAllAscending().executeAsList().map { it.toDomainModel() }
    }
    
    // Budget calculations
    override suspend fun getSpentAmountsByCategory(): Map<String, Long> = withContext(Dispatchers.Default) {
        queries.getSpentByCategory()
            .executeAsList()
            .associate { it.categoryId.toString() to it.totalAmount }
    }
}

// Extension functions for mapping
private fun TransactionEntity.toDomainModel() = Transaction(
    id = id,
    amount = Money(minorUnits = amountMinorUnits, currencyCode = "TRY"), // TODO: Currency from account
    timestampUtcMillis = timestampUtcMillis,
    note = note,
    categoryId = categoryId,
    accountId = accountId,
    type = TransactionType.valueOf(type)
)

private fun CategoryEntity.toDomainModel() = Category(
    id = id,
    name = name,
    color = color,
    icon = icon,
    isIncome = type == "INCOME"  // TODO: type field eklenmeli
)

private fun AccountEntity.toDomainModel() = Account(
    id = id,
    name = name,
    type = type,
    currency = currency,
    isDefault = isDefault == 1L,
    archived = archived == 1L
)
```

#### Step 1.3: DI Setup
**Dosya**: `shared/src/commonMain/kotlin/.../di/SharedModule.kt`

```kotlin
package com.alperen.spendcraft.shared.di

import com.alperen.spendcraft.shared.data.DatabaseDriverFactory
import com.alperen.spendcraft.shared.data.repository.SharedTransactionsRepository
import com.alperen.spendcraft.shared.database.SpendCraftDatabase
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import org.koin.dsl.module

val sharedModule = module {
    // Database
    single { 
        val driverFactory: DatabaseDriverFactory = get()
        SpendCraftDatabase(driver = driverFactory.createDriver())
    }
    
    // Repositories
    single<TransactionsRepository> { 
        SharedTransactionsRepository(database = get()) 
    }
}
```

---

### Phase 2: Android Integration (2-3 gün)

#### Step 2.1: Koin Setup (Android)
**Dosya**: `shared/src/androidMain/kotlin/.../di/AndroidModule.kt`

```kotlin
package com.alperen.spendcraft.shared.di

import android.content.Context
import com.alperen.spendcraft.shared.data.DatabaseDriverFactory
import org.koin.dsl.module

val androidModule = module {
    single { DatabaseDriverFactory(context = get<Context>()) }
}
```

#### Step 2.2: App Module'de Koin Initialize
**Dosya**: `app/src/main/java/.../SpendCraftApplication.kt`

```kotlin
import com.alperen.spendcraft.shared.di.sharedModule
import com.alperen.spendcraft.shared.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SpendCraftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Start Koin
        startKoin {
            androidContext(this@SpendCraftApplication)
            modules(sharedModule, androidModule)
        }
    }
}
```

#### Step 2.3: ViewModel'leri Shared Repository ile Güncelle
**Örnek**: `DashboardViewModel.kt`

```kotlin
@HiltViewModel
class DashboardViewModel @Inject constructor(
    // OLD: private val roomRepository: TransactionsRepositoryImpl
    // NEW: Koin'den inject et
) : ViewModel() {
    
    private val sharedRepository: TransactionsRepository by inject()  // Koin
    
    val transactions = sharedRepository.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}
```

---

### Phase 3: iOS Integration (3-4 gün)

#### Step 3.1: CocoaPods Shared Framework
**Dosya**: `iosApp/Podfile`

```ruby
target 'SpendCraftiOS' do
  use_frameworks!
  
  pod 'shared', :path => '../shared'
  
  # Firebase
  pod 'Firebase/Analytics'
  pod 'Firebase/Crashlytics'
  pod 'Firebase/Auth'
  pod 'Firebase/Firestore'
  
  # Google Ads
  pod 'Google-Mobile-Ads-SDK'
end
```

#### Step 3.2: Swift Bridge
**Dosya**: `iosApp/SpendCraftiOS/Shared/KoinHelper.swift`

```swift
import shared

class KoinHelper {
    static let shared = KoinHelper()
    
    private var koinApp: Koin_coreKoinApplication?
    
    func initialize() {
        koinApp = KoinKt.doInitKoin()
    }
    
    func getTransactionsRepository() -> TransactionsRepository {
        return koinApp!.koin.get(objCClass: TransactionsRepository.self) as! TransactionsRepository
    }
}
```

#### Step 3.3: ViewModel'leri Shared Repository ile Güncelle
**Örnek**: `TransactionsViewModel.swift`

```swift
class TransactionsViewModel: ObservableObject {
    @Published var transactions: [Transaction] = []
    
    // OLD: private let context = CoreDataStack.shared.context
    // NEW: Shared repository
    private let repository: TransactionsRepository
    
    init() {
        self.repository = KoinHelper.shared.getTransactionsRepository()
        loadTransactions()
    }
    
    func loadTransactions() {
        // Collect Flow as async sequence
        Task {
            for await txs in repository.observeTransactions() {
                await MainActor.run {
                    self.transactions = txs
                }
            }
        }
    }
}
```

---

### Phase 4: Migration & Cleanup (2-3 gün)

#### Step 4.1: Data Migration
**iOS**: Core Data → SQLDelight
```swift
// Dosya: iosApp/SpendCraftiOS/Migration/CoreDataToSQLDelightMigrator.swift

class CoreDataToSQLDelightMigrator {
    static func migrate() {
        let context = CoreDataStack.shared.context
        let repository = KoinHelper.shared.getTransactionsRepository()
        
        // Migrate Transactions
        let transactionRequest: NSFetchRequest<TransactionEntity> = TransactionEntity.fetchRequest()
        let coreDataTransactions = try! context.fetch(transactionRequest)
        
        for cdTx in coreDataTransactions {
            let sharedTx = Transaction(
                id: cdTx.id,
                amount: Money(minorUnits: cdTx.amountMinor, currencyCode: "TRY"),
                timestampUtcMillis: cdTx.timestampUtcMillis,
                note: cdTx.note,
                categoryId: cdTx.categoryId,
                accountId: cdTx.accountId,
                type: cdTx.isIncome ? .INCOME : .EXPENSE
            )
            repository.upsert(transaction: sharedTx)
        }
        
        // Mark migration as complete
        UserDefaults.standard.set(true, forKey: "coreDataMigrationCompleted")
    }
}
```

**Android**: Room → SQLDelight
```kotlin
// Dosya: app/src/main/java/.../migration/RoomToSQLDelightMigrator.kt

class RoomToSQLDelightMigrator(
    private val roomDatabase: AppDatabase,
    private val sharedRepository: TransactionsRepository
) {
    suspend fun migrate() {
        // Migrate Transactions
        val roomTransactions = roomDatabase.transactionDao().getAllForMigration()
        roomTransactions.forEach { roomTx ->
            val sharedTx = Transaction(
                id = roomTx.id,
                amount = Money(minorUnits = roomTx.amountMinor, currencyCode = "TRY"),
                timestampUtcMillis = roomTx.date.time,
                note = roomTx.note,
                categoryId = roomTx.categoryId,
                accountId = roomTx.accountId,
                type = if (roomTx.isIncome) TransactionType.INCOME else TransactionType.EXPENSE
            )
            sharedRepository.upsert(sharedTx)
        }
        
        // Mark migration as complete
        val prefs = context.getSharedPreferences("migration", MODE_PRIVATE)
        prefs.edit().putBoolean("roomMigrationCompleted", true).apply()
    }
}
```

#### Step 4.2: Cleanup
**iOS**:
- ❌ Delete `CoreDataStack.swift`
- ❌ Delete `SpendCraft.xcdatamodeld`
- ❌ Delete all CoreData entity files
- ❌ Update all ViewModels to use Shared repository

**Android**:
- ❌ Delete `AppDatabase.kt`
- ❌ Delete all Room DAO files
- ❌ Delete all Room entity files
- ❌ Remove Room dependencies from `build.gradle.kts`
- ❌ Update all ViewModels to use Shared repository

---

## 📊 Kazanç Tahminleri

### Kod Azaltma
| Metrik | Önce | Sonra | Kazanç |
|--------|------|-------|--------|
| **iOS Database Code** | ~1,200 satır (Core Data) | ~100 satır (SQLDelight bridge) | %92 ↓ |
| **Android Database Code** | ~1,500 satır (Room) | ~100 satır (SQLDelight bridge) | %93 ↓ |
| **Shared Code** | ~500 satır (interfaces) | ~2,000 satır (full impl) | 4x ↑ |
| **Toplam Satır** | 13,500 satır | 9,500 satır | %30 ↓ |
| **Business Logic Tekrarı** | %70 | %10 | %86 ↓ |

### Sürdürülebilirlik
- ✅ **Bug Fix Süresi**: %80 azalma (bir kere fix, iki platformda çalışır)
- ✅ **Feature Development**: %50 hızlanma (business logic shared)
- ✅ **Test Coverage**: %100 artış (shared tests)
- ✅ **Code Review**: %40 hızlanma (tek kod tabanı)

---

## ⚠️ Riskler ve Azaltma Stratejileri

### Risk 1: Data Loss During Migration
**Azaltma**:
- Migrasyon öncesi backup al
- Paralel çalıştırma (Room/Core Data + SQLDelight)
- Rollback mekanizması

### Risk 2: Performance Degradation
**Azaltma**:
- Benchmark testleri yap
- Pagination ekle
- Index optimizasyonları

### Risk 3: iOS Flow Collection
**Azaltma**:
- KMP-NativeCoroutines library kullan
- Async/await bridge pattern

---

## 📅 Timeline

### Week 1: Foundation
- **Day 1-2**: SQL Schema iyileştirmeleri
- **Day 3-4**: SharedTransactionsRepository implementation
- **Day 5**: Unit tests + DI setup

### Week 2: Platform Integration
- **Day 6-7**: Android integration (Koin + ViewModels)
- **Day 8-9**: iOS integration (CocoaPods + ViewModels)
- **Day 10**: Integration tests

### Week 3: Migration & Production
- **Day 11-12**: Data migration (iOS + Android)
- **Day 13**: Cleanup (delete Room/Core Data)
- **Day 14**: Testing + Bug fixes
- **Day 15**: Production deployment

---

## 🎯 Başarı Kriterleri

### Teknik
- ✅ %0 data loss
- ✅ Performance = veya > mevcut sistem
- ✅ Unit test coverage > %80
- ✅ Integration test coverage > %60

### Business
- ✅ 0 kritik bug
- ✅ User experience degradation yok
- ✅ App store ratings korunmuş

---

## 📚 Referanslar

### Documentation
- [SQLDelight Docs](https://cashapp.github.io/sqldelight/)
- [KMP Best Practices](https://kotlinlang.org/docs/multiplatform.html)
- [Koin DI](https://insert-koin.io/docs/reference/koin-mp/kmp/)

### Similar Migrations
- [Uber KMP Migration](https://eng.uber.com/kmp/)
- [Netflix KMP](https://netflixtechblog.com/kotlin-multiplatform-mobile/)

---

**Rapor Sonu**

_Bu roadmap, SQLDelight migration'ının detaylı planını içermektedir. Implementation 2-3 hafta sürecektir._

