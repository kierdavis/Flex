package com.kierdavis.flex;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

public class FlexHandlingContext {
    protected Plugin plugin;
    protected Object object;
    protected Method method;
    
    public FlexHandlingContext(Plugin plugin_, Object object_, Method method_) {
        plugin = plugin_;
        object = object_;
        method = method_;
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
    
    protected String methodDesc() {
        return "method " + method.getName() + " of class " + object.getClass().getName() + " in plugin " + plugin.getName();
    }
    
    public void validate() throws FlexBuildingException {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        if (paramTypes.length != 1 || paramTypes[0] != FlexCommandContext.class) {
            throw new FlexBuildingException("FlexHandler " + methodDesc() + " must have exactly one parameter (of type FlexCommandContext)");
        }
        
        Class<?> returnType = method.getReturnType();
        if (!returnType.equals(Boolean.class)) {
            throw new FlexBuildingException("FlexHandler " + methodDesc() + " must return a boolean");
        }
    }
    
    public boolean invoke(FlexCommandContext ctx) {
        try {
            return (boolean) method.invoke(object, ctx);
        }
        
        catch (IllegalAccessException e) {
            FlexCommandExecutor.logException(e, "Unexpected IllegalAccessException when attempting to invoke " + methodDesc());
            
            ctx.error("There was an unhandled error while executing the command. Please contact an administrator.");
            return false;
        }
        
        catch (InvocationTargetException e) {
            FlexCommandExecutor.logException(e, "Unexpected InvocationTargetException when attempting to invoke " + methodDesc());
            
            ctx.error("There was an unhandled error while executing the command. Please contact an administrator.");
            return false;
        }
    }
}
