package com.whatsapptracker.service;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class WhatsAppAccessibilityParser_Factory implements Factory<WhatsAppAccessibilityParser> {
  @Override
  public WhatsAppAccessibilityParser get() {
    return newInstance();
  }

  public static WhatsAppAccessibilityParser_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static WhatsAppAccessibilityParser newInstance() {
    return new WhatsAppAccessibilityParser();
  }

  private static final class InstanceHolder {
    private static final WhatsAppAccessibilityParser_Factory INSTANCE = new WhatsAppAccessibilityParser_Factory();
  }
}
