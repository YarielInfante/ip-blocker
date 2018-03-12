package com.ef.config;

import com.ef.batch.job.ParserJob;
import com.ef.batch.listener.JobCompletionNotificationListener;
import com.ef.batch.step.BlockedListLoaderStep;
import com.ef.batch.step.EmptyBlockedUserTableStep;
import com.ef.batch.step.EmptyTableStep;
import com.ef.batch.step.FileLoaderStep;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringRegistry {

    private static AnnotationConfigApplicationContext context;

    static {
        context = new AnnotationConfigApplicationContext();

        context.register(EmptyTableStep.class);
        context.register(ParserJob.class);
        context.register(Configurations.class);
        context.register(JobCompletionNotificationListener.class);
        context.register(BlockedListLoaderStep.class);
        context.register(EmptyBlockedUserTableStep.class);
        context.register(FileLoaderStep.class);

        refreshContext();
    }

    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }

    public static void refreshContext() {
        context.refresh();
    }


}
