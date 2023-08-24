package me.devkevin.landcore.faction.commands.captain;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.faction.Faction;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 14:04
 * FactionPromoteCommand / me.devkevin.landcore.faction.commands.captain / LandCore
 */
public class FactionPromoteCommand extends PlayerCommand {
    private final LandCore plugin;

    public FactionPromoteCommand(LandCore plugin) {
        super("faction.promote");
        this.plugin = plugin;
        setAliases("f.promote");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(CC.RED + "Usage: /faction promote <player>");
            return;
        }

        FactionProfile factionProfile = this.plugin.getFactionManager().getProfile(player);
        if (factionProfile.getFaction() == null) {
            player.sendMessage(CC.RED + "You are not in a faction!");
            return;
        }

        Faction faction = factionProfile.getFaction();

        if (!faction.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You are not the faction leader!");
            return;
        }

        OfflinePlayer offlinePlayer = this.plugin.getServer().getOfflinePlayer(args[0]);

        if (offlinePlayer.getPlayer() != null && offlinePlayer.getPlayer() == player) {
            player.sendMessage(CC.RED + "You may not promote yourself!");
            return;
        }

        if (!faction.getMembers().contains(offlinePlayer.getUniqueId())) {
            player.sendMessage(player.getName() + CC.RED + " is not in your faction!");
            return;
        }

        if (faction.getCaptains().contains(offlinePlayer.getUniqueId())) {
            player.sendMessage(player.getName() + CC.RED + " is already a faction captain!");
            return;
        }

        faction.getCaptains().add(offlinePlayer.getUniqueId());
        faction.broadcast(player.getName() + CC.YELLOW + " has been promoted to faction captain!");
    }
}
