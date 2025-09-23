package com.alperen.spendcraft.data.repository;

import com.alperen.spendcraft.data.db.dao.BudgetAlertDao;
import com.alperen.spendcraft.data.db.dao.BudgetDao;
import com.alperen.spendcraft.data.db.dao.CategoryDao;
import com.alperen.spendcraft.data.db.dao.TxDao;
import com.alperen.spendcraft.domain.repo.BudgetRepository;
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
public final class RepositoryModule_ProvideBudgetRepositoryFactory implements Factory<BudgetRepository> {
  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<BudgetAlertDao> budgetAlertDaoProvider;

  private final Provider<TxDao> txDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public RepositoryModule_ProvideBudgetRepositoryFactory(Provider<BudgetDao> budgetDaoProvider,
      Provider<BudgetAlertDao> budgetAlertDaoProvider, Provider<TxDao> txDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
    this.budgetAlertDaoProvider = budgetAlertDaoProvider;
    this.txDaoProvider = txDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public BudgetRepository get() {
    return provideBudgetRepository(budgetDaoProvider.get(), budgetAlertDaoProvider.get(), txDaoProvider.get(), categoryDaoProvider.get());
  }

  public static RepositoryModule_ProvideBudgetRepositoryFactory create(
      Provider<BudgetDao> budgetDaoProvider, Provider<BudgetAlertDao> budgetAlertDaoProvider,
      Provider<TxDao> txDaoProvider, Provider<CategoryDao> categoryDaoProvider) {
    return new RepositoryModule_ProvideBudgetRepositoryFactory(budgetDaoProvider, budgetAlertDaoProvider, txDaoProvider, categoryDaoProvider);
  }

  public static BudgetRepository provideBudgetRepository(BudgetDao budgetDao,
      BudgetAlertDao budgetAlertDao, TxDao txDao, CategoryDao categoryDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideBudgetRepository(budgetDao, budgetAlertDao, txDao, categoryDao));
  }
}
