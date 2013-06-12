package com.kierdavis.flex;

import org.bukkit.Bukkit;

public class FlexRootDispatcher extends FlexDispatcher {
    @Override
    protected FlexDispatcher getOrCreateChild(String name) {
        Bukkit.getPluginCommand(name).setExecutor(FlexCommandExecutor.getInstance());
        return super.getOrCreateChild(name);
    }
    
    @Override
    public boolean dispatch(FlexCommandContext ctx) {
        String name = ctx.getCommand().getName().toLowerCase();
        
        if (children != null && children.containsKey(name)) {
            FlexDispatcher child = children.get(name);
            ctx.pushPathComponent(name);
            return child.dispatch(ctx);
        }
        
        //if (hctx != null) {
        //    return hctx.invoke(ctx);
        //}
        
        invalidCommand(ctx, name);
        return false;
    }
}
