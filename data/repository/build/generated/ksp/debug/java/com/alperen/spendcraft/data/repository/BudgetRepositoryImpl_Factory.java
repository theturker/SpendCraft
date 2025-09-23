package com.alperen.spendcraft.data.repository;

import com.alperen.spendcraft.data.db.dao.BudgetAlertDao;
import com.alperen.spendcraft.data.db.dao.BudgetDao;
import com.alperen.spendcraft.data.db.dao.CategoryDao;
import com.alperen.spendcraft.data.db.dao.TxDao;
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
public final class BudgetRepositoryImpl_Factory implements Factory<BudgetRepositoryImpl> {
  private final Provider<BudgetDao> budgetDaoProvider;

  private final Provider<BudgetAlertDao> budgetAlertDaoProvider;

  private final Provider<TxDao> txDaoProvider;

  private final Provider<CategoryDao> categoryDaoProvider;

  public BudgetRepositoryImpl_Factory(Provider<BudgetDao> budgetDaoProvider,
      Provider<BudgetAlertDao> budgetAlertDaoProvider, Provider<TxDao> txDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
    this.budgetAlertDaoProvider = budgetAlertDaoProvider;
    this.txDaoProvider = txDaoProvider;
    this.categoryDaoProvider = categoryDaoProvider;
  }

  @Override
  public BudgetRepositoryImpl get() {
    return newInstance(budgetDaoProvider.get(), budgetAlertDaoProvider.get(), txDaoProvider.get(), categoryDaoProvider.get());
  }

  public static BudgetRepositoryImpl_Factory create(Provider<BudgetDao> budgetDaoProvider,
      Provider<BudgetAlertDao> budgetAlertDaoProvider, Provider<TxDao> txDaoProvider,
      Provider<CategoryDao> categoryDaoProvider) {
    return new BudgetRepositoryImpl_Factory(budgetDaoProvider, budgetAlertDaoProvider, txDaoProvider, categoryDaoProvider);
  }

  public static BudgetRepositoryImpl newInstance(BudgetDao budgetDao, BudgetAlertDao budgetAlertDao,
      TxDao txDao, CategoryDao categoryDao) {
    return new BudgetRepositoryImpl(budgetDao, budgetAlertDao, txDao, categoryDao);
  }
}
