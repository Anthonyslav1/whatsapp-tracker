package com.whatsapptracker;

import androidx.hilt.work.HiltWorkerFactory;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class WhatsAppTrackerApp_MembersInjector implements MembersInjector<WhatsAppTrackerApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public WhatsAppTrackerApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<WhatsAppTrackerApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new WhatsAppTrackerApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(WhatsAppTrackerApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.whatsapptracker.WhatsAppTrackerApp.workerFactory")
  public static void injectWorkerFactory(WhatsAppTrackerApp instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
