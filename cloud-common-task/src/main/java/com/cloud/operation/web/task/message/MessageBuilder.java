package com.cloud.operation.web.task.message;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageBuilder {
	String[] value() default "";
}
