package ru.ifmo.is.together.common.search;

import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecificationBuilder<T> {

  private final List<SearchCriteria> params;

  public SearchSpecificationBuilder() {
    this.params = new ArrayList<>();
  }

  public final SearchSpecificationBuilder<T> with(
    @NonNull String key,
    @NonNull String operation,
    @NonNull Object value
  ) {
    params.add(new SearchCriteria(key, operation, value));
    return this;
  }

  public final SearchSpecificationBuilder<T> with(@NonNull SearchCriteria searchCriteria) {
    params.add(searchCriteria);
    return this;
  }

  public Specification<T> build() {
    if (params.isEmpty()) return null;

    Specification<T> result = new SearchSpecification<T>(params.get(0));

    for (int idx = 1; idx < params.size(); idx++) {
      SearchCriteria criteria = params.get(idx);
      result = SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
        ? Specification.where(result).and(new SearchSpecification<T>(criteria))
        : Specification.where(result).or(new SearchSpecification<T>(criteria));
    }

    return result;
  }
}
