package org.kxysl1k.netherControl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherControl extends JavaPlugin implements Listener {

    private boolean isNetherOpen;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        isNetherOpen = this.getConfig().getBoolean("nether-open", false);
        getLogger().info("NetherControl включен! Состояние ада: " + (isNetherOpen ? "открыто" : "закрыто"));
        Bukkit.getPluginManager().registerEvents(this, this);
        this.getCommand("nether").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("NetherControl выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("nether")) {
            if (!sender.hasPermission("nethercontrol.admin")) {
                sender.sendMessage("§cУ вас нет разрешения на использование этой команды!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("§cИспользуйте: /nether <open|close>");
                return true;
            }

            if (args[0].equalsIgnoreCase("open")) {
                isNetherOpen = true;
                this.getConfig().set("nether-open", true);
                this.saveConfig();
                sender.sendMessage("§aАд теперь открыт для телепортации!");
                return true;
            } else if (args[0].equalsIgnoreCase("close")) {
                isNetherOpen = false;
                this.getConfig().set("nether-open", false);
                this.saveConfig();
                sender.sendMessage("§cАд теперь закрыт для телепортации!");
                teleportPlayersFromNether();
                return true;
            } else {
                sender.sendMessage("§cИспользуйте: /nether <open|close>");
                return true;
            }
        }
        return false;
    }

    private void teleportPlayersFromNether() {
        World overworld = Bukkit.getWorlds().get(0);
        Location spawnLocation = overworld.getSpawnLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
                Location bedSpawn = player.getBedSpawnLocation();
                if (bedSpawn != null && bedSpawn.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    player.teleport(bedSpawn);
                    player.sendMessage("§aВас телепортировали к вашей кровати, так как ад закрыт!");
                } else {
                    player.teleport(spawnLocation);
                    player.sendMessage("§aВас телепортировали на мировой спавн, так как ад закрыт!");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getTo() != null && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
            if (!isNetherOpen) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cТелепортация в ад запрещена!");
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            World overworld = Bukkit.getWorlds().get(0);
            Location spawnLocation = overworld.getSpawnLocation();
            Location bedSpawn = player.getBedSpawnLocation();
            if (bedSpawn != null && bedSpawn.getWorld().getEnvironment() == World.Environment.NORMAL) {
                event.setRespawnLocation(bedSpawn);
                player.sendMessage("§aВас телепортировали к вашей кровати, так как вы находились в аду!");
            } else {
                event.setRespawnLocation(spawnLocation);
                player.sendMessage("§aВас телепортировали на мировой спавн, так как вы находились в аду!");
            }
        }
    }
}