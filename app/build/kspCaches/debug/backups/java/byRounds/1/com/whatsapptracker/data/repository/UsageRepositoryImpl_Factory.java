package com.whatsapptracker.data.repository;

import com.whatsapptracker.data.db.ChatSessionDao;
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
    "KotlinInternalInJava"
})
public final class UsageRepositoryImpl_Factory implements Factory<UsageRepositoryImpl> {
  private final Provider<ChatSessionDao> daoProvider;

  public UsageRepositoryImpl_Factory(Provider<ChatSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public UsageRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static UsageRepositoryImpl_Factory create(Provider<ChatSessionDao> daoProvider) {
    return new UsageRepositoryImpl_Factory(daoProvider);
  }

  public static UsageRepositoryImpl newInstance(ChatSessionDao dao) {
    return new UsageRepositoryImpl(dao);
  }
}
