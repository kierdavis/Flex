package com.kierdavis.flex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FlexDispatcher {
    protected FlexHandlingContext hctx;
    protected FlexDispatcher parent;
    protected Map<String, FlexDispatcher> children;
    
    public FlexDispatcher() {
        hctx = null;
        parent = null;
        children = null;
    }
    
    public FlexHandlingContext getHandlingContext() {
        return hctx;
    }
    
    public FlexDispatcher getParent() {
        return parent;
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
            child.parent = this;
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
            if (source.children != null) {
                getChildren().putAll(source.children);
            }
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
                ctx.popArg();
                ctx.pushPathComponent(name);
                return child.dispatch(ctx);
            }
        }
        
        if (hctx != null) {
            return hctx.invoke(ctx);
        }
        
        invalidCommand(ctx, ctx.getPathString());
        return false;
    }
    
    protected void invalidCommand(FlexCommandContext ctx, String path) {
        if (children == null) {
            ctx.error(FlexData.getInstance().getText("invalid-command", path));
        }
        
        else {
            showSubcommands(ctx, path);
        }
    }
    
    public void showSubcommands(FlexCommandContext ctx, String path) {
        if (children == null || children.size() == 0) {
            ctx.info(FlexData.getInstance().getText("no-subcommands", path));
        }
        
        else {
            ctx.info(FlexData.getInstance().getText("subcommand-list", path));
            
            String mainArgUsage = "";
            
            if (hctx != null && !(hctx.getPermission() != null && !ctx.getSender().hasPermission(hctx.getPermission()))) {
                if (hctx.getArgUsage() != null) {
                    ctx.info("  /" + path + " " + hctx.getArgUsage());
                }
                else {
                    ctx.info("  /" + path);
                }
            }
            
            Iterator<String> it = children.keySet().iterator();
            while (it.hasNext()) {
                String childName = (String) it.next();
                FlexDispatcher child = children.get(childName);
                String argUsage = "";
                
                if (child.hctx != null) {
                    if (child.hctx.getPermission() != null && !ctx.getSender().hasPermission(child.hctx.getPermission())) {
                        continue;
                    }
                    
                    if (child.hctx.getArgUsage() != null) {
                        argUsage = " " + child.hctx.getArgUsage();
                    }
                }
                
                ctx.info("  /" + path + " " + childName + argUsage);
            }
        }
    }
}
