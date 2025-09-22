package com.alperen.spendcraft.data.repository;

import com.alperen.spendcraft.data.db.dao.CategoryDao;
import com.alperen.spendcraft.data.db.dao.TxDao;
import com.alperen.spendcraft.domain.repo.TransactionsRepository;
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
public final class RepositoryModule_ProvideTransactionsRepositoryFactory implements Factory<TransactionsRepository> {
  private final Provider<TxDao> txDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public RepositoryModule_ProvideTransactionsRepositoryFactory(Provider<TxDao> txDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.txDaoProvider = txDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public TransactionsRepository get() {
    return provideTransactionsRepository(txDaoProvider.get(), categoryDaoProvider.get());
  }

  public static RepositoryModule_ProvideTransactionsRepositoryFactory create(
      Provider<TxDao> txDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new RepositoryModule_ProvideTransactionsRepositoryFactory(txDaoProvider, categoryDaoProvider);
  }

  public static TransactionsRepository provideTransactionsRepository(TxDao txDao,
      CategoryDao categoryDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideTransactionsRepository(txDao, categoryDao));
  }
}
