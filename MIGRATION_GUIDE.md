# Android'den KMP'ye Migration Guide

Bu dokümanda mevcut Android uygulamanızı Kotlin Multiplatform'a nasıl geçireceğinizi adım adım anlatıyoruz.

## 📋 Genel Bakış

### Değişenler
- ✅ Domain layer artık `shared` modülünde
- ✅ Model sınıfları `shared` modülünde
- ✅ Repository interface'leri `shared` modülünde
- ✅ Use case'ler `shared` modülünde
- ✅ Database: Room → SQLDelight
- ✅ DI: Hilt → Koin (opsiyonel, Hilt devam edebilir)

### Değişmeyenler
- ✅ UI katmanı (Jetpack Compose)
- ✅ Navigation yapısı
- ✅ ViewModel'ler (şimdilik)
- ✅ Feature modülleri
- ✅ Firebase entegrasyonu

## 🔄 Adım Adım Migration

### 1. Shared Modülünü App'e Bağlama

**app/build.gradle.kts**:
```kotlin
dependencies {
    // Shared modülü ekle
    implementation(project(":shared"))
    
    // Koin (opsiyonel - Hilt ile birlikte kullanılabilir)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    
    // Mevcut dependencyler...
}
```

### 2. Import Path'leri Güncelleme

**Eski**:
```kotlin
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase
```

**Yeni**:
```kotlin
import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import com.alperen.spendcraft.shared.domain.usecase.ObserveTransactionsUseCase
```

### 3. Application Class'ını Güncelleme

**app/src/main/java/.../SpendCraftApplication.kt**:
```kotlin
class SpendCraftApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Koin'i başlat
        startKoin {
            androidContext(this@SpendCraftApplication)
            modules(getSharedModules())
        }
        
        // Mevcut Firebase vb. init'ler...
    }
}
```

### 4. Repository Implementasyonları

#### Seçenek A: Shared'daki SQLDelight Repository'yi Kullan

```kotlin
// TransactionsViewModel.kt
@HiltViewModel
class TransactionsViewModel @Inject constructor(
    // Artık shared modülden geliyor
    private val observeTransactionsUseCase: ObserveTransactionsUseCase,
    private val upsertTransactionUseCase: UpsertTransactionUseCase
) : ViewModel() {
    
    val transactions: StateFlow<List<Transaction>> = 
        observeTransactionsUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            upsertTransactionUseCase(transaction)
        }
    }
}
```

#### Seçenek B: Mevcut Room Repository'yi Adapter ile Kullan

Geçiş sürecinde hem Room hem de SQLDelight kullanabilirsiniz:

```kotlin
class RoomToSQLDelightAdapter @Inject constructor(
    private val roomRepository: RoomTransactionsRepository,
    private val sqlDelightRepository: TransactionsRepositoryImpl
) : TransactionsRepository {
    
    override fun observeTransactions(): Flow<List<Transaction>> {
        // Şimdilik Room'dan oku
        return roomRepository.observeTransactions()
            .map { it.map { entity -> entity.toDomainModel() } }
    }
    
    override suspend fun upsert(transaction: Transaction) {
        // Her iki veritabanına da yaz (migration için)
        roomRepository.upsert(transaction.toRoomEntity())
        sqlDelightRepository.upsert(transaction)
    }
}
```

### 5. Hilt Modüllerini Güncelleme

**data/repository/di/RepositoryModule.kt**:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SpendCraftDatabase {
        val driver = DatabaseDriverFactory(context).createDriver()
        return SpendCraftDatabase(driver)
    }
    
    @Provides
    @Singleton
    fun provideTransactionsRepository(
        database: SpendCraftDatabase
    ): TransactionsRepository {
        return TransactionsRepositoryImpl(database)
    }
}
```

### 6. Use Case'leri Güncelleme

Use case'ler artık shared modülden geliyor, sadece import path'leri değişiyor:

```kotlin
// Eski
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase

// Yeni
import com.alperen.spendcraft.shared.domain.usecase.ObserveTransactionsUseCase
```

Hilt injection'ı aynı şekilde çalışır:
```kotlin
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    
    @Provides
    fun provideObserveTransactionsUseCase(
        repository: TransactionsRepository
    ): ObserveTransactionsUseCase {
        return ObserveTransactionsUseCase(repository)
    }
}
```

### 7. Feature Modüllerini Güncelleme

Her feature modülü için:

1. **Import path'leri değiştir**
2. **build.gradle.kts'ye shared dependency'si ekle**:
```kotlin
dependencies {
    implementation(project(":shared"))
    // ...
}
```

### 8. Data Migration (Room → SQLDelight)

Kullanıcı verilerini kaybetmemek için:

```kotlin
class DataMigrationHelper @Inject constructor(
    private val roomDb: RoomDatabase,
    private val sqlDelightDb: SpendCraftDatabase
) {
    suspend fun migrateData() {
        // Room'dan tüm verileri oku
        val transactions = roomDb.transactionDao().getAllSync()
        val categories = roomDb.categoryDao().getAllSync()
        val accounts = roomDb.accountDao().getAllSync()
        
        // SQLDelight'a yaz
        sqlDelightDb.transaction {
            transactions.forEach { tx ->
                sqlDelightDb.transactionEntityQueries.insert(
                    amountMinorUnits = tx.amountMinorUnits,
                    timestampUtcMillis = tx.timestampUtcMillis,
                    note = tx.note,
                    categoryId = tx.categoryId,
                    accountId = tx.accountId,
                    type = tx.type.name
                )
            }
            
            // Kategoriler ve hesaplar için de aynısı...
        }
        
        // Migration tamamlandığında flag set et
        preferences.setBoolean("data_migrated", true)
    }
}

