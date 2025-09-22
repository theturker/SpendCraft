package com.alperen.spendcraft.feature.transactions;

import com.alperen.spendcraft.core.common.NotificationTester;
import com.alperen.spendcraft.domain.usecase.DeleteCategoryUseCase;
import com.alperen.spendcraft.domain.usecase.DeleteTransactionUseCase;
import com.alperen.spendcraft.domain.usecase.InsertCategoryUseCase;
import com.alperen.spendcraft.domain.usecase.MarkTodayLoggedUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveAccountsUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveCategoriesUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveStreakUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsByAccountUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase;
import com.alperen.spendcraft.domain.usecase.UpdateAccountUseCase;
import com.alperen.spendcraft.domain.usecase.UpsertTransactionUseCase;
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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<ObserveTransactionsUseCase> observeTransactionsProvider;

  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<ObserveAccountsUseCase> observeAccountsProvider;

  private final Provider<ObserveTransactionsByAccountUseCase> observeTransactionsByAccountProvider;

  private final Provider<UpsertTransactionUseCase> upsertProvider;

  private final Provider<DeleteTransactionUseCase> deleteProvider;

  private final Provider<InsertCategoryUseCase> insertCategoryProvider;

  private final Provider<DeleteCategoryUseCase> deleteCategoryProvider;

  private final Provider<UpdateAccountUseCase> updateAccountProvider;

  private final Provider<MarkTodayLoggedUseCase> markTodayLoggedProvider;

  private final Provider<ObserveStreakUseCase> observeStreakProvider;

  private final Provider<NotificationTester> notificationTesterProvider;

  public TransactionsViewModel_Factory(
      Provider<ObserveTransactionsUseCase> observeTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveTransactionsByAccountUseCase> observeTransactionsByAccountProvider,
      Provider<UpsertTransactionUseCase> upsertProvider,
      Provider<DeleteTransactionUseCase> deleteProvider,
      Provider<InsertCategoryUseCase> insertCategoryProvider,
      Provider<DeleteCategoryUseCase> deleteCategoryProvider,
      Provider<UpdateAccountUseCase> updateAccountProvider,
      Provider<MarkTodayLoggedUseCase> markTodayLoggedProvider,
      Provider<ObserveStreakUseCase> observeStreakProvider,
      Provider<NotificationTester> notificationTesterProvider) {
    this.observeTransactionsProvider = observeTransactionsProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.observeAccountsProvider = observeAccountsProvider;
    this.observeTransactionsByAccountProvider = observeTransactionsByAccountProvider;
    this.upsertProvider = upsertProvider;
    this.deleteProvider = deleteProvider;
    this.insertCategoryProvider = insertCategoryProvider;
    this.deleteCategoryProvider = deleteCategoryProvider;
    this.updateAccountProvider = updateAccountProvider;
    this.markTodayLoggedProvider = markTodayLoggedProvider;
    this.observeStreakProvider = observeStreakProvider;
    this.notificationTesterProvider = notificationTesterProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(observeTransactionsProvider.get(), observeCategoriesProvider.get(), observeAccountsProvider.get(), observeTransactionsByAccountProvider.get(), upsertProvider.get(), deleteProvider.get(), insertCategoryProvider.get(), deleteCategoryProvider.get(), updateAccountProvider.get(), markTodayLoggedProvider.get(), observeStreakProvider.get(), notificationTesterProvider.get());
  }

  public static TransactionsViewModel_Factory create(
      Provider<ObserveTransactionsUseCase> observeTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveTransactionsByAccountUseCase> observeTransactionsByAccountProvider,
      Provider<UpsertTransactionUseCase> upsertProvider,
      Provider<DeleteTransactionUseCase> deleteProvider,
      Provider<InsertCategoryUseCase> insertCategoryProvider,
      Provider<DeleteCategoryUseCase> deleteCategoryProvider,
      Provider<UpdateAccountUseCase> updateAccountProvider,
      Provider<MarkTodayLoggedUseCase> markTodayLoggedProvider,
      Provider<ObserveStreakUseCase> observeStreakProvider,
      Provider<NotificationTester> notificationTesterProvider) {
    return new TransactionsViewModel_Factory(observeTransactionsProvider, observeCategoriesProvider, observeAccountsProvider, observeTransactionsByAccountProvider, upsertProvider, deleteProvider, insertCategoryProvider, deleteCategoryProvider, updateAccountProvider, markTodayLoggedProvider, observeStreakProvider, notificationTesterProvider);
  }

  public static TransactionsViewModel newInstance(ObserveTransactionsUseCase observeTransactions,
      ObserveCategoriesUseCase observeCategories, ObserveAccountsUseCase observeAccounts,
      ObserveTransactionsByAccountUseCase observeTransactionsByAccount,
      UpsertTransactionUseCase upsert, DeleteTransactionUseCase delete,
      InsertCategoryUseCase insertCategory, DeleteCategoryUseCase deleteCategory,
      UpdateAccountUseCase updateAccount, MarkTodayLoggedUseCase markTodayLogged,
      ObserveStreakUseCase observeStreak, NotificationTester notificationTester) {
    return new TransactionsViewModel(observeTransactions, observeCategories, observeAccounts, observeTransactionsByAccount, upsert, delete, insertCategory, deleteCategory, updateAccount, markTodayLogged, observeStreak, notificationTester);
  }
}
