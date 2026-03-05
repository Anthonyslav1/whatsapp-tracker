package com.whatsapptracker.di;

import com.whatsapptracker.data.db.ChatSessionDao;
import com.whatsapptracker.data.repository.UsageRepository;
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
    "KotlinInternalInJava"
})
public final class RepositoryModule_ProvideUsageRepositoryFactory implements Factory<UsageRepository> {
  private final Provider<ChatSessionDao> daoProvider;

  public RepositoryModule_ProvideUsageRepositoryFactory(Provider<ChatSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UsageRepository get() {
    return provideUsageRepository(daoProvider.get());
  }

  public static RepositoryModule_ProvideUsageRepositoryFactory create(
      Provider<ChatSessionDao> daoProvider) {
    return new RepositoryModule_ProvideUsageRepositoryFactory(daoProvider);
  }

  public static UsageRepository provideUsageRepository(ChatSessionDao dao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideUsageRepository(dao));
  }
}
