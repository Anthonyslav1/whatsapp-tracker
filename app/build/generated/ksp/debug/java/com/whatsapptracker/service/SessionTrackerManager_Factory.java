package com.whatsapptracker.service;

import com.whatsapptracker.data.db.ChatSessionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class SessionTrackerManager_Factory implements Factory<SessionTrackerManager> {
  private final Provider<ChatSessionDao> daoProvider;

  public SessionTrackerManager_Factory(Provider<ChatSessionDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public SessionTrackerManager get() {
    return newInstance(daoProvider.get());
  }

  public static SessionTrackerManager_Factory create(Provider<ChatSessionDao> daoProvider) {
    return new SessionTrackerManager_Factory(daoProvider);
  }

  public static SessionTrackerManager newInstance(ChatSessionDao dao) {
    return new SessionTrackerManager(dao);
  }
}
