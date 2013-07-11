package com.kierdavis.flex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Server;

/**
 * Contains the information provided to the top-level onCommand implementation,
 * and also provides utility methods for handling code.
 *
 * @see org.bukkit.command.CommandExecutor#onCommand
 */
public class FlexCommandContext {
    /**
     * The sender of the command.
     */
    protected CommandSender sender;
    
    /**
     * The top-level command object.
     */
    protected Command cmd;
    
    /**
     * The alias used for the top-level command.
     */
    protected String label;
    
    /**
     * The arguments given to the command. This array does not include arguments
     * that are processed by Flex as subcommand names.
     */
    protected String[] args;
    
    /**
     * The command path.
     */
    protected List<String> path;
    protected StringBuilder pathBuilder; 
    
    /**
     * Constructor. The arguments should be those passed to onCommand by Bukkit.
     *
     * @param sender_   the source of the command
     * @param cmd_      the command which was executed
     * @param label_    the alias of the command which was used
     * @param args_     the command arguments passed
     * @see org.bukkit.command.CommandExecutor#onCommand
     */
    public FlexCommandContext(CommandSender sender_, Command cmd_, String label_, String[] args_) {
        sender = sender_;
        cmd = cmd_;
        label = label_;
        args = args_;
        path = new ArrayList<String>();
        pathBuilder = new StringBuilder();
    }
    
    /**
     * Returns the source of the command (e.g. a {@link org.bukkit.entity.Player}
     * or {@link org.bukkit.command.ConsoleCommandSender}).
     *
     * @return the source of the command
     */
    public CommandSender getSender() {
        return sender;
    }
    
    /**
     * Returns whether the command was executed by a player.
     *
     * @return  <code>true</code> if the command was run by a player, else
     *          <code>false</code>
     */
    public boolean isPlayer() {
        return sender instanceof Player;
    }
    
    /**
     * If the command was run by a player, the {@link org.bukkit.entity.Player}
     * object representing the player is returned. Otherwise, the message
     * <code>"This command can only be used by players."</code> is sent to the
     * sender and <code>null</code> is returned.
     *
     * To avoid sending the message use {@link #isPlayer} to check the type of
     * the sender and <code>(Player) ctx.getSender()</code> to retrieve the
     * Player object.
     *
     * @return the Player that executed the command, or <code>null</code>
     */
    public Player getPlayer() {
        if (isPlayer()) {
            return (Player) sender;
        }
        else {
            error("This command can only be used by players.");
            return null;
        }
    }
    
    /**
     * Returns the Command object representing the top-level command.
     *
     * @return the top-level Command
     */
    public Command getCommand() {
        return cmd;
    }
    
    /**
     * Returns the alias used for the top-level command. This is part of
     * Bukkit's alias system, not the one implemented in Flex with the
     * {@link FlexCommandExecutor#alias} methods. To find the Flex alias use
     * {@link #getPath}.
     *
     * @return the Bukkit alias for the command
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Returns the remaining arguments to the command after subcommand names
     * have been processed.
     *
     * @return the command arguments
     */
    public String[] getArgs() {
        return args;
    }
    
    /**
     * Returns the argument indexed by <code>pos</code>. If <code>pos</code> is
     * above the upper bound of the array then a message is sent to the command
     * sender informing them that not enough arguments were supplied to the
     * command, and <code>null</code> is returned.
     *
     * To avoid sending the message, use <code>ctx.getArg(pos, null)</code>.
     *
     * @param pos   the argument index to retrieve
     * @return      the specified argument, or <code>null</code>
     * @see #getArg(int, String)
     */
    public String getArg(int pos) {
        if (pos < args.length) {
            return args[pos];
        }
        else {
            error("Not enough arguments for command '" + getPathString() + "'");
            return null;
        }
    }
    
    /**
     * Returns the argument indexed by <code>pos</code>. If not enough arguments
     * were supplied to the command for <code>pos</code> to be a valid index,
     * <code>fallback</code> is returned instead. No message is sent to the
     * user.
     *
     * @param pos       the argument index to retrieve
     * @param fallback  the default value
     * @return          the specified argument, or <code>fallback</code>
     * @see #getArg(int)
     */
    public String getArg(int pos, String fallback) {
        if (pos <= args.length) {
            return args[pos];
        }
        else {
            return fallback;
        }
    }
    
    /**
     * Returns the number of arguments supplied to the command.
     *
     * @return the number of arguments
     */
    public int numArgs() {
        return args.length;
    }
    
    /**
     * Removes and returns the first argument. <code>null</code> is returned if
     * there are not enough arguments.
     *
     * @return the popped argument
     */
    public String popArg() {
        String arg = getArg(0, null);
        if (arg == null) {
            return null;
        }
        
        args = Arrays.copyOfRange(args, 1, args.length);
        return arg;
    }
    
    /**
     * Joins all arguments, starting from index <code>start</code>, into a
     * string seperated by spaces.
     *
     * @param start the start index
     * @return      the joined string
     */
    public String argsString(int start) {
        StringBuilder b = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            if (i > 0) b.append(" ");
            b.append(args[i]);
        }
        return b.toString();
    }
    
    /**
     * Joins all arguments into a string seperated by spaces.
     *
     * @return the joined string
     */
    public String argsString() {
        return argsString(0);
    }
    
    /**
     * Returns the command path (the top-level command name followed by
     * subcommand names).
     *
     * @return the command path
     */
    public List<String> getPath() {
        return path;
    }
    
    /**
     * Returns the command path (the top-level command name followed by
     * subcommand names) as a space-seperated string.
     *
     * @return the command path
     */
    public String getPathString() {
        return pathBuilder.toString();
    }
    
    /**
     * Appends a path component (subcommand name) to {@link #path}.
     *
     * @param part the subcommand name to add
     */
    protected void pushPathComponent(String part) {
        if (pathBuilder.length() > 0) {
            pathBuilder.append(" ");
        }
        
        pathBuilder.append(part);
        path.add(part);
    }
    
    /**
     * Sends a gold-coloured message to the sender.
     *
     * @param message the message to send
     */
    public void info(String message) {
        sender.sendMessage(ChatColor.GOLD + message);
    }
    
    /**
     * Sends a red-coloured message to the sender, prefixed with "Error: ".
     *
     * @param message the message to send
     */
    public void error(String message) {
        sender.sendMessage(ChatColor.RED + "Error: " + message);
    }
    
    /**
     * Returns the {@link Server} associated with the command sender.
     *
     * @return the server
     */
    public Server getServer() {
        return sender.getServer();
    }
    
    /**
     * Returns the {@link Logger} associated with the command sender's
     * {@link Server}.
     *
     * @return the server logger
     */
    public Logger getLogger() {
        return getServer().getLogger();
    }
    
    /**
     * Returns the {@link FlexDispatcher} responsible for this command.
     *
     * @return this command's dispatcher
     */
    public FlexDispatcher getDispatcher() {
        return FlexCommandExecutor.getInstance().getRoot().traverse(path.toArray(new String[0]));
    }
    
    /**
     * Shows subcommand help for this command.
     */
    public void showSubcommands() {
        getDispatcher().showSubcommands(this, getPathString());
    }
}
