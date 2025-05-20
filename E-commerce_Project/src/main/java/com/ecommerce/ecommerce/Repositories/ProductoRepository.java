package com.ecommerce.ecommerce.Repositories;

import com.ecommerce.ecommerce.Entities.Producto;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Importar esto
import org.springframework.stereotype.Repository;

// Extender BaseRepository y JpaSpecificationExecutor
@Repository
public interface ProductoRepository extends BaseRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {
}
