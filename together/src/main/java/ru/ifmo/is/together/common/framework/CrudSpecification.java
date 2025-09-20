package ru.ifmo.is.together.common.framework;

import org.springframework.data.jpa.domain.Specification;

public abstract class CrudSpecification<T> {
  protected Specification<T> with(String subject, int subjectId) {
    return (root, query, cb) -> cb.equal(root.get(subject).get("id"), subjectId);
  }
}
