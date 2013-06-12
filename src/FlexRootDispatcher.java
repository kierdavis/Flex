package com.kierdavis.flex;

public class FlexRootDispatcher extends FlexDispatcher {
    @Override
    public void add(String[] path, FlexHandlingContext hctx) {
        super.add(path, hctx);
        
        String name = path[0];
        hctx.getPlugin().getServer().getPluginCommand(name).setExecutor(FlexCommandExecutor.getInstance());
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
        
        ctx.error("No such command '" + name + "'");
        return false;
    }
}
