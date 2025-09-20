package ru.ifmo.is.together.common.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import ru.ifmo.is.together.common.utils.datetime.ZonedDateTimeDeserializer;
import ru.ifmo.is.together.common.utils.datetime.ZonedDateTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Configuration
public class JsonConfiguration {
  @Bean
  public Module hibernateModule() {
    return new Hibernate5JakartaModule();
  }

  @Bean
  public Jackson2ObjectMapperBuilder objectMapperBuilder() {
    var builder = new Jackson2ObjectMapperBuilder();
    // formatter
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // deserializers
    builder.deserializers(new LocalDateDeserializer(dateFormatter));
    builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
    builder.deserializers(new ZonedDateTimeDeserializer());

    // serializers
    builder.serializers(new LocalDateSerializer(dateFormatter));
    builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
    builder.serializers(new ZonedDateTimeSerializer());

    builder.modulesToInstall(new JsonNullableModule());

    builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));

    return builder;
  }
}
