package net.flawe.signui.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import net.flawe.signui.SignUI;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class SignHelper {

    private final Player player;
    private final Sign sign;
    private final ProtocolManager protocolManager;

    public SignHelper(Player player, Sign sign) {
        this.player = player;
        this.sign = sign;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void openSign() {
        setEditable(true);
        setSignEditor();
        BlockPosition position = new BlockPosition(sign.getX(), sign.getY(), sign.getZ());
        PacketContainer container = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        container.getBlockPositionModifier().write(0, position);
        try {
            protocolManager.sendServerPacket(player, container);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private Object getSignTileEntity() {
        try {
            World bukkitWorld = player.getWorld();
            Object world = bukkitWorld.getClass().getDeclaredMethod("getHandle").invoke(bukkitWorld);
            Class<?> blockPositionClass = Class.forName("net.minecraft.server." + SignUI.getServerVersion() +".BlockPosition");
            Object blockPosition = blockPositionClass
                    .getConstructor(int.class, int.class, int.class).newInstance(sign.getX(), sign.getY(), sign.getZ());
            return world.getClass().getMethod("getTileEntity", blockPositionClass).invoke(world, blockPosition);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException | InstantiationException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setSignEditor() {
        Object signTile = getSignTileEntity();
        try {
            Object entityPlayer = player.getClass().getDeclaredMethod("getHandle").invoke(player);
            Field field = getEntityHumanField();
            if (!field.isAccessible())
                field.setAccessible(true);
            field.set(signTile, entityPlayer);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Field getEntityHumanField() {
        for (Field field : getSignTileEntity().getClass().getDeclaredFields()) {
            try {
                if (field.getType().equals(Class.forName("net.minecraft.server." + SignUI.getServerVersion() + ".EntityHuman")))
                    return field;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public UUID getSignEditor() {
        Object signTile = getSignTileEntity();
        try {
            Field field = getEntityHumanField();
            if (!field.isAccessible())
                field.setAccessible(true);
            Object entityPlayer = field.get(signTile);
            return (UUID) entityPlayer.getClass().getMethod("getUniqueID").invoke(entityPlayer);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setEditable(boolean b) {
        Object signTile = getSignTileEntity();
        try {
            signTile.getClass().getField("isEditable").set(signTile, b);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public boolean signHasEditor() {
        Object signTile = getSignTileEntity();
        try {
            Field field = getEntityHumanField();
            if (!field.isAccessible())
                field.setAccessible(true);
            return field.get(signTile) != null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
