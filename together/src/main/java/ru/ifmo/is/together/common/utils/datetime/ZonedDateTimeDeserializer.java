package ru.ifmo.is.together.common.utils.datetime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
  private static final String NULL_VALUE = "null";

  public Class<ZonedDateTime> handledType() {
    return ZonedDateTime.class;
  }

  @Override
  public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    ObjectCodec oc = jp.getCodec();
    JsonNode node = oc.readTree(jp);
    var dateString = node.textValue();

    ZonedDateTime dateTime = null;
    if (!NULL_VALUE.equals(dateString)) {
      dateTime = ZonedDateTime.parse(dateString, ISO_OFFSET_DATE_TIME);
    }
    return dateTime;
  }
}
