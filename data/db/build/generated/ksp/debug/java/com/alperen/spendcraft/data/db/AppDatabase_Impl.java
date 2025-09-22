package com.alperen.spendcraft.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.alperen.spendcraft.data.db.dao.AccountDao;
import com.alperen.spendcraft.data.db.dao.AccountDao_Impl;
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao;
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao_Impl;
import com.alperen.spendcraft.data.db.dao.BudgetDao;
import com.alperen.spendcraft.data.db.dao.BudgetDao_Impl;
import com.alperen.spendcraft.data.db.dao.CategoryDao;
import com.alperen.spendcraft.data.db.dao.CategoryDao_Impl;
import com.alperen.spendcraft.data.db.dao.DailyEntryDao;
import com.alperen.spendcraft.data.db.dao.DailyEntryDao_Impl;
import com.alperen.spendcraft.data.db.dao.TxDao;
import com.alperen.spendcraft.data.db.dao.TxDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TxDao _txDao;

  private volatile CategoryDao _categoryDao;

  private volatile AccountDao _accountDao;

  private volatile DailyEntryDao _dailyEntryDao;

  private volatile BudgetDao _budgetDao;

  private volatile BudgetAlertDao _budgetAlertDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amountMinor` INTEGER NOT NULL, `timestampUtcMillis` INTEGER NOT NULL, `note` TEXT, `categoryId` INTEGER, `accountId` INTEGER, `isIncome` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_timestampUtcMillis` ON `transactions` (`timestampUtcMillis`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_categoryId` ON `transactions` (`categoryId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_accountId` ON `transactions` (`accountId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `accounts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `isDefault` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `daily_entry` (`dateEpochDay` INTEGER NOT NULL, PRIMARY KEY(`dateEpochDay`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budget` (`categoryId` TEXT NOT NULL, `monthlyLimitMinor` INTEGER NOT NULL, PRIMARY KEY(`categoryId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `budget_alert` (`categoryId` TEXT NOT NULL, `level` INTEGER NOT NULL, `monthKey` TEXT NOT NULL, PRIMARY KEY(`categoryId`, `level`, `monthKey`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '78518630703bd7be82193d56c07b3d77')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `transactions`");
        db.execSQL("DROP TABLE IF EXISTS `categories`");
        db.execSQL("DROP TABLE IF EXISTS `accounts`");
        db.execSQL("DROP TABLE IF EXISTS `daily_entry`");
        db.execSQL("DROP TABLE IF EXISTS `budget`");
        db.execSQL("DROP TABLE IF EXISTS `budget_alert`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTransactions = new HashMap<String, TableInfo.Column>(7);
        _columnsTransactions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("amountMinor", new TableInfo.Column("amountMinor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("timestampUtcMillis", new TableInfo.Column("timestampUtcMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("categoryId", new TableInfo.Column("categoryId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("accountId", new TableInfo.Column("accountId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTransactions.put("isIncome", new TableInfo.Column("isIncome", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTransactions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTransactions = new HashSet<TableInfo.Index>(3);
        _indicesTransactions.add(new TableInfo.Index("index_transactions_timestampUtcMillis", false, Arrays.asList("timestampUtcMillis"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_categoryId", false, Arrays.asList("categoryId"), Arrays.asList("ASC")));
        _indicesTransactions.add(new TableInfo.Index("index_transactions_accountId", false, Arrays.asList("accountId"), Arrays.asList("ASC")));
        final TableInfo _infoTransactions = new TableInfo("transactions", _columnsTransactions, _foreignKeysTransactions, _indicesTransactions);
        final TableInfo _existingTransactions = TableInfo.read(db, "transactions");
        if (!_infoTransactions.equals(_existingTransactions)) {
          return new RoomOpenHelper.ValidationResult(false, "transactions(com.alperen.spendcraft.data.db.entities.TransactionEntity).\n"
                  + " Expected:\n" + _infoTransactions + "\n"
                  + " Found:\n" + _existingTransactions);
        }
        final HashMap<String, TableInfo.Column> _columnsCategories = new HashMap<String, TableInfo.Column>(3);
        _columnsCategories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCategories.put("icon", new TableInfo.Column("icon", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCategories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCategories = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCategories = new TableInfo("categories", _columnsCategories, _foreignKeysCategories, _indicesCategories);
        final TableInfo _existingCategories = TableInfo.read(db, "categories");
        if (!_infoCategories.equals(_existingCategories)) {
          return new RoomOpenHelper.ValidationResult(false, "categories(com.alperen.spendcraft.data.db.entities.CategoryEntity).\n"
                  + " Expected:\n" + _infoCategories + "\n"
                  + " Found:\n" + _existingCategories);
        }
        final HashMap<String, TableInfo.Column> _columnsAccounts = new HashMap<String, TableInfo.Column>(3);
        _columnsAccounts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAccounts.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAccounts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAccounts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAccounts = new TableInfo("accounts", _columnsAccounts, _foreignKeysAccounts, _indicesAccounts);
        final TableInfo _existingAccounts = TableInfo.read(db, "accounts");
        if (!_infoAccounts.equals(_existingAccounts)) {
          return new RoomOpenHelper.ValidationResult(false, "accounts(com.alperen.spendcraft.data.db.entities.AccountEntity).\n"
                  + " Expected:\n" + _infoAccounts + "\n"
                  + " Found:\n" + _existingAccounts);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyEntry = new HashMap<String, TableInfo.Column>(1);
        _columnsDailyEntry.put("dateEpochDay", new TableInfo.Column("dateEpochDay", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyEntry = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyEntry = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyEntry = new TableInfo("daily_entry", _columnsDailyEntry, _foreignKeysDailyEntry, _indicesDailyEntry);
        final TableInfo _existingDailyEntry = TableInfo.read(db, "daily_entry");
        if (!_infoDailyEntry.equals(_existingDailyEntry)) {
          return new RoomOpenHelper.ValidationResult(false, "daily_entry(com.alperen.spendcraft.data.db.entities.DailyEntryEntity).\n"
                  + " Expected:\n" + _infoDailyEntry + "\n"
                  + " Found:\n" + _existingDailyEntry);
        }
        final HashMap<String, TableInfo.Column> _columnsBudget = new HashMap<String, TableInfo.Column>(2);
        _columnsBudget.put("categoryId", new TableInfo.Column("categoryId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudget.put("monthlyLimitMinor", new TableInfo.Column("monthlyLimitMinor", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudget = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudget = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudget = new TableInfo("budget", _columnsBudget, _foreignKeysBudget, _indicesBudget);
        final TableInfo _existingBudget = TableInfo.read(db, "budget");
        if (!_infoBudget.equals(_existingBudget)) {
          return new RoomOpenHelper.ValidationResult(false, "budget(com.alperen.spendcraft.data.db.entities.BudgetEntity).\n"
                  + " Expected:\n" + _infoBudget + "\n"
                  + " Found:\n" + _existingBudget);
        }
        final HashMap<String, TableInfo.Column> _columnsBudgetAlert = new HashMap<String, TableInfo.Column>(3);
        _columnsBudgetAlert.put("categoryId", new TableInfo.Column("categoryId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetAlert.put("level", new TableInfo.Column("level", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBudgetAlert.put("monthKey", new TableInfo.Column("monthKey", "TEXT", true, 3, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBudgetAlert = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBudgetAlert = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBudgetAlert = new TableInfo("budget_alert", _columnsBudgetAlert, _foreignKeysBudgetAlert, _indicesBudgetAlert);
        final TableInfo _existingBudgetAlert = TableInfo.read(db, "budget_alert");
        if (!_infoBudgetAlert.equals(_existingBudgetAlert)) {
          return new RoomOpenHelper.ValidationResult(false, "budget_alert(com.alperen.spendcraft.data.db.entities.BudgetAlertEntity).\n"
                  + " Expected:\n" + _infoBudgetAlert + "\n"
                  + " Found:\n" + _existingBudgetAlert);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "78518630703bd7be82193d56c07b3d77", "95f7a1055a65753d05bae644ad88be01");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "transactions","categories","accounts","daily_entry","budget","budget_alert");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `transactions`");
      _db.execSQL("DELETE FROM `categories`");
      _db.execSQL("DELETE FROM `accounts`");
      _db.execSQL("DELETE FROM `daily_entry`");
      _db.execSQL("DELETE FROM `budget`");
      _db.execSQL("DELETE FROM `budget_alert`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TxDao.class, TxDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CategoryDao.class, CategoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AccountDao.class, AccountDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyEntryDao.class, DailyEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetDao.class, BudgetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BudgetAlertDao.class, BudgetAlertDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TxDao txDao() {
    if (_txDao != null) {
      return _txDao;
    } else {
      synchronized(this) {
        if(_txDao == null) {
          _txDao = new TxDao_Impl(this);
        }
        return _txDao;
      }
    }
  }

  @Override
  public CategoryDao categoryDao() {
    if (_categoryDao != null) {
      return _categoryDao;
    } else {
      synchronized(this) {
        if(_categoryDao == null) {
          _categoryDao = new CategoryDao_Impl(this);
        }
        return _categoryDao;
      }
    }
  }

  @Override
  public AccountDao accountDao() {
    if (_accountDao != null) {
      return _accountDao;
    } else {
      synchronized(this) {
        if(_accountDao == null) {
          _accountDao = new AccountDao_Impl(this);
        }
        return _accountDao;
      }
    }
  }

  @Override
  public DailyEntryDao dailyEntryDao() {
    if (_dailyEntryDao != null) {
      return _dailyEntryDao;
    } else {
      synchronized(this) {
        if(_dailyEntryDao == null) {
          _dailyEntryDao = new DailyEntryDao_Impl(this);
        }
        return _dailyEntryDao;
      }
    }
  }

  @Override
  public BudgetDao budgetDao() {
    if (_budgetDao != null) {
      return _budgetDao;
    } else {
      synchronized(this) {
        if(_budgetDao == null) {
          _budgetDao = new BudgetDao_Impl(this);
        }
        return _budgetDao;
      }
    }
  }

  @Override
  public BudgetAlertDao budgetAlertDao() {
    if (_budgetAlertDao != null) {
      return _budgetAlertDao;
    } else {
      synchronized(this) {
        if(_budgetAlertDao == null) {
          _budgetAlertDao = new BudgetAlertDao_Impl(this);
        }
        return _budgetAlertDao;
      }
    }
  }
}
