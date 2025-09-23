package com.alperen.spendcraft.data.repository;

import com.alperen.spendcraft.data.db.dao.DailyEntryDao;
import com.alperen.spendcraft.domain.repo.StreakRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RepositoryModule_ProvideStreakRepositoryFactory implements Factory<StreakRepository> {
  private final Provider<DailyEntryDao> dailyEntryDaoProvider;

  public RepositoryModule_ProvideStreakRepositoryFactory(
      Provider<DailyEntryDao> dailyEntryDaoProvider) {
    this.dailyEntryDaoProvider = dailyEntryDaoProvider;
  }

  @Override
  public StreakRepository get() {
    return provideStreakRepository(dailyEntryDaoProvider.get());
  }

  public static RepositoryModule_ProvideStreakRepositoryFactory create(
      Provider<DailyEntryDao> dailyEntryDaoProvider) {
    return new RepositoryModule_ProvideStreakRepositoryFactory(dailyEntryDaoProvider);
  }

  public static StreakRepository provideStreakRepository(DailyEntryDao dailyEntryDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideStreakRepository(dailyEntryDao));
  }
}
