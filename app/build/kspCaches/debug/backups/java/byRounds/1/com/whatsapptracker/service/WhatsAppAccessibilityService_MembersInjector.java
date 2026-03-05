package com.whatsapptracker.service;

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
public final class WhatsAppAccessibilityService_MembersInjector implements MembersInjector<WhatsAppAccessibilityService> {
  private final Provider<WhatsAppAccessibilityParser> parserProvider;

  private final Provider<SessionTrackerManager> sessionTrackerProvider;

  public WhatsAppAccessibilityService_MembersInjector(
      Provider<WhatsAppAccessibilityParser> parserProvider,
      Provider<SessionTrackerManager> sessionTrackerProvider) {
    this.parserProvider = parserProvider;
    this.sessionTrackerProvider = sessionTrackerProvider;
  }

  public static MembersInjector<WhatsAppAccessibilityService> create(
      Provider<WhatsAppAccessibilityParser> parserProvider,
      Provider<SessionTrackerManager> sessionTrackerProvider) {
    return new WhatsAppAccessibilityService_MembersInjector(parserProvider, sessionTrackerProvider);
  }

  @Override
  public void injectMembers(WhatsAppAccessibilityService instance) {
    injectParser(instance, parserProvider.get());
    injectSessionTracker(instance, sessionTrackerProvider.get());
  }

  @InjectedFieldSignature("com.whatsapptracker.service.WhatsAppAccessibilityService.parser")
  public static void injectParser(WhatsAppAccessibilityService instance,
      WhatsAppAccessibilityParser parser) {
    instance.parser = parser;
  }

  @InjectedFieldSignature("com.whatsapptracker.service.WhatsAppAccessibilityService.sessionTracker")
  public static void injectSessionTracker(WhatsAppAccessibilityService instance,
      SessionTrackerManager sessionTracker) {
    instance.sessionTracker = sessionTracker;
  }
}
