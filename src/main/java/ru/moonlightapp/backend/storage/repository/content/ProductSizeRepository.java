package ru.moonlightapp.backend.storage.repository.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.moonlightapp.backend.storage.model.content.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {

    @Query(value = """
            select s.size from product_sizes s
            join product_size_mappings m on m.product_size_id = s.id
            where s.product_types & ?1 <> 0
            group by s.size
            order by count(*) desc
            limit ?2
            """, nativeQuery = true)
    float[] findPopularSizes(int productTypeBit, int limit);

}
