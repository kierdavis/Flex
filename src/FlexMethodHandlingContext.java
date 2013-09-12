package com.kierdavis.flex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class FlexMethodHandlingContext implements FlexHandlingContext {
    protected Plugin plugin;
    protected Object object;
    protected Method method;
    protected FlexHandler annotation;
    
    public FlexMethodHandlingContext(Plugin plugin_, Object object_, Method method_) {
        plugin = plugin_;
        object = object_;
        method = method_;
        annotation = method.getAnnotation(FlexHandler.class);
    }
    
    public Plugin getPlugin() {
        return plugin;
    }
    
    public Object getObject() {
        return object;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public FlexHandler getAnnotation() {
        return annotation;
    }
    
    public String getArgUsage() {
        return annotation.argUsage().length() == 0 ? "" : annotation.argUsage();
    }
    
    public String getPermission() {
        return annotation.permission().length() == 0 ? "" : annotation.permission();
    }
    
    protected String methodDesc() {
        return "method " + method.getName() + " of class " + object.getClass().getName() + " in plugin " + plugin.getName();
    }
    
    public void validate() throws FlexBuildingException {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        if (paramTypes.length != 1 || paramTypes[0] != FlexCommandContext.class) {
            throw new FlexBuildingException("FlexHandler " + methodDesc() + " must have exactly one parameter (of type FlexCommandContext)");
        }
        
        Class<?> returnType = method.getReturnType();
        if (!returnType.equals(boolean.class)) {
            throw new FlexBuildingException("FlexHandler " + methodDesc() + " must return a boolean");
        }
    }
    
    public boolean invoke(FlexCommandContext ctx) {
        if (annotation.permission().length() > 0) {
            if (!ctx.getSender().hasPermission(annotation.permission())) {
                ctx.error(FlexData.getInstance().getText("no-permission"));
                return false;
            }
        }
        
        if (annotation.playerOnly()) {
            if (!ctx.isPlayer()) {
                ctx.error(FlexData.getInstance().getText("players-only"));
                return false;
            }
        }
        
        //if (annotation.argNames() != null) {
        //    if (ctx.numArgs() < annotation.argNames().length) {
        //        StringBuilder b = new StringBuilder();
        //        b.append("Usage: /");
        //        b.append(ctx.getPath());
        //        
        //        for (String argName : annotation.argNames()) {
        //            b.append(" ");
        //            if (argName.endsWith("?")) {
        //                b.append("[").append(argName.substring(0, argName.length() - 1)).append("]");
        //            } else {
        //                b.append("<").append(argName).append(">");
        //            }
        //        }
        //        
        //        ctx.error(b.toString());
        //        return false;
        //    }
        //}
        
        try {
            return (boolean) method.invoke(object, ctx);
        }
        catch (IllegalAccessException e) {
            FlexCommandExecutor.logException(plugin, e, "Unexpected IllegalAccessException when attempting to invoke " + methodDesc());
            ctx.error(FlexData.getInstance().getText("internal-error"));
            return false;
        }
        catch (InvocationTargetException e) {
            FlexCommandExecutor.logException(plugin, e, "Unexpected InvocationTargetException when attempting to invoke " + methodDesc());
            ctx.error(FlexData.getInstance().getText("internal-error"));
            return false;
        }
    }
}
