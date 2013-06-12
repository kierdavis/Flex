package com.kierdavis.flex;

import java.util.Arrays;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlexCommandContext {
    protected CommandSender sender;
    protected Command cmd;
    protected String label;
    protected String[] args;
    protected StringBuilder pathBuilder;
    
    public FlexCommandContext(CommandSender sender_, Command cmd_, String label_, String[] args_) {
        sender = sender_;
        cmd = cmd_;
        label = label_;
        args = args_;
        pathBuilder = new StringBuilder();
    }
    
    public CommandSender getSender() {
        return sender;
    }
    
    public boolean isPlayer() {
        return sender instanceof Player;
    }
    
    public Player getPlayer() {
        if (isPlayer()) {
            return (Player) sender;
        }
        else {
            error("This command can only be used by players.");
            return null;
        }
    }
    
    public Command getCommand() {
        return cmd;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String[] getArgs() {
        return args;
    }
    
    public String getArg(int pos) {
        if (pos <= args.length) {
            return args[pos];
        }
        else {
            error("Not enough arguments for command '" + getPath() + "'");
            return null;
        }
    }
    
    public String getArg(int pos, String fallback) {
        if (pos <= args.length) {
            return args[pos];
        }
        else {
            return fallback;
        }
    }
    
    public int numArgs() {
        return args.length;
    }
    
    public String popArg(int pos) {
        String arg = getArg(pos);
        if (arg == null) {
            return null;
        }
        
        args = Arrays.copyOfRange(args, 1, args.length);
        return arg;
    }
    
    public String popArg(int pos, String fallback) {
        String arg = getArg(pos, fallback);
        args = Arrays.copyOfRange(args, 1, args.length);
        return arg;
    }
    
    public String getPath() {
        return pathBuilder.toString();
    }
    
    public void pushPathComponent(String part) {
        if (pathBuilder.length() > 0) {
            pathBuilder.append(" ");
        }
        
        pathBuilder.append(part);
    }
    
    public void info(String message) {
        sender.sendMessage(ChatColor.GOLD + message);
    }
    
    public void error(String message) {
        sender.sendMessage(ChatColor.RED + "Error: " + message);
    }
    
    public Server getServer() {
        return sender.getServer();
    }
    
    public Logger getLogger() {
        return getServer().getLogger();
    }
}
