package ru.ifmo.is.together.common.utils.datetime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
  public Class<ZonedDateTime> handledType() {
    return ZonedDateTime.class;
  }

  @Override
  public void serialize(ZonedDateTime dateTime, JsonGenerator generator, SerializerProvider provider) throws IOException {
    var dateTimeString = dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()));
    generator.writeString(dateTimeString);
  }
}
