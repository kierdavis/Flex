Flex
====

Flex is a lightweight Java framework to make handling commands in Bukkit plugins
easier.

## Download

### Stable

* Version: 0.2.10
* [Download][stable-dl]
* [API Docs][stable-docs]

[stable-dl]: http://bukkit.kierdavis.com/Flex/v0.2.10/Flex.jar
[stable-docs]: http://bukkit.kierdavis.com/Flex/v0.2.10/javadoc/

### Dev

* [Download][dev-dl]
* [API Docs][dev-docs]

[dev-dl]: http://bukkit.kierdavis.com/Flex/latest/Flex.jar
[dev-docs]: http://bukkit.kierdavis.com/Flex/latest/javadoc/

## Examples

MyHandler.java:

    package net.example.myplugin;
    
    import com.kierdavis.flex.FlexCommandContext;
    import com.kierdavis.flex.FlexHandler;
    import org.bukkit.entity.Player;
    
    public class MyHandler {
        private MyPlugin plugin;
        
        public MyHandler(MyPlugin plugin) {
            this.plugin = plugin;
        }
        
        @FlexHandler("myplugin group create", permission="myplugins.commands.group.create")
        public boolean doCreateGroup(FlexCommandContext ctx) {
            String groupName = ctx.getArg(0);
            if (groupName == null) return false; // groupName will be null if not enough arguments are provided. The sender is notified by ctx.getArg().
            
            Player owner = ctx.getPlayer();
            if (owner == null) return false; // owner will be null if the sender is not a player. The sender is notifid by ctx.getPlayer().
            
            plugin.createGroup(groupName, owner);
            
            // Feed back to the sender.
            ctx.info("Group " + groupName + " created.");
        }
    }

MyPlugin.java:

    package net.example.myplugin;
    
    import com.kierdavis.flex.FlexCommandExecutor;
    import org.bukkit.entity.Player;
    import org.bukkit.plugin.java.JavaPlugin;
    
    public class MyPlugin extends JavaPlugin {
        @Override
        public void onEnable() {
            MyHandler handler = new MyHandler(this);
            
            FlexCommandExecutor.getInstance().addHandler(this, handler);
        }
        
        public void createGroup(String groupName, Player owner) {
            // .....
        }
    }

Alternatively, take a look at some real world plugins using Flex:

* **[UltraCommand][ultracommand]**: [AddCommandHandler.java][ultracommand-handler] (handler), [UltraCommand.java][ultracommand-plugin] (plugin)

[ultracommand]: http://dev.bukkit.org/bukkit-mods/ultracommand/
[ultracommand-handler]: https://github.com/kierdavis/UltraCommand/tree/master/src/AddCommandHandler.java
[ultracommand-plugin]: https://github.com/kierdavis/UltraCommand/tree/master/src/UltraCommand.java
