package uk.ac.cam.ks828.fjava.messages;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Methods in dynamic message classes with no parameters that
 * are labelled with this annotation must be executed.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {}
