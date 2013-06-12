package com.kierdavis.flex;

import java.lang.reflect.Method;

public class FlexHandlingContext {
    protected Object object;
    protected Method method;
    
    public FlexHandlingContext(Object object_, Method method_) {
        object = object_;
        method = method_;
    }
    
    protected String methodDesc() {
        return "'" + method.getName() + "' of '" + object.getClass().getName() + "'";
    }
    
    public void validate() throws FlexBuildingException {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        if (paramTypes.length != 1 || paramTypes[0] != FlexCommandContext.class) {
            throw new FlexBuildingException("FlexHandler method " + methodDesc() + " must have exactly one parameter (of type FlexCommandContext)");
        }
        
        Class<?> returnType = method.getReturnType();
        if (returnType != Boolean.class) {
            throw new FlexBuildingException("FlexHandler method " + methodDesc() + " must return a boolean");
        }
    }
    
    public boolean invoke(FlexCommandContext ctx) {
        return (boolean) method.invoke(object, ctx);
    }
}
