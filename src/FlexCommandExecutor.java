package com.kierdavis.flex;

import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Method;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class FlexCommandExecutor implements CommandExecutor {
    protected static FlexCommandExecutor instance = null;
    protected static Set<Plugin> clients = new HashSet<Plugin>();
    
    public static FlexCommandExecutor getInstance() {
        if (instance == null) {
            instance = new FlexCommandExecutor();
        }
        
        return instance;
    }
    
    protected static String getClientsStr() {
        StringBuilder b = new StringBuilder();
        Iterator<Plugin> it = clients.iterator();
        
        if (!it.hasNext()) return "";
        
        b.append((String) it.next());
        while (it.hasNext()) {
            b.append(", ").append((String) it.next());
        }
        
        return b.toString();
    }
    
    public static void logException(Exception e, String msg) {
        Bukkit.getLogger().log(Level.SEVERE, "[Flex] " + msg, e);
        Bukkit.getLogger().log(Level.SEVERE, "[Flex] This is likely to be a bug in one or more of the plugins using the Flex command handling framework.");
        Bukkit.getLogger().log(Level.SEVERE, "[Flex] Plugins using Flex: " + getClientsStr());
    }
    
    // -------------------------------------------------------------------------
    
    protected FlexDispatcher root;
    
    protected FlexCommandExecutor() {
        root = new FlexDispatcher();
    }
    
    public void addHandler(Plugin plugin, Object handler) {
        clients.add(plugin);
        
        try {
            for (Method m : handler.getClass().getMethods()) {
                if (m.isAnnotationPresent(FlexHandler.class)) {
                    FlexHandler annotation = m.getAnnotation(FlexHandler.class);
                    String[] path = annotation.path().split(" ");
                    String rootName = path[0];
                    
                    plugin.getServer().getPluginCommand(rootName).setExecutor(this);
                    
                    FlexHandlingContext hctx = new FlexHandlingContext(plugin, handler, m);
                    hctx.validate();
                    
                    root.add(path, hctx);
                }
            }
        }
        
        catch (FlexBuildingException e) {
            logException(e, "Error when building command trees");
        }
    }
    
    public void addHandlers(Plugin plugin, Object... handlers) {
        for (Object handler : handlers) {
            addHandler(plugin, handler);
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FlexCommandContext ctx = new FlexCommandContext(sender, cmd, label, args);
        return root.dispatch(ctx);
    }
}