// Application.onCreate'de
if (!preferences.getBoolean("data_migrated", false)) {
    lifecycleScope.launch {
        dataMigrationHelper.migrateData()
    }
}
```

## 🧪 Test Stratejisi

### 1. Unit Testler

```kotlin
class ObserveTransactionsUseCaseTest {
    
    @Test
    fun `should emit transactions from repository`() = runTest {
        // Given
        val mockRepo = mock<TransactionsRepository>()
        val testTransactions = listOf(
            Transaction(1, Money(1000), /* ... */)
        )
        whenever(mockRepo.observeTransactions()).thenReturn(flowOf(testTransactions))
        
        val useCase = ObserveTransactionsUseCase(mockRepo)
        
        // When
        val result = useCase().first()
        
        // Then
        assertEquals(testTransactions, result)
    }
}
```

### 2. Integration Testler

```kotlin
@HiltAndroidTest
class TransactionFlowTest {
    
    @get:Rule
    val hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: TransactionsRepository
    
    @Test
    fun `should insert and retrieve transaction`() = runTest {
        // Given
        val transaction = Transaction(
            id = null,
            amount = Money(5000),
            timestampUtcMillis = System.currentTimeMillis(),
            note = "Test",
            categoryId = 1,
            accountId = 1,
            type = TransactionType.EXPENSE
        )
        
        // When
        repository.upsert(transaction)
        
        // Then
        repository.observeTransactions().first().let { transactions ->
            assertTrue(transactions.any { it.note == "Test" })
        }
    }
}
```

## 📊 Migration Checklist

### Hazırlık
- [ ] Shared modülü build oluyor
- [ ] Import path'leri güncellendi
- [ ] Dependency injection ayarlandı

### Core
- [ ] Models import edildi
- [ ] Repository interface'leri import edildi
- [ ] Use case'ler import edildi
- [ ] Database driver ayarlandı

### Features
- [ ] Transactions feature güncellendi
- [ ] Reports feature güncellendi
- [ ] Categories feature güncellendi
- [ ] Accounts feature güncellendi
- [ ] Budget feature güncellendi
- [ ] Settings feature güncellendi

### Data
- [ ] SQLDelight şeması oluşturuldu
- [ ] Repository implementasyonları oluşturuldu
- [ ] Data migration yazıldı
- [ ] Migration test edildi

### Testing
- [ ] Unit testler geçiyor
- [ ] Integration testler geçiyor
- [ ] UI testler geçiyor
- [ ] Manuel test tamamlandı

### Production
- [ ] Beta test başarılı
- [ ] Performance kabul edilebilir
- [ ] Crash rate düşük
- [ ] User feedback pozitif

## 🐛 Bilinen Sorunlar ve Çözümler

### 1. "Cannot find symbol" Hatası

**Sorun**: Shared modüldeki sınıflar bulunamıyor.

**Çözüm**:
```bash
./gradlew :shared:build
./gradlew clean build
```

### 2. SQLDelight Code Generation

**Sorun**: SQLDelight query sınıfları oluşturulmuyor.

**Çözüm**:
```bash
./gradlew :shared:generateSqlDelightInterface
```

### 3. iOS Framework Hatası

**Sorun**: iOS framework build olmuyor.

**Çözüm**:
```bash
./gradlew :shared:syncFramework
cd iosApp
pod install
```

### 4. Koin + Hilt Çakışması

**Sorun**: Her iki DI framework da aynı anda çalışırken sorun çıkıyor.

**Çözüm**: Shared modül için Koin, Android-specific sınıflar için Hilt kullanın:
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    // Hilt injection
    private val androidSpecific: AndroidClass
) : ViewModel() {
    
    // Koin injection
    private val sharedUseCase: SharedUseCase by inject()
}
```

## 🚀 Production'a Geçiş

### Aşamalı Rollout

1. **Alpha (Internal)**: %1 kullanıcı
2. **Beta (Public)**: %10 kullanıcı
3. **Production**: %100 kullanıcı

### Rollback Planı

Migration başarısız olursa:
```kotlin
if (migrationFailed || userReportedIssue) {
    // Eski Room veritabanına geri dön
    sharedPreferences.edit()
        .putBoolean("use_legacy_db", true)
        .apply()
    
    // Uygulamayı yeniden başlat
    ProcessPhoenix.triggerRebirth(context)
}
```

## 📞 Destek

Sorun yaşarsanız:
1. [GitHub Issues](https://github.com/alperenturker/SpendCraft/issues)
2. Clean build deneyin: `./gradlew clean build`
3. Cache temizleyin: `./gradlew cleanBuildCache`

---

**Önemli**: Migration'ı production'a geçirmeden önce mutlaka:
- ✅ Tüm testleri çalıştırın
- ✅ Manuel test yapın
- ✅ Beta test yapın
- ✅ Backup alın

