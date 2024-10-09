package org.dolphina.dataInsert;

import com.github.javafaker.Faker;
import org.springframework.batch.item.ItemReader;
import java.util.Locale;

public class ProductItemReader implements ItemReader<Product> {

    private final Faker faker = new Faker(new Locale("en-US"));
    private int count = 0;
    private final int maxCount;  // 생성할 데이터의 총 개수

    public ProductItemReader(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public Product read() throws Exception {
        if (count < maxCount) {
            count++;
            return new Product(
                    "P_" + faker.random().hex().substring(0,7) + "_" + faker.random().hex().substring(0,7),
                    faker.commerce().productName(),
                    faker.commerce().material() + " " + faker.company().catchPhrase() + " " + faker.lorem().paragraph(),
                    faker.number().randomDouble(2, 10, 1000),
                    faker.commerce().department(),
                    faker.company().name(),
                    faker.number().randomDouble(1, 1, 5)
            );
        } else {
            return null; // 데이터 생성 완료 시 null을 반환하여 Spring Batch에서 종료
        }
    }
}
