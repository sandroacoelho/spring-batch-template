package com.sandro.template.batch;

import com.sandro.template.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class SpringBatchListener {

    public static final String SPRING_BATCH_MESSAGE = "message";
    public static final String ID = "id";

    private final JobLauncher jobLauncher;

    private final Job remessaThreeStepsJob;

    public SpringBatchListener(JobLauncher jobLauncher, Job remessaThreeStepsJob) {
        this.jobLauncher = jobLauncher;
        this.remessaThreeStepsJob = remessaThreeStepsJob;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receiveMessage(String message) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {

        processMessage(message);

    }

    public void processMessage(String message) throws JobInstanceAlreadyCompleteException,
            JobExecutionAlreadyRunningException,
            JobParametersInvalidException,
            JobRestartException {

        var jobParameters = new JobParameters(Map.of(SPRING_BATCH_MESSAGE,
                new JobParameter<>(message, String.class),
                ID, new JobParameter<>(UUID.randomUUID().toString(), String.class)));

        jobLauncher.run(remessaThreeStepsJob, jobParameters);

    }
}
