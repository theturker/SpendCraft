package com.alperen.spendcraft.data.db;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DbModule_ProvideDbFactory implements Factory<AppDatabase> {
  private final Provider<Context> contextProvider;

  public DbModule_ProvideDbFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppDatabase get() {
    return provideDb(contextProvider.get());
  }

  public static DbModule_ProvideDbFactory create(Provider<Context> contextProvider) {
    return new DbModule_ProvideDbFactory(contextProvider);
  }

  public static AppDatabase provideDb(Context context) {
    return Preconditions.checkNotNullFromProvides(DbModule.INSTANCE.provideDb(context));
  }
}
