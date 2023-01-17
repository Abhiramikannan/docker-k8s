package com.hsbg.dockercompose.core.repository;

import com.hsbg.dockercompose.core.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface BookRepository extends JpaRepository<BookEntity,Long> {
}
