package com.alperen.spendcraft.data.repository;

import com.alperen.spendcraft.data.db.dao.DailyEntryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class StreakRepositoryImpl_Factory implements Factory<StreakRepositoryImpl> {
  private final Provider<DailyEntryDao> dailyEntryDaoProvider;

  public StreakRepositoryImpl_Factory(Provider<DailyEntryDao> dailyEntryDaoProvider) {
    this.dailyEntryDaoProvider = dailyEntryDaoProvider;
  }

  @Override
  public StreakRepositoryImpl get() {
    return newInstance(dailyEntryDaoProvider.get());
  }

  public static StreakRepositoryImpl_Factory create(Provider<DailyEntryDao> dailyEntryDaoProvider) {
    return new StreakRepositoryImpl_Factory(dailyEntryDaoProvider);
  }

  public static StreakRepositoryImpl newInstance(DailyEntryDao dailyEntryDao) {
    return new StreakRepositoryImpl(dailyEntryDao);
  }
}
