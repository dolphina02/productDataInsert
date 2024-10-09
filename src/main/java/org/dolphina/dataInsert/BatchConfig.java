package org.dolphina.dataInsert;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableTransactionManagement
public class BatchConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/ecommerce");
        dataSource.setUsername("fast");
        dataSource.setPassword("fastcampus");
        return dataSource;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200, "http")
        ).build();

        RestClientTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }

    // 트랜잭션 관리자를 정의하여 트랜잭션 관리 문제 해결
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public Job productImportJob(JobRepository jobRepository, Step productStep) {
        return new JobBuilder("productImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(productStep)
                .build();
    }

    @Bean
    public Step productStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("productStep", jobRepository)
                .<Product, Product>chunk(1000, transactionManager)
                .reader(productReader())
                .writer(compositeWriter())
                .allowStartIfComplete(true)  // 스텝이 완료된 후에도 재실행 가능하도록 설정
                .build();
    }

    @Bean
    public CompositeItemWriter<Product> compositeWriter() {
        CompositeItemWriter<Product> writer = new CompositeItemWriter<>();
        writer.setDelegates(List.of(mysqlWriter(), esWriter()));
        return writer;
    }

    @Bean
    public ProductJdbcWriter mysqlWriter() {
        return new ProductJdbcWriter(dataSource());
    }

    @Bean
    public ElasticsearchItemWriter esWriter() {
        return new ElasticsearchItemWriter(elasticsearchClient());
    }

    @Bean
    public ItemReader<Product> productReader() {
        return new ProductItemReader(5000000);  // Custom reader 구현
    }
}
