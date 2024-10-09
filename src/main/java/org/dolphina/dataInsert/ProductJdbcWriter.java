package org.dolphina.dataInsert;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import javax.sql.DataSource;

public class ProductJdbcWriter extends JdbcBatchItemWriter<Product> {

    public ProductJdbcWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("INSERT INTO products (product_id, name, description, price, category, brand, rating) " +
                "VALUES (:productId, :name, :description, :price, :category, :brand, :rating)");
        setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
    }
}
