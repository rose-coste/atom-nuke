package org.atomnuke.atom.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.atomnuke.atom.model.constraint.ValueConstraint;

/**
 *
 * @author zinic
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComplexConstraint {

   Class<? extends ValueConstraint> value();
}
