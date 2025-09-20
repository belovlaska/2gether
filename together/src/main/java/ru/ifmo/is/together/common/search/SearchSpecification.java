package ru.ifmo.is.together.common.search;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@RequiredArgsConstructor
public class SearchSpecification<T> implements Specification<T> {
  private final SearchCriteria searchCriteria;

  @Override
  public Predicate toPredicate(
    @NonNull Root<T> root,
    @Nullable CriteriaQuery<?> query,
    @NonNull CriteriaBuilder cb
  ) {
    var strToSearch = searchCriteria.getValue().toString().toLowerCase();

    return switch (
      Objects.requireNonNull(
        SearchOperation.getSimpleOperation(searchCriteria.getOperation())
      )) {
      case CONTAINS -> cb.like(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        "%" + strToSearch + "%"
      );
      case DOES_NOT_CONTAIN -> cb.notLike(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        "%" + strToSearch + "%"
      );
      case STR_CONTAINS -> cb.like(
        cb.lower(root.get(searchCriteria.getFilterKey()).as(String.class)),
        "%" + strToSearch + "%"
      );
      case STR_DOES_NOT_CONTAIN -> cb.notLike(
        cb.lower(root.get(searchCriteria.getFilterKey()).as(String.class)),
        "%" + strToSearch + "%"
      );
      case BEFORE -> {
        var date = ZonedDateTime.parse(searchCriteria.getValue().toString(), ISO_DATE_TIME);
        yield cb.lessThanOrEqualTo(root.<ZonedDateTime>get(searchCriteria.getFilterKey()), date);
      }
      case AFTER -> {
        var date = ZonedDateTime.parse(searchCriteria.getValue().toString(), ISO_DATE_TIME);
        yield cb.greaterThanOrEqualTo(root.<ZonedDateTime>get(searchCriteria.getFilterKey()), date);
      }
      case BEGINS_WITH -> cb.like(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        strToSearch + "%"
      );
      case DOES_NOT_BEGIN_WITH -> cb.notLike(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        strToSearch + "%"
      );
      case ENDS_WITH -> cb.like(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        "%" + strToSearch
      );
      case DOES_NOT_END_WITH -> cb.notLike(
        cb.lower(root.get(searchCriteria.getFilterKey())),
        "%" + strToSearch
      );
      case EQUAL -> cb.equal(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue()
      );
      case NOT_EQUAL -> cb.notEqual(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue()
      );
      case STR_EQUAL -> cb.equal(
        root.get(searchCriteria.getFilterKey()).as(String.class),
        searchCriteria.getValue().toString()
      );
      case STR_NOT_EQUAL -> cb.notEqual(
        root.get(searchCriteria.getFilterKey()).as(String.class),
        searchCriteria.getValue().toString()
      );
      case NUL -> cb.isNull(
        root.get(searchCriteria.getFilterKey())
      );
      case NOT_NULL -> cb.isNotNull(
        root.get(searchCriteria.getFilterKey())
      );
      case GREATER_THAN -> cb.greaterThan(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue().toString()
      );
      case GREATER_THAN_EQUAL -> cb.greaterThanOrEqualTo(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue().toString()
      );
      case LESS_THAN -> cb.lessThan(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue().toString()
      );
      case LESS_THAN_EQUAL -> cb.lessThanOrEqualTo(
        root.get(searchCriteria.getFilterKey()),
        searchCriteria.getValue().toString()
      );
      default -> null;
    };
  }
}
