package kr.co.jsol.jagency.common.infrastructure.annotation;

import org.springframework.format.annotation.DateTimeFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
public @interface JDateFormat {
}
