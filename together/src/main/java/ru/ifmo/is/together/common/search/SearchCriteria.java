package ru.ifmo.is.together.common.search;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

  private String filterKey;
  private String operation;
  private Object value;
  private String dataOption;

  public SearchCriteria(String filterKey, String operation, Object value) {
    this.filterKey = filterKey;
    this.operation = operation;
    this.value = value;
  }
}
