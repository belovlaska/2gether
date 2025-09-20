package ru.ifmo.is.together.common.search;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SearchMapper<T> {
  public Specification<T> map(SearchDto searchDto) {
    var builder = new SearchSpecificationBuilder<T>();
    if (searchDto == null) return builder.build();

    var criteriaList = searchDto.getSearchCriteriaList();
    if (criteriaList == null) return builder.build();

    var dataOpinion = searchDto.getDataOption();
    criteriaList.forEach(criteria -> {
      criteria.setDataOption(dataOpinion);
      builder.with(criteria);
    });

    return builder.build();
  }
}
