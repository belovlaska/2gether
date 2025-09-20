package ru.ifmo.is.together.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.Locale;

@Configuration
public class LocaleProvider {
  @Bean
  public Locale getLocale() {
    return LocaleContextHolder.getLocale();
  }

  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    var lci = new LocaleChangeInterceptor();
    lci.setParamName("locale");
    return lci;
  }

  @Bean
  public LocaleResolver localeResolver() {
    var lr = new AcceptHeaderLocaleResolver();
    lr.setSupportedLocales(Arrays.asList(new Locale("ru"), new Locale("en")));
    lr.setDefaultLocale(new Locale("ru"));
    return lr;
  }
}
