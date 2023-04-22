package com.gmail.justbru00.epic.rename.spikeyutils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String formatColors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


    public static String centerText(String string) {
        StringBuilder tmp = new StringBuilder();

        int num = 27 - ChatColor.stripColor(string).length();

        for (int x = 0; x < num; x++) {
            tmp.append(" ");
        }

        return tmp.toString() + string;
    }
}
