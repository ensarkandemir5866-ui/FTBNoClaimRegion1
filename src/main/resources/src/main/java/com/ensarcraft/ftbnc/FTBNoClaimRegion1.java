package com.ensarcraft.ftbnc;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.managers.RegionContainer;
import com.sk89q.worldguard.protection.managers.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FTBNoClaimRegion1 extends JavaPlugin implements Listener {

    public static StateFlag FTB_NO_CLAIM_FLAG;

    @Override
    public void onEnable() {
        try {
            FTB_NO_CLAIM_FLAG = new StateFlag("ftb-no-claim", false);
            WorldGuard.getInstance().getFlagRegistry().register(FTB_NO_CLAIM_FLAG);
        } catch (FlagConflictException e) {
            // başka bir plugin aynı ismi kaydetmişse, var olanı kullanmayı dene
            FTB_NO_CLAIM_FLAG = (StateFlag) WorldGuard.getInstance()
                    .getFlagRegistry()
                    .get("ftb-no-claim");
        }
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("FTBNoClaimRegion1 enabled!");
    }

    private boolean isInNoClaimRegion(Player p) {
        if (FTB_NO_CLAIM_FLAG == null) return false;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(p.getLocation()));
        return set.testState(WorldGuardPlugin.inst().wrapPlayer(p), FTB_NO_CLAIM_FLAG);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String msg = e.getMessage().toLowerCase();
        // hem /chunks hem /ftbchunks ile başlayan tüm komutları yakala
        if (msg.startsWith("/chunks") || msg.startsWith("/ftbchunks")) {
            if (isInNoClaimRegion(e.getPlayer())) {
                e.getPlayer().sendMessage("§cBu bölgede claim yapamazsın!");
                e.setCancelled(true);
            }
        }
    }
}
