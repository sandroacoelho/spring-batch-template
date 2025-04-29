package com.sandro.template.batch;

import io.opentelemetry.api.trace.Span;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.util.UUID;

public class TracingJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Span span = Span.current();

        span.setAttribute("batch.job.name", jobExecution.getJobInstance().getJobName());
        span.setAttribute("batch.job.id", jobExecution.getId());
        span.setAttribute("batch.id", UUID.randomUUID().toString());
        span.setAttribute("batch.user", UUID.randomUUID().toString());

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);
    }
}
