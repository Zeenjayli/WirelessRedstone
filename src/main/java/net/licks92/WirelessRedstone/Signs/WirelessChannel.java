package net.licks92.WirelessRedstone.Signs;

import net.licks92.WirelessRedstone.ConfigManager;
import net.licks92.WirelessRedstone.WirelessRedstone;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SerializableAs("WirelessChannel")
public class WirelessChannel implements ConfigurationSerializable {

    private int id;
    private String name;
    private boolean active;
    private boolean locked;

    private List<String> owners = new LinkedList<String>();
    private List<WirelessTransmitter> transmitters = new LinkedList<>();
    private List<WirelessReceiver> receivers = new LinkedList<WirelessReceiver>();
    private List<WirelessScreen> screens = new LinkedList<>();

    public WirelessChannel(String name) {
        this.setName(name);
    }

    public WirelessChannel(Map<String, Object> map) {
        this.setId((Integer) map.get("id"));
        this.setName((String) map.get("name"));
        this.setOwners((List<String>) map.get("owners"));
        this.setReceivers((List<WirelessReceiver>) map.get("receivers"));
        this.setTransmitters((List<WirelessTransmitter>) map.get("transmitters"));
        this.setScreens((List<WirelessScreen>) map.get("screens"));
        try {
            this.setLocked((Boolean) map.get("locked"));
        } catch (NullPointerException ignored) {
            this.setLocked(false);
        }
    }

    public void turnOn() {
        turnOn(0);
    }

    public void turnOn(int time) {
        if (isLocked()) {
            WirelessRedstone.getWRLogger().debug("Channel " + name + " didn't turn on because locked.");
            return;
        }

        active = true;

        for (WirelessReceiver receiver : getReceivers()) {
            receiver.turnOn(name);
        }

        for (WirelessScreen screen : getScreens()) {
            screen.turnOn();
        }

        if (time > 0) {
            Bukkit.getScheduler().runTaskLater(WirelessRedstone.getInstance(), new Runnable() {
                @Override
                public void run() {
                    turnOff(true);
                }
            }, time / 50);
        }
    }

    public void turnOff() {
        turnOff(false);
    }

    public void turnOff(boolean force) {
        if (isLocked()) {
            WirelessRedstone.getWRLogger().debug("Channel " + name + " didn't turn off because locked.");
            return;
        }

        boolean canTurnOff = true;
        if (ConfigManager.getConfig().useORLogic() && !force) {
            for (WirelessTransmitter transmitter : getTransmitters()) {
                if (transmitter.isPowered())
                    canTurnOff = false;
            }
        }

        if (!canTurnOff) {
            return;
        }

        active = false;

        for (WirelessReceiver receiver : getReceivers()) {
            receiver.turnOff(name);
        }

        for (WirelessScreen screen : getScreens()) {
            screen.turnOff();
        }

    }

    public void addWirelessPoint(WirelessPoint wirelessPoint) {
        if (wirelessPoint instanceof WirelessTransmitter) {
            transmitters.add((WirelessTransmitter) wirelessPoint);
        } else if (wirelessPoint instanceof WirelessScreen) {
            screens.add((WirelessScreen) wirelessPoint);
        } else if (wirelessPoint instanceof WirelessReceiver) {
            receivers.add((WirelessReceiver) wirelessPoint);
        }

        //TODO: Maybe add owner from wirelesspoint to list of owners
    }

    public void removeWirelessPoint(WirelessPoint wirelessPoint) {
        if (wirelessPoint instanceof WirelessTransmitter) {
            transmitters.remove(wirelessPoint);
        } else if (wirelessPoint instanceof WirelessScreen) {
            screens.remove(wirelessPoint);
        } else if (wirelessPoint instanceof WirelessReceiver) {
            receivers.remove(wirelessPoint);
        }

        //TODO: Maybe remove owner from wirelesspoint to list of owners
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<String> getOwners() {
        return owners;
    }

    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

    public List<WirelessTransmitter> getTransmitters() {
        return transmitters;
    }

    public void setTransmitters(List<WirelessTransmitter> transmitters) {
        this.transmitters = transmitters;
    }

    public List<WirelessReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<WirelessReceiver> receivers) {
        this.receivers = receivers;
    }

    public List<WirelessScreen> getScreens() {
        return screens;
    }

    public void setScreens(List<WirelessScreen> screens) {
        this.screens = screens;
    }

    public boolean isActive() {
        return active;
    }

    public List<WirelessPoint> getSigns() {
        List<WirelessPoint> signs = new ArrayList<>();
        signs.addAll(getTransmitters());
        signs.addAll(getReceivers());
        signs.addAll(getScreens());
        return signs;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", getId());
        map.put("name", getName());
        map.put("owners", getOwners());
        map.put("receivers", getReceivers());
        map.put("transmitters", getTransmitters());
        map.put("screens", getScreens());
        map.put("locked", isLocked());
        return map;
    }

}
