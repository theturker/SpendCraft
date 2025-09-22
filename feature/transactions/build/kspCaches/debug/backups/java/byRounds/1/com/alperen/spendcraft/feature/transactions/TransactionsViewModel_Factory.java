package com.alperen.spendcraft.feature.transactions;

import com.alperen.spendcraft.domain.usecase.DeleteCategoryUseCase;
import com.alperen.spendcraft.domain.usecase.DeleteTransactionUseCase;
import com.alperen.spendcraft.domain.usecase.InsertCategoryUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveCategoriesUseCase;
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase;
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

  private final Provider<UpsertTransactionUseCase> upsertProvider;

  private final Provider<DeleteTransactionUseCase> deleteProvider;

  private final Provider<InsertCategoryUseCase> insertCategoryProvider;

  private final Provider<DeleteCategoryUseCase> deleteCategoryProvider;

  public TransactionsViewModel_Factory(
      Provider<ObserveTransactionsUseCase> observeTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<UpsertTransactionUseCase> upsertProvider,
      Provider<DeleteTransactionUseCase> deleteProvider,
      Provider<InsertCategoryUseCase> insertCategoryProvider,
      Provider<DeleteCategoryUseCase> deleteCategoryProvider) {
    this.observeTransactionsProvider = observeTransactionsProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.upsertProvider = upsertProvider;
    this.deleteProvider = deleteProvider;
    this.insertCategoryProvider = insertCategoryProvider;
    this.deleteCategoryProvider = deleteCategoryProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(observeTransactionsProvider.get(), observeCategoriesProvider.get(), upsertProvider.get(), deleteProvider.get(), insertCategoryProvider.get(), deleteCategoryProvider.get());
  }

  public static TransactionsViewModel_Factory create(
      Provider<ObserveTransactionsUseCase> observeTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<UpsertTransactionUseCase> upsertProvider,
      Provider<DeleteTransactionUseCase> deleteProvider,
      Provider<InsertCategoryUseCase> insertCategoryProvider,
      Provider<DeleteCategoryUseCase> deleteCategoryProvider) {
    return new TransactionsViewModel_Factory(observeTransactionsProvider, observeCategoriesProvider, upsertProvider, deleteProvider, insertCategoryProvider, deleteCategoryProvider);
  }

  public static TransactionsViewModel newInstance(ObserveTransactionsUseCase observeTransactions,
      ObserveCategoriesUseCase observeCategories, UpsertTransactionUseCase upsert,
      DeleteTransactionUseCase delete, InsertCategoryUseCase insertCategory,
      DeleteCategoryUseCase deleteCategory) {
    return new TransactionsViewModel(observeTransactions, observeCategories, upsert, delete, insertCategory, deleteCategory);
  }
}
