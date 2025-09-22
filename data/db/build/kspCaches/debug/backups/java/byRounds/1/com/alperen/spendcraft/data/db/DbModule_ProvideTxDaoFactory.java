package com.alperen.spendcraft.data.db;

import com.alperen.spendcraft.data.db.dao.TxDao;
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
public final class DbModule_ProvideTxDaoFactory implements Factory<TxDao> {
  private final Provider<AppDatabase> dbProvider;

  public DbModule_ProvideTxDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public TxDao get() {
    return provideTxDao(dbProvider.get());
  }

  public static DbModule_ProvideTxDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DbModule_ProvideTxDaoFactory(dbProvider);
  }

  public static TxDao provideTxDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DbModule.INSTANCE.provideTxDao(db));
  }
}
