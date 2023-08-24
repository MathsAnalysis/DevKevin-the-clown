package me.devkevin.landcore.commands.impl.staff.punish;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.utils.Duration;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Objects;
import java.util.UUID;

public class MuteCommand extends BaseCommand {
    private final LandCore plugin;

    public MuteCommand(LandCore plugin) {
        super("mute", Rank.TRIAL_MOD);
        this.plugin = plugin;
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        CoreProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());


        if (args.length < 3) {
            sender.sendMessage(CC.RED +  "Usage: /mute <player> <duration> <reason> " + CC.GRAY + "[-s]" + CC.RED + ".");
        }

        if (profile == null) {
            sender.sendMessage(CC.RED + "Could no load " + target.getName() + " database. Contact with the developer.");
        }

        if (Objects.requireNonNull(profile).getActiveMute() != null) {
            sender.sendMessage(CC.RED + "That player is already muted.");
            return;
        }

        long duration = Duration.fromString(args[1]).getValue();
        boolean silent = args[args.length - 1].equalsIgnoreCase("-s");

        if (duration == -1) {
            sender.sendMessage(CC.RED + "That duration is not valid.");
            sender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
            return;
        }

        String staffName = sender instanceof Player ? plugin.getProfileManager().getProfile(((Player) sender).getUniqueId()).getGrant().getRank().getColor() + sender.getName() : CC.D_RED + "Console";

        Punishment punishment = new Punishment(target.getUniqueId(), PunishmentType.MUTE, System.currentTimeMillis(),
                args[1], duration);

        if (sender instanceof Player) {
            punishment.setAddedBy(((Player) sender).getUniqueId());
        }

        profile.getPunishments().add(punishment);
        profile.save(true);

        Player player = profile.getPlayer();


        if (player != null) {
            String senderName = sender instanceof Player ? plugin.getProfileManager().getProfiles().get(((Player) sender).getUniqueId()).getGrant().getRank().getColor() : CC.D_RED + "Console";
            player.sendMessage(CC.RED + "You have been muted by " + senderName + CC.RED + " for: " + CC.YELLOW + args[1]);

            if (!punishment.isPermanent()) {
                player.sendMessage(CC.RED + "This mute will expire in " + CC.YELLOW + punishment.getTimeRemaining());
            }
        }

        punishment.broadcast(staffName, target, silent);
    }

}
