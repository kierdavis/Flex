package com.kierdavis.flex;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark methods on an object as being handlers for commands.
 *
 * Example:
 * <pre>
 * \@FlexHandler("myplugin group add", permission = "myplugin.commands.add")
 * public boolean handleAddGroup(FlexCommandContext ctx) {
 *     // ...
 * }
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FlexHandler {
    /**
     * The command path (the top-level command followed by subcommand names), as
     * a space-seperated string. It is named <code>value</code> to allow the
     * element name to be omitted.
     */
    String value();
    
    /**
     * If this is not the empty string, the command will fail if the sender does
     * not have this permission.
     */
    String permission() default "";
    
    /**
     * If this is true, the command will fail if the sender is not a player.
     */
    boolean playerOnly() default false;
    
    /**
     * A string that represents how arguments should be passed to the command,
     * used in help output. It is recommended that required argument names are
     * enclosed in angle brackets (&lt;...&gt;) and optional argument names are
     * enclosed in square brackets ([...]). Example for a fly-speed command:
     * "&lt;speed&gt; [player]"
     */
    String argUsage() default "";
}
