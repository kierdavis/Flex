package com.kierdavis.flex;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * The root class to which command handlers can be added.
 *
 * This class is a singleton; use the static {@link #getInstance} method rather
 * than instantaniating the class directly.
 */
public class FlexCommandExecutor implements CommandExecutor {
    /**
     * The single instance of this class. It is initalised and returned by
     * {@link #getInstance}.
     */
    protected static FlexCommandExecutor instance = null;
    
    /**
     * A set of all plugins that have registered handlers with this framework.
     * Currently it is only used to provide more information in error messages,
     * but a use may come across for it in the future.
     */
    protected static Set<Plugin> clients = new HashSet<Plugin>();
    
    /**
     * Returns the singleton instance of this class, creating it if needed.
     *
     * @return the instance of FlexCommandExecutor
     */
    public static FlexCommandExecutor getInstance() {
        if (instance == null) {
            instance = new FlexCommandExecutor();
        }
        
        return instance;
    }
    
    /**
     * Builds a string containing the names of all the client plugins that have
     * registered handlers with this framework. Used for error messages.
     *
     * @return the built string
     */
    protected static String getClientsStr() {
        StringBuilder b = new StringBuilder();
        Iterator<Plugin> it = clients.iterator();
        
        if (!it.hasNext()) return "";
        
        b.append(((Plugin) it.next()).getName());
        while (it.hasNext()) {
            b.append(", ").append(((Plugin) it.next()).getName());
        }
        
        return b.toString();
    }
    
    /**
     * Logs an error that was probably caused by a client plugin.
     *
     * @param plugin the plugin that caused the exception, or null
     * @param e      the exception that was thrown
     * @param msg    an accompanying message
     */
    public static void logException(Plugin plugin, Throwable e, String msg) {
        Bukkit.getLogger().log(Level.SEVERE, "[Flex] " + msg, e);
        
        if (plugin != null) {
            /*
            Bukkit.getLogger().log(Level.SEVERE, "[Flex] This may to be a bug in one or more of the plugins using the Flex command handling framework.");
            Bukkit.getLogger().log(Level.SEVERE, "[Flex] Plugins using Flex: " + getClientsStr());
        }
        else {*/
            Bukkit.getLogger().log(Level.SEVERE, "[Flex] The plugin that caused this error is: " + plugin.getName());
        }
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * The root command dispatcher.
     */
    protected FlexRootDispatcher root;
    
    /**
     * This constructor should not be used - the {@link #getInstance} method
     * should be used instead.
     */
    protected FlexCommandExecutor() {
        root = new FlexRootDispatcher();
    }
    
    /**
     * Adds the methods on <code>handler</code> annotated with
     * {@link FlexHandler} to the dispatcher tree.
     *
     * @param plugin    the plugin to bind the commands to. Theoretically this
     *                  parameter can take the value <code>null</code>; however
     *                  it is recommended to pass a valid {@link Plugin} to aid
     *                  server administrators in debugging problems.
     * @param handler   the object to extract handling methods from
     */
    public void addHandler(Plugin plugin, Object handler) {
        if (plugin != null) {
            clients.add(plugin);
        }
        
        try {
            for (Method m : handler.getClass().getMethods()) {
                if (m.isAnnotationPresent(FlexHandler.class)) {
                    FlexHandler annotation = m.getAnnotation(FlexHandler.class);
                    String[] path = annotation.value().split(" ");
                    String rootName = path[0];
                    
                    FlexMethodHandlingContext hctx = new FlexMethodHandlingContext(plugin, handler, m);
                    hctx.validate();
                    
                    root.add(path, hctx);
                }
            }
        }
        
        catch (FlexBuildingException e) {
            logException(plugin, e, "Error when building command trees: " + e.toString());
        }
    }
    
    /**
     * Adds the methods annotated with {@link FlexHandler} on each of the
     * objects passed to the dispatcher tree.
     *
     * @param plugin    the plugin to bind the commands to. Theoretically this
     *                  parameter can take the value <code>null</code>; however
     *                  it is recommended to pass a valid {@link Plugin} to aid
     *                  server administrators in debugging problems.
     * @param handlers  the objects to extract handlnig methods from
     * @see #addHandler
     */
    public void addHandlers(Plugin plugin, Object... handlers) {
        for (Object handler : handlers) {
            addHandler(plugin, handler);
        }
    }
    
    /**
     * Creates an alias under <code>aliasPath</code> for the command tree under
     * <code>originalPath</code>.
     *
     * @param originalPath  the space-seperated path to use as the source.
     *                      Example: <code>"myplugin group add"</code>.
     * @param aliasPath     the space-seperated path to use as the destination.
     *                      Example: <code>"addgroup"</code>.
     */
    public void alias(String originalPath, String aliasPath) {
        alias(originalPath.split(" "), aliasPath.split(" "));
    }
    
    /**
     * Creates an alias under <code>aliasPath</code> for the command tree under
     * <code>originalPath</code>.
     *
     * @param originalPath  the path to use as the source.
     *                      Example: <code>{"myplugin", "group", "add"}</code>.
     * @param aliasPath     the path to use as the destination.
     *                      Example: <code>{"addgroup"}</code>.
     */
    public void alias(String[] originalPath, String[] aliasPath) {
        FlexDispatcher source = root.traverse(originalPath);
        root.extend(aliasPath, source);
    }
    
    /**
     * Executes the given command, returning its success.
     *
     * @param sender    the source of the command
     * @param cmd       the command which was executed
     * @param label     the alias of the command which was used
     * @param args      the command arguments passed
     * @return          <code>true</code> if the command succeeded, else
     *                  <code>false</code>
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FlexCommandContext ctx = new FlexCommandContext(sender, cmd, label, args);
        return root.dispatch(ctx);
    }
    
    /**
     * Returns the root dispatcher.
     * 
     * @return the root dispatcher
     */
    public FlexRootDispatcher getRoot() {
        return root;
    }
}
