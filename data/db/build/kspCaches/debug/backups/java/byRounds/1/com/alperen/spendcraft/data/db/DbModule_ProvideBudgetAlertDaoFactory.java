package com.alperen.spendcraft.data.db;

import com.alperen.spendcraft.data.db.dao.BudgetAlertDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class DbModule_ProvideBudgetAlertDaoFactory implements Factory<BudgetAlertDao> {
  private final Provider<AppDatabase> dbProvider;

  public DbModule_ProvideBudgetAlertDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BudgetAlertDao get() {
    return provideBudgetAlertDao(dbProvider.get());
  }

  public static DbModule_ProvideBudgetAlertDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DbModule_ProvideBudgetAlertDaoFactory(dbProvider);
  }

  public static BudgetAlertDao provideBudgetAlertDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DbModule.INSTANCE.provideBudgetAlertDao(db));
  }
}
