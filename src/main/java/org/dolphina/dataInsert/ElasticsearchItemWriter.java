package org.dolphina.dataInsert;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.ArrayList;
import java.util.List;

public class ElasticsearchItemWriter implements ItemWriter<Product> {

    private final ElasticsearchClient client;

    public ElasticsearchItemWriter(ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public void write(Chunk<? extends Product> items) throws Exception {
        // Bulk request를 위한 리스트 생성
        List<BulkOperation> bulkOperations = new ArrayList<>();

        // 각 Product를 BulkOperation으로 변환하여 리스트에 추가
        for (Product product : items) {
            BulkOperation operation = BulkOperation.of(b -> b
                    .index(idx -> idx
                            .index("products")
                            .id(product.getProductId().toString())
                            .document(product)
                    )
            );
            bulkOperations.add(operation);
        }

        // BulkRequest 생성
        BulkRequest bulkRequest = BulkRequest.of(b -> b.operations(bulkOperations));

        // Bulk API 호출
        BulkResponse bulkResponse = client.bulk(bulkRequest);

        // 에러 처리 (BulkResponse 내에서 에러가 발생했는지 확인)
        if (bulkResponse.errors()) {
            // 에러 처리 로직 추가
            throw new RuntimeException("Bulk operation failed: " + bulkResponse.toString());
        }
    }
}
