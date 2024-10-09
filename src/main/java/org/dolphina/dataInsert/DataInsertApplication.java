package org.dolphina.dataInsert;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.dolphina.dataInsert"})  // DataSourceConfig가 위치한 패키지 지정
public class DataInsertApplication {

	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BatchConfig.class);
		JobLauncher jobLauncher = context.getBean(JobLauncher.class);
		Job job = context.getBean("productImportJob", Job.class);

		JobExecution execution = jobLauncher.run(job, new JobParametersBuilder().toJobParameters());
		System.out.println("Job Exit Status : " + execution.getStatus());

		context.close();
	}
}
