// src/main/java/com/ecommerce/ecommerce/Repositories/BaseRepository.java
package com.ecommerce.ecommerce.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

// @NoRepositoryBean es importante para que Spring Data JPA no intente crear una implementación
// para esta interfaz directamente, ya que es una interfaz base para otros repositorios.
@NoRepositoryBean
public interface BaseRepository<E, ID> extends JpaRepository<E, ID> {
    // Aquí puedes añadir métodos comunes a todos tus repositorios si los necesitas
}