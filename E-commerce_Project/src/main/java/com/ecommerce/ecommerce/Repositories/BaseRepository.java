package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Base;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<E extends Base, ID extends Serializable> extends JpaRepository<E, ID> {

    List<E> findAllByActivoTrue();
    Page<E> findAllByActivoTrue(Pageable pageable);
    Optional<E> findByIdAndActivoTrue(ID id);
}