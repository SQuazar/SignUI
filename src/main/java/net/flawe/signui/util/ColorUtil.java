package net.flawe.signui.util;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String[] format(String[] arr) {
        for (int i = 0; i < arr.length; i++)
            arr[i] = format(arr[i]);
        return arr;
    }
}
