package org.kxysl1k.netherControl;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class NetherControl extends JavaPlugin implements Listener {

    private boolean isNetherOpen;
    private boolean isEndOpen;
    private FileConfiguration langConfig;
    private final Map<String, String> messages = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        isNetherOpen = getConfig().getBoolean("nether-open", false);
        isEndOpen = getConfig().getBoolean("end-open", false);

        loadLanguage(getConfig().getString("language", "en"));

        getLogger().info(getMessage("plugin-enabled")
                .replace("%nether%", isNetherOpen ? getMessage("state-open") : getMessage("state-closed"))
                .replace("%end%", isEndOpen ? getMessage("state-open") : getMessage("state-closed")));

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("worldcontrol").setExecutor(this);
    }

    @Override
    public void onDisable() {
        getLogger().info(getMessage("plugin-disabled"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("worldcontrol.admin")) {
            sender.sendMessage(getMessage("no-permission"));
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(getMessage("command-usage"));
            return true;
        }

        String world = args[0].toLowerCase();
        String action = args[1].toLowerCase();

        if (world.equals("nether")) {
            if (action.equals("open")) {
                isNetherOpen = true;
                getConfig().set("nether-open", true);
                saveConfig();
                sender.sendMessage(getMessage("nether-opened"));
            } else if (action.equals("close")) {
                isNetherOpen = false;
                getConfig().set("nether-open", false);
                saveConfig();
                sender.sendMessage(getMessage("nether-closed"));
                teleportPlayers(World.Environment.NETHER);
            } else {
                sender.sendMessage(getMessage("command-usage"));
            }
            return true;
        } else if (world.equals("end")) {
            if (action.equals("open")) {
                isEndOpen = true;
                getConfig().set("end-open", true);
                saveConfig();
                sender.sendMessage(getMessage("end-opened"));
            } else if (action.equals("close")) {
                isEndOpen = false;
                getConfig().set("end-open", false);
                saveConfig();
                sender.sendMessage(getMessage("end-closed"));
                teleportPlayers(World.Environment.THE_END);
            } else {
                sender.sendMessage(getMessage("command-usage"));
            }
            return true;
        } else {
            sender.sendMessage(getMessage("command-usage"));
            return true;
        }
    }

    private void teleportPlayers(World.Environment env) {
        World overworld = Bukkit.getWorlds().get(0);
        Location spawn = overworld.getSpawnLocation();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getEnvironment() == env) {
                Location bed = player.getBedSpawnLocation();
                if (bed != null && bed.getWorld().getEnvironment() == World.Environment.NORMAL) {
                    player.teleport(bed);
                    player.sendMessage(getMessage("teleport-bed"));
                } else {
                    player.teleport(spawn);
                    player.sendMessage(getMessage("teleport-spawn"));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        World.Environment env = to.getWorld().getEnvironment();
        if ((env == World.Environment.NETHER && !isNetherOpen) ||
                (env == World.Environment.THE_END && !isEndOpen)) {

            event.setCancelled(true);
            Block portalBlock = event.getFrom().getBlock();
            if (portalBlock.getType() == Material.NETHER_PORTAL || portalBlock.getType() == Material.END_PORTAL) {
                portalBlock.setType(Material.AIR);
            }
            event.getPlayer().sendMessage(getMessage("portal-blocked"));
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        World.Environment env = player.getWorld().getEnvironment();
        if ((env == World.Environment.NETHER && !isNetherOpen) || (env == World.Environment.THE_END && !isEndOpen)) {
            World overworld = Bukkit.getWorlds().get(0);
            Location spawn = overworld.getSpawnLocation();
            Location bed = player.getBedSpawnLocation();

            if (bed != null && bed.getWorld().getEnvironment() == World.Environment.NORMAL) {
                event.setRespawnLocation(bed);
                player.sendMessage(getMessage("teleport-bed-respawn"));
            } else {
                event.setRespawnLocation(spawn);
                player.sendMessage(getMessage("teleport-spawn-respawn"));
            }
        }
    }

    private void loadLanguage(String lang) {
        File langFile = new File(getDataFolder(), "lang_" + lang + ".yml");
        if (!langFile.exists()) {
            saveResource("lang_" + lang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        try (Reader defConfigStream = new InputStreamReader(getResource("lang_" + lang + ".yml"))) {
            FileConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            for (String key : defConfig.getKeys(true)) {
                messages.put(key, langConfig.getString(key, defConfig.getString(key)));
            }
        } catch (Exception e) {
            getLogger().warning("Failed to load language file: " + lang);
        }
    }

    private String getMessage(String key) {
        return messages.getOrDefault(key, "Missing lang key: " + key);
    }
}
