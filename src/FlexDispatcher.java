package com.kierdavis.flex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FlexDispatcher {
    protected FlexHandlingContext hctx;
    protected Map<String, FlexDispatcher> children;
    
    public FlexDispatcher() {
        hctx = null;
        children = null;
    }
    
    protected Map<String, FlexDispatcher> getOrCreateMap() {
        if (children == null) {
            children = new HashMap<String, FlexDispatcher>();
        }
        
        return children;
    }
    
    protected FlexDispatcher getOrCreateChild(String name) {
        FlexDispatcher child = getOrCreateMap().get(name.toLowerCase());
        if (child == null) {
            child = new FlexDispatcher();
            getOrCreateMap().put(name.toLowerCase(), child);
        }
        
        return child;
    }
    
    public void add(String[] path, FlexHandlingContext hctx_) {
        if (path == null || path.length == 0) {
            hctx = hctx_;
        }
        
        else {
            String name = path[0];
            String[] remaining = Arrays.copyOfRange(path, 1, path.length);
            
            getOrCreateChild(name).add(remaining, hctx_);
        }
    }
    
    public boolean dispatch(FlexCommandContext ctx) {
        if (ctx.numArgs() > 0 && children != null) {
            String name = ctx.getArg(0).toLowerCase();
            
            if (children.containsKey(name)) {
                FlexDispatcher child = children.get(name);
                ctx.popArg(0);
                ctx.pushPathComponent(name);
                return child.dispatch(ctx);
            }
        }
        
        if (hctx != null) {
            return hctx.invoke(ctx);
        }
        
        ctx.error("No such command '" + ctx.getPath() + "'");
        return false;
    }
}
