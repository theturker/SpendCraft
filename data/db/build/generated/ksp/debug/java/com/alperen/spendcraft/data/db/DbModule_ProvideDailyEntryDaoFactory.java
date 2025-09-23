package com.alperen.spendcraft.data.db;

import com.alperen.spendcraft.data.db.dao.DailyEntryDao;
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
public final class DbModule_ProvideDailyEntryDaoFactory implements Factory<DailyEntryDao> {
  private final Provider<AppDatabase> dbProvider;

  public DbModule_ProvideDailyEntryDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public DailyEntryDao get() {
    return provideDailyEntryDao(dbProvider.get());
  }

  public static DbModule_ProvideDailyEntryDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DbModule_ProvideDailyEntryDaoFactory(dbProvider);
  }

  public static DailyEntryDao provideDailyEntryDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DbModule.INSTANCE.provideDailyEntryDao(db));
  }
}
