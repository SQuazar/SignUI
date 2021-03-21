package net.flawe.signui;

import net.flawe.signui.commands.SignCommand;
import net.flawe.signui.util.SignHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static net.flawe.signui.util.ColorUtil.format;

public final class SignUI extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            err("Required plugin (ProtocolLib) isn't found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        getCommand("sign").setExecutor(new SignCommand(this));
        Bukkit.getPluginManager().registerEvents(this, this);
        info("Enabled!");
    }

    @Override
    public void onDisable() {
        info("Disabled!");
    }

    public static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (e.getClickedBlock() == null)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign))
            return;
        Player player = e.getPlayer();
        if (player.isSneaking())
            return;
        if (getConfig().getBoolean("ui-permission")) {
            if (!player.hasPermission("signui.ui")) {
                player.sendMessage(format("&7[&6SignUI&7] &fYou don't have permissions for do this!"));
                return;
            }
        }
        Sign sign = (Sign) e.getClickedBlock().getState();
        if (!player.getWorld().equals(sign.getWorld()))
            return;
        if (player.getLocation().distance(sign.getLocation()) > 20)
            return;
        SignHelper helper = new SignHelper(player, sign);
        if (helper.signHasEditor() && !helper.getSignEditor().equals(player.getUniqueId())) {
            Player target = Bukkit.getPlayer(helper.getSignEditor());
            if (target != null)
                if (target.getWorld().equals(sign.getWorld()))
                    if (target.getLocation().distance(sign.getLocation()) <= 20 && !target.isDead()) {
                        player.sendMessage(format(getConfig().getString("already-edited").replace("%player%", target.getName())));
                        return;
                    }
        }
        helper.openSign();
    }

    @EventHandler
    public void onChange(SignChangeEvent e) {
        Sign sign = (Sign) e.getBlock().getState();
        SignHelper helper = new SignHelper(e.getPlayer(), sign);
        helper.setSignEditor();
        if (e.getPlayer().getLocation().distance(sign.getLocation()) > 20)
            e.setCancelled(true);
    }

    public void info(String s) {
        getLogger().info(s);
    }

    public void warn(String s) {
        getLogger().warning(s);
    }

    public void err(String s) {
        getLogger().severe(s);
    }
}
