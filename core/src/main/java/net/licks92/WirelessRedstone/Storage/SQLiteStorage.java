package net.licks92.WirelessRedstone.Storage;

import net.licks92.WirelessRedstone.Signs.WirelessChannel;
import net.licks92.WirelessRedstone.Signs.WirelessPoint;
import net.licks92.WirelessRedstone.Signs.WirelessReceiver;
import net.licks92.WirelessRedstone.Signs.WirelessReceiverSwitch;
import net.licks92.WirelessRedstone.WirelessRedstone;

import java.io.File;
import java.util.Collection;
import java.util.Objects;

public class SQLiteStorage extends StorageConfiguration {

    private File channelFolder;

    public SQLiteStorage(String channelFolder) {
        this.channelFolder = new File(WirelessRedstone.getInstance().getDataFolder(), channelFolder);
    }

    @Override
    public boolean initStorage() {
        try {
            DatabaseClient.getInstance(channelFolder.toString());
            WirelessRedstone.getStorageManager().updateChannels(false);

            StorageType oldStorageType = canConvertFromType();
            if (oldStorageType != null) {
                return WirelessRedstone.getStorageManager().moveStorageFromType(oldStorageType);
            }

            return true;
        } catch (RuntimeException e) {
            WirelessRedstone.getWRLogger().severe("There was an error accessing the database!");
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean close() {
        DatabaseClient.getInstance().getDatabase().close();
        return true;
    }

    @Override
    protected Collection<WirelessChannel> getAllChannels() {
        return DatabaseClient.getInstance().getAllChannels();
    }

    @Override
    public boolean createChannel(WirelessChannel channel) {
        channel.getSigns()
                .forEach(wirelessPoint -> DatabaseClient.getInstance().insertWirelessPoint(channel, wirelessPoint));

        return super.createChannel(channel);
    }

    @Override
    public boolean createWirelessPoint(String channelName, WirelessPoint wirelessPoint) {
        WirelessChannel channel = WirelessRedstone.getStorageManager().getChannel(channelName);
        channel.addWirelessPoint(wirelessPoint);

        DatabaseClient.getInstance().insertWirelessPoint(channel, wirelessPoint);

        return super.createWirelessPoint(channelName, wirelessPoint);
    }

    @Override
    public boolean removeWirelessPoint(String channelName, WirelessPoint wirelessPoint) {
        WirelessChannel channel = WirelessRedstone.getStorageManager().getChannel(channelName);
        channel.removeWirelessPoint(wirelessPoint);

        return super.removeWirelessPoint(channelName, wirelessPoint);
    }

    @Override
    public boolean updateChannel(String channelName, WirelessChannel channel) {
        return super.updateChannel(channelName, channel);
    }

    @Override
    public boolean removeChannel(String channelName, boolean removeSigns) {
        return super.removeChannel(channelName, removeSigns);
    }

    @Override
    public boolean wipeData() {
        DatabaseClient.getInstance().recreateDatabase();

        return super.wipeData();
    }

    @Override
    protected StorageType canConvertFromType() {
        for (File file : Objects.requireNonNull(channelFolder.listFiles())) {
            if (file.getName().contains(".yml")) {
                return StorageType.YAML;
            }
        }

        return null;
    }

    @Override
    public void updateSwitchState(WirelessChannel channel) {
        for (WirelessReceiver receiver : channel.getReceivers()) {
            if (receiver instanceof WirelessReceiverSwitch) {
                DatabaseClient.getInstance().updateSwitch((WirelessReceiverSwitch) receiver);
            }
        }
    }
}