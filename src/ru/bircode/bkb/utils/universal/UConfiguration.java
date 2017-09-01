package ru.bircode.bkb.utils.universal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class UConfiguration {
    
    private static Plugin MAIN_PLUGIN;
    private static final Map<String, Map.Entry<FileConfiguration, File>> DATA = new HashMap<>();
    private static boolean isInitialized = false;
    
    public static void initialize(Plugin plugin){
        if(isInitialized) return;
        MAIN_PLUGIN = plugin;
        isInitialized = true;
    }

    public static void saveConfiguration(FileConfiguration file, String fileName) {
        fileName = replaceDirectory(fileName);
        try {
            File fileSignature = new File(fileName);
            fileSignature.setWritable(true);
            if(!fileSignature.exists()){
                fileSignature.createNewFile();
                String content = FileUtils.readFileToString(fileSignature, "ISO8859_1");
                FileUtils.write(fileSignature, content, "UTF-8");
            }
            file.save(fileSignature);
        } catch (IOException ex) {
            Logger.getLogger(UConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static FileConfiguration getConfiguration(File file) {
        YamlConfiguration fc = new YamlConfiguration();
        try {
            file.setWritable(true);
            if(!file.exists()){
                file.createNewFile();
                String content = FileUtils.readFileToString(file, "ISO8859_1");
                FileUtils.write(file, content, "UTF-8");
            }
            fc.load(file);
            if (!DATA.containsKey(file.getName())) {
                DATA.put(file.getName(), new AbstractMap.SimpleEntry<>(fc, file));
            }
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(UConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fc;
    }

    public static Map<String, Map.Entry<FileConfiguration, File>> getConfigs() {
        return DATA;
    }

    public static void saveConfiguration(String name) {
        try {
            if (getConfigs().get(name) == null) {
                throw new Exception("Config \"" + name + "\" not registered!");
            }
            saveConfiguration(getConfigs().get(name).getKey(), name);
        } catch (Exception ex) {
            Logger.getLogger(UConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static FileConfiguration getConfiguration(String fileName) {
        File file = new File(replaceDirectory(fileName));
        try {
            return getConfiguration(file);
        } catch (Exception e) {
            Logger.getLogger(UConfiguration.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public static void writeResource(String resourceFileName, String destination) {
        destination = destination != null ? replaceDirectory(destination) : replaceDirectory(resourceFileName);
        File fileDes = new File(destination);
        fileDes.setWritable(true);
        if (!fileDes.exists()) {
            fileDes.getParentFile().mkdirs();
            copyFile(MAIN_PLUGIN.getResource("resources/" + resourceFileName), fileDes);
        }
    }

    private static void copyFile(InputStream in, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            Throwable throwable = null;
            try {
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            catch (Throwable buf) {
                throwable = buf;
                throw buf;
            } finally {
                if (out != null) {
                    if (throwable != null) {
                        try {
                            out.close();
                        } catch (Throwable buf) {
                            throwable.addSuppressed(buf);
                        }
                    } else {
                        out.close();
                    }
                }
            }
            in.close();
        } catch (Exception out) { }
    }

    public static Boolean isExists(String fileName) {
        fileName = replaceDirectory(fileName);
        return new File(fileName).exists();
    }

    private static String replaceDirectory(String fileName) {
        return MAIN_PLUGIN.getDataFolder() + File.separator + fileName;
    }

    public static String getString(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getString(path, null);
    }

    public static int getInt(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getInt(path);
    }

    public static long getLong(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getLong(path);
    }

    public static boolean getBoolean(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getBoolean(path, false);
    }

    public static ConfigurationSection getConfigurationSection(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getConfigurationSection(path);
    }

    public static List<String> getStringList(String fileConfigurationName, String path) {
        return getConfiguration(fileConfigurationName).getStringList(path);
    }

    public static void setValue(String fileConfigurationName, String path, Object value) {
        FileConfiguration fc = getConfiguration(fileConfigurationName);
        fc.set(path, value);
        saveConfiguration(fc, fileConfigurationName);
    }
    
    public static class ConfigUtils {

        private static double _double(String _double){
            return Double.valueOf(_double);
        }

        private static float _float(String _float){
            return Float.valueOf(_float);
        }

        public static Sound getSoundFromConfig(String config, String path, Sound defaultValue){
            try {
                return Sound.valueOf(UConfiguration.getString(config, path));
            } catch (Exception exception) {
                if(defaultValue != null) return defaultValue;
                return null;
            }
        }

        public static Location getLocationFromConfig(String config, String path){
            try {
                String[] locationString = UConfiguration.getString(config, path).split(",");
                World world = Bukkit.getWorld(locationString[0]);
                double x = _double(locationString[1]);
                double y = _double(locationString[2]);
                double z = _double(locationString[3]);
                float yaw = _float(locationString[4]);
                float pitch = _float(locationString[5]);
                return new Location(world, x, y, z, yaw, pitch);
            } catch (Exception exception) {
                return null;
            }
        }

        public static void writeSoundToConfig(String config, String path, Sound sound){
            UConfiguration.setValue(config, path, sound.name());
        }

        public static void writeLocationToConfig(String config, String path, Location location){
            String x = String.valueOf(location.getX());
            String y = String.valueOf(location.getY());
            String z = String.valueOf(location.getZ());
            String yaw = String.valueOf(location.getYaw());
            String pitch = String.valueOf(location.getPitch());
            String world = location.getWorld().getName();
            String toConfig = world+","+x+","+y+","+z+","+yaw+","+pitch;
            UConfiguration.setValue(config, path, toConfig);
        }

    }
    
}

