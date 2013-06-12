package com.kierdavis.flex;

public class FlexRootDispatcher extends FlexDispatcher {
    public void add(String[] path, FlexHandlingContext hctx) {
        super.add(path, hctx);
        
        String name = path[0];
        hctx.getPlugin().getServer().getPluginCommand(name).setExecutor(FlexCommandExecutor.getInstance());
    }
}
