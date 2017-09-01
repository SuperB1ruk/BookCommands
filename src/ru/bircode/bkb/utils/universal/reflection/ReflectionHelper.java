package ru.bircode.bkb.utils.universal.reflection;

import org.bukkit.Bukkit;
import java.lang.reflect.Field;

public class ReflectionHelper {
    
    public static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    public static String getSimplePackage(Class<?> clss) {
        return clss.getName().split(ReflectionHelper.getVersion() + "\\.")[1];
    }

    public static Class<?> getCraftBukkitClass(String name) {
        return ReflectionHelper.findClass("org.bukkit.craftbukkit." + ReflectionHelper.getVersion() + "." + name);
    }

    public static Class<?> getNMSClass(String name) {
        return ReflectionHelper.findClass("net.minecraft.server." + ReflectionHelper.getVersion() + "." + name);
    }

    private static Class<?> findClass(String name) {
        Class ret = null;
        try {
            ret = Class.forName(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static Object getField(Object instance, String name) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.getName().equals(name)) {
                field.setAccessible(true);
                try {
                    return field.get(instance);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}

