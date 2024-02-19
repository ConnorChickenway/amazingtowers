package xyz.connorchickenway.towers.cmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCommand {

    String subcmd();

    String[] usage();

    String[] error() default "";

    int max_args();

    boolean builder_cmd();

    boolean wand_usage() default false;

    boolean can_console() default false;

    boolean setup_cmd() default false;

}
