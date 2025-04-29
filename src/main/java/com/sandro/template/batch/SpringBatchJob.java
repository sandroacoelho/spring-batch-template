package com.sandro.template.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import static com.sandro.template.batch.SpringBatchListener.SPRING_BATCH_MESSAGE;

@Configuration
public class SpringBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public SpringBatchJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public Job springBatchThreeStepsJob() {
        return new JobBuilder("springBatchThreeStepsJob", jobRepository)
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 1: " + chunkContext.getStepContext().getJobParameters().get(SPRING_BATCH_MESSAGE));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 2: " + chunkContext.getStepContext().getJobParameters().get(SPRING_BATCH_MESSAGE));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Step 3: " + chunkContext.getStepContext().getJobParameters().get(SPRING_BATCH_MESSAGE));
                    //Thread.sleep(Duration.of(1, ChronoUnit.MINUTES));
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

}
