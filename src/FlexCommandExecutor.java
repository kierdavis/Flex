package com.kierdavis.flex;

import java.lang.reflect.Method;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FlexCommandExecutor implements CommandExecutor {
    protected FlexDispatcher root;
    
    public FlexCommandExecutor() {
        root = new FlexDispatcher();
    }
    
    public FlexCommandExecutor(Object handler) throws FlexBuildingException {
        this();
        addHandler(handler);
    }
    
    public FlexCommandExecutor(Object... handlers) throws FlexBuildingException {
        this();
        
        for (Object handler : handlers) {
            addHandler(handler);
        }
    }
    
    public void addHandler(Object handler) throws FlexBuildingException {
        for (Method m : handler.getClass().getMethods()) {
            if (m.isAnnotationPresent(FlexHandler.class)) {
                FlexHandler annotation = m.getAnnotation(FlexHandler.class);
                String[] path = annotation.path().split(" ");
                
                FlexHandlingContext hctx = new FlexHandlingContext(handler, m);
                hctx.validate();
                
                root.add(path, hctx)
            }
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FlexCommandContext ctx = new FlexCommandContext(sender, cmd, label, args);
        return root.dispatch(ctx);
    }
}
