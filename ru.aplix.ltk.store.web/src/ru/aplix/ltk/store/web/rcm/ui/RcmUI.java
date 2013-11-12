package ru.aplix.ltk.store.web.rcm.ui;

import java.lang.annotation.*;

import org.springframework.stereotype.Controller;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@RcmUIQualifier
public @interface RcmUI {

	String value() default "";

}
