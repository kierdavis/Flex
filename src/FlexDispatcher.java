package com.kierdavis.flex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlexDispatcher {
    protected FlexHandlingContext hctx;
    protected Map<String, FlexDispatcher> children;
    
    public FlexDispatcher() {
        hctx = null;
        children = null;
    }
    
    public FlexHandlingContext getHandlingContext() {
        return hctx;
    }
    
    protected Map<String, FlexDispatcher> getChildren() {
        if (children == null) {
            children = new HashMap<String, FlexDispatcher>();
        }
        
        return children;
    }
    
    protected FlexDispatcher getOrCreateChild(String name) {
        FlexDispatcher child = getChildren().get(name.toLowerCase());
        if (child == null) {
            child = new FlexDispatcher();
            getChildren().put(name.toLowerCase(), child);
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
    
    public void extend(String[] path, FlexDispatcher source) {
        if (path == null || path.length == 0) {
            hctx = source.hctx;
            getChildren().putAll(source.children);
        }
        
        else {
            String name = path[0];
            String[] remaining = Arrays.copyOfRange(path, 1, path.length);
            
            getOrCreateChild(name).extend(remaining, source);
        }
    }
    
    public FlexDispatcher traverse(String[] path) {
        if (path == null || path.length == 0) {
            return this;
        }
        
        else {
            String name = path[0];
            String[] remaining = Arrays.copyOfRange(path, 1, path.length);
            
            if (children.containsKey(name)) {
                return children.get(name).traverse(remaining);
            }
            else {
                return null;
            }
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
        
        invalidCommand(ctx, ctx.getPath());
        return false;
    }
    
    public void invalidCommand(FlexCommandContext ctx, String path) {
        if (children == null) {
            ctx.error("No such command /" + path);
        }
        
        else {
            ctx.info("Subcommands of /" + path + ":");
            
            Iterator<String> it = children.keySet().iterator();
            while (it.hasNext()) {
                ctx.info("  /" + path + " " + (String) it.next());
            }
        }
    }
}
