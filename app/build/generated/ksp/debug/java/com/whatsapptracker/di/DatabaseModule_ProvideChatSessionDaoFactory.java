package com.whatsapptracker.di;

import com.whatsapptracker.data.db.AppDatabase;
import com.whatsapptracker.data.db.ChatSessionDao;
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
    "KotlinInternalInJava"
})
public final class DatabaseModule_ProvideChatSessionDaoFactory implements Factory<ChatSessionDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideChatSessionDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ChatSessionDao get() {
    return provideChatSessionDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideChatSessionDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideChatSessionDaoFactory(databaseProvider);
  }

  public static ChatSessionDao provideChatSessionDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideChatSessionDao(database));
  }
}
