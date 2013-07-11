package com.kierdavis.flex;

public interface FlexHandlingContext {
    public boolean invoke(FlexCommandContext ctx);
    
    public String getArgUsage();
    public String getPermission();
}
