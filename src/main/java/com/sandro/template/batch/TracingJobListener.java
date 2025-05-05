package com.sandro.template.batch;

import io.opentelemetry.api.trace.*;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.util.UUID;

import static io.opentelemetry.instrumentation.api.incubator.log.LoggingContextConstants.SPAN_ID;
import static io.opentelemetry.instrumentation.api.incubator.log.LoggingContextConstants.TRACE_ID;

@Slf4j
@RequiredArgsConstructor
public class TracingJobListener implements JobExecutionListener {

    private final Tracer tracer;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Span span = tracer.spanBuilder("batch-job-span").startSpan();

        jobExecution.getExecutionContext().put(TRACE_ID, span.getSpanContext().getTraceId());
        jobExecution.getExecutionContext().put(SPAN_ID, span.getSpanContext().getSpanId());

        MDC.put(TRACE_ID, span.getSpanContext().getTraceId());
        MDC.put(SPAN_ID, span.getSpanContext().getSpanId());

        span.setAttribute("batch.job.name", jobExecution.getJobInstance().getJobName());
        span.setAttribute("batch.job.id", jobExecution.getId());
        span.setAttribute("batch.id", UUID.randomUUID().toString());
        span.setAttribute("batch.user", UUID.randomUUID().toString());

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);

        String traceId = jobExecution.getExecutionContext().getString(TRACE_ID);
        String spanId = jobExecution.getExecutionContext().getString(SPAN_ID);

        SpanContext parentContext = SpanContext.createFromRemoteParent(
                traceId, spanId,
                TraceFlags.getSampled(), TraceState.getDefault());

        Span stepSpan = tracer.spanBuilder("step")
                .setParent(Context.root().with(Span.wrap(parentContext)))
                .startSpan();
        stepSpan.end();
    }
}
