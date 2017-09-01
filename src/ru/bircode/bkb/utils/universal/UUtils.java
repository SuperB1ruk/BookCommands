package ru.bircode.bkb.utils.universal;

import java.util.Random;
import org.bukkit.ChatColor;

public class UUtils {
    
    private static final Random random = new Random();
    
    public static String replaceColors(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    public static int randomInt(int range){
        return random.nextInt(range);
    }
    
}
