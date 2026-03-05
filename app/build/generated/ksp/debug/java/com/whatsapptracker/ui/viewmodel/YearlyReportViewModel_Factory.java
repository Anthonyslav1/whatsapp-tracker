package com.whatsapptracker.ui.viewmodel;

import com.whatsapptracker.data.repository.UsageRepository;
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
public final class YearlyReportViewModel_Factory implements Factory<YearlyReportViewModel> {
  private final Provider<UsageRepository> repositoryProvider;

  public YearlyReportViewModel_Factory(Provider<UsageRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public YearlyReportViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static YearlyReportViewModel_Factory create(Provider<UsageRepository> repositoryProvider) {
    return new YearlyReportViewModel_Factory(repositoryProvider);
  }

  public static YearlyReportViewModel newInstance(UsageRepository repository) {
    return new YearlyReportViewModel(repository);
  }
}
