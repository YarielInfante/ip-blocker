package com.ef.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Here we register all of our spring configuration classes. It also contains a Spring context object.
 *
 * @author yinfante
 */
public class SpringRegistry {

    // spring context
    private static AnnotationConfigApplicationContext context;

    /**
     * Registering all our spring configuration classes.
     */
    static {
        context = new AnnotationConfigApplicationContext();
        // scan packages in search of spring annotated components
        context.scan("com.ef");

        refreshContext();
    }

    /**
     * It returns an instance of spring context
     *
     * @return an AnnotationConfigApplicationContext instance
     * @see AnnotationConfigApplicationContext
     */
    public static AnnotationConfigApplicationContext getContext() {
        return context;
    }

    /**
     * It refreshes spring context
     */
    public static void refreshContext() {
        context.refresh();
    }


}
