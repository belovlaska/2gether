package ru.ifmo.is.together.common.framework;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CrudRepository<T extends CrudEntity>
  extends JpaRepository<T, Integer>,
          JpaSpecificationExecutor<T> {
}
