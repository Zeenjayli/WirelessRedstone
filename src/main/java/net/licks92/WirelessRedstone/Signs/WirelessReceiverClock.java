package net.licks92.WirelessRedstone.Signs;

import net.licks92.WirelessRedstone.WirelessRedstone;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

@SerializableAs("WirelessReceiverClock")
public class WirelessReceiverClock extends WirelessReceiver {

    private int delay;
    private BukkitTask bukkitTask;

    public WirelessReceiverClock(int x, int y, int z, String world, boolean isWallSign, BlockFace direction, String owner, int delay) {
        super(x, y, z, world, isWallSign, direction, owner);
        this.delay = delay;
    }

    public WirelessReceiverClock(Map<String, Object> map) {
        super(map);
        delay = (Integer) map.get("delay");
    }

    @Override
    public void turnOn(String channelName) {
        int delayInTicks = delay / 50;

        bukkitTask = Bukkit.getScheduler().runTaskTimer(WirelessRedstone.getInstance(), new Runnable() {
            boolean state = false;

            @Override
            public void run() {
                state = !state;
                changeState(state, channelName);
            }
        }, 0, delayInTicks);
    }

    @Override
    public void turnOff(String channelName) {
        if (bukkitTask != null) {
            Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        }
        changeState(false, channelName);
    }

    @Override
    public void changeSignContent(Block block, String channelName) {
        Sign sign = (Sign) block.getState();
        sign.setLine(0, WirelessRedstone.getStringManager().tagsReceiver.get(0));
        sign.setLine(1, channelName);
        sign.setLine(2, WirelessRedstone.getStringManager().tagsReceiverClockType.get(0));
        sign.setLine(3, Integer.toString(delay));
        sign.update(true);
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        map.put("delay", getDelay());
        return map;
    }

}