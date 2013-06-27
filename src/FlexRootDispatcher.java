package com.kierdavis.flex;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

public class FlexRootDispatcher extends FlexDispatcher {
    @Override
    protected FlexDispatcher getOrCreateChild(String name) {
        PluginCommand cmd = Bukkit.getPluginCommand(name);
        
        if (cmd != null) {
            cmd.setExecutor(FlexCommandExecutor.getInstance());
        }
        else {
            Bukkit.getLogger().warning("No plugin command registered with Bukkit for the command root '" + name + "'. Did the author forget to add it to their plugin.yml?");
        }
        
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
