package net.licks92.WirelessRedstone.Commands;

import net.licks92.WirelessRedstone.Signs.SignType;
import net.licks92.WirelessRedstone.Signs.WirelessChannel;
import net.licks92.WirelessRedstone.Utils;
import net.licks92.WirelessRedstone.WirelessRedstone;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(description = "Create a WirelessPoint", usage = "<channel> <signtype> [sign details]", aliases = {"create", "c"},
        permission = "create", canUseInConsole = false, canUseInCommandBlock = false)
public class Create extends WirelessCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            Utils.sendFeedback(WirelessRedstone.getStrings().commandTooFewArguments, sender, true);
            return;
        }

        if (Utils.getType(args[1]) == null) {
            Utils.sendFeedback(WirelessRedstone.getStrings().commandIncorrectSignType, sender, true);
            return;
        }

        String cname = args[0];
        SignType type = Utils.getType(args[1]);

        if (type == null) {
            Utils.sendFeedback(WirelessRedstone.getStrings().commandIncorrectSignType, sender, true);
            return;
        }

        if (type == SignType.RECEIVER_CLOCK || type == SignType.RECEIVER_DELAYER) {
            if (args.length < 3) {
                Utils.sendFeedback(WirelessRedstone.getStrings().commandTooFewArguments, sender, true);
                return;
            }

        }

        WirelessChannel channel = WirelessRedstone.getStorageManager().getChannel(cname);
        if (channel != null) {
            if (!hasAccessToChannel(sender, cname)) {
                Utils.sendFeedback(WirelessRedstone.getStrings().permissionChannelAccess, sender, true);
                return;
            }
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        if (location.getBlock().getType() != Material.AIR) {
            Utils.sendFeedback(WirelessRedstone.getStrings().commandInvalidSignLocation, sender, true);
            return;
        }

        int extraData = 0;

        if (type == SignType.RECEIVER_DELAYER) {
            try {
                extraData = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                Utils.sendFeedback(WirelessRedstone.getStrings().delayNumberOnly, sender, true);
                location.getBlock().setType(Material.AIR);
                return;
            }
        } else if (type == SignType.RECEIVER_CLOCK) {
            try {
                extraData = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                Utils.sendFeedback(WirelessRedstone.getStrings().intervalNumberOnly, sender, true);
                location.getBlock().setType(Material.AIR);
                return;
            }
        }

        WirelessRedstone.getSignManager().placeSign(cname, location, type, extraData);
        //TODO: Register sign to channel
    }
}