package ru.bircode.bkb;

import com.google.gson.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.bircode.BConnector;
import ru.bircode.VersionStatus;
import ru.bircode.bkb.utils.universal.UConfiguration;
import ru.bircode.bkb.utils.universal.UUtils;
import ru.bircode.bkb.variable.VariableScanner;

public class BCommands extends JavaPlugin implements Listener {

    private static BConnector bConnector;
    private static VersionStatus VRS_ST = VersionStatus.UNKNOWN;
    private static final String VERSION = "0.1";
    public final List<Book> BOOKS = new ArrayList<>();
    private Book OPEN_ON_JOIN;
    private long OPEN_ON_JOIN_DELAY;
    public static BCommands instance;
    
    @Override
    public void onEnable(){
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        UConfiguration.initialize(this);
        UConfiguration.writeResource("config.yml", "config.yml");
        if(UConfiguration.getBoolean("config.yml", "checkUpdates")){
            getLogger().info("Connecting to bircode.ru...");
            bConnector = new BConnector("BookCommands", VERSION);
            JsonObject res = bConnector.doRequest("getData", new JsonObject());
            if(res.has("versionInformation")){
                String vrsst = res.get("versionInformation").getAsString();
                try {
                    VRS_ST = VersionStatus.valueOf(vrsst);
                } catch (Exception exc) {
                    VRS_ST = VersionStatus.UNKNOWN;
                }
            }
            if(res.has("alertMessage")){
                getLogger().info("############################################");
                getLogger().info(" ");
                getLogger().info("ALERT MESSAGE: "+res.get("alertMessage").getAsString());
                getLogger().info(" ");
                getLogger().info("############################################");
            }
        }
        boolean ooje = UConfiguration.getBoolean("config.yml", "openOnJoinEnabled");
        VariableScanner.initialize("${", "}");
        loadBooks();
        if(ooje){
            String bk = UConfiguration.getString("config.yml", "openOnJoin");
            for(Book book : BOOKS){
                if(bk.equals(book.BOOK_NAME)){
                    OPEN_ON_JOIN = book;
                    break;
                }
            }
            if(OPEN_ON_JOIN == null){
                getLogger().info("Invalid onJoin book \""+bk+"\"");
            }else{
                OPEN_ON_JOIN_DELAY = UConfiguration.getLong("config.yml", "openOnJoinDelay");
            }
        }
        getLogger().info("Checking version...");
        getLogger().info(checkVersion());
    }
    
    public void loadBooks(){
        BOOKS.clear();
        File bookFolder = new File(this.getDataFolder(), "books");
        if (!bookFolder.exists()) {
            bookFolder.mkdirs();
            UConfiguration.writeResource("books/examplebook.yml", "books/examplebook.yml");
            UConfiguration.writeResource("books/examplebook2.yml", "books/examplebook2.yml");
        }
        for (File f : bookFolder.listFiles()) {
            if(!f.isFile()) continue;
            getLogger().info("Loading book "+f.getName());
            List<List<String>> pages = new ArrayList<>();
            FileConfiguration ccc = UConfiguration.getConfiguration(f);
            for(String page : ccc.getConfigurationSection("pages").getValues(false).keySet()){
                getLogger().info("["+f.getName().replace(".yml", "")+"] Loading page "+page);
                List<String> lines = ccc.getStringList("pages."+page);
                pages.add(lines);
            }
            Book book = new Book(f.getName().replace(".yml", ""), pages);
            BOOKS.add(book);
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(OPEN_ON_JOIN == null) return;
        new BukkitRunnable(){
            @Override
            public void run(){
                OPEN_ON_JOIN.open(e.getPlayer());
            }
        }.runTaskLater(this, OPEN_ON_JOIN_DELAY);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(cmd.getName().equals("bookcommands") || cmd.getName().equals("bkco")){
            if(args.length < 1){
                sender.sendMessage(UUtils.replaceColors("&e * * * * * * &6BookCommands v"+VERSION));
                sender.sendMessage(UUtils.replaceColors(" &eРазработчик плагина: &6B1ruk ( Антон Бирюков, за помощью обращаться сюда: http://vk.com/mcbiruk )"));
                String versionInfo = null;
                if(VRS_ST == VersionStatus.OLD || VRS_ST == VersionStatus.OLD_BLOCKED){
                    versionInfo = "&c"+VERSION+" (старая, скачайте обновление)"/*, обновите с помощью &4/"+cmd.getName()+" update&c)"*/;
                }else if(VRS_ST == VersionStatus.LATEST || VRS_ST == VersionStatus.LATEST_BLOCKED){
                    versionInfo = "&a"+VERSION+" (последняя)";
                }else if(VRS_ST == VersionStatus.UNKNOWN){
                    versionInfo = "&c"+VERSION+" &4&l(ошибка проверки версии)";
                }
                sender.sendMessage(UUtils.replaceColors(" &eВерсия плагина: "+versionInfo+""));
                if(sender.hasPermission("bookcommands.showcommands")){ 
                    sender.sendMessage(UUtils.replaceColors("&e * * * * * * * * &6Команды"));
                    sender.sendMessage(UUtils.replaceColors(" &e/"+cmd.getName()+" open [КНИГА] &6- открыть меню книги (необходимо право bookcommands.open.[НАЗВАНИЕ ФАЙЛА КНИГИ БЕЗ .yml]"));
                    if(sender.hasPermission("bookcommands.admin")) sender.sendMessage(UUtils.replaceColors(" &e/"+cmd.getName()+" reload &6- перезагрузить конфигурацию и книги"));
                    //if(sender.hasPermission("bookcommands.admin")) sender.sendMessage(" &e/"+cmd.getName()+" update &6- обновить плагин (будет произведена перезагрузка плагина)");
                }
                sender.sendMessage(UUtils.replaceColors("&e * * * * * * * * *"));
            }else{
                if(args[0].equals("open")){
                    if(!(sender instanceof Player)){
                        sender.sendMessage(UUtils.replaceColors("&cКоманду может выполнить только игрок!\n&cThe command can be executed only from player!"));
                        return false;
                    }
                    if(args.length < 2){
                        sender.sendMessage(UUtils.replaceColors("&e/"+cmd.getName()+" open [КНИГА]"));
                    }else{
                        boolean o = false;
                        for(Book book : BOOKS){
                            if(book.BOOK_NAME.equals(args[1])){
                                if(sender.hasPermission(book.getPermission())){
                                    book.open((Player)sender);
                                    o = true;
                                    break;
                                }else{
                                    sender.sendMessage(UUtils.replaceColors("&cУ вас нет доступа к этой книге."));
                                }
                            }
                        }
                        if(!o){
                            sender.sendMessage(UUtils.replaceColors("&cТакой книги не существует."));
                        }
                    }
                }
                if(args[0].equals("reload")){
                    if(sender.hasPermission("bookcommands.admin")){
                        loadBooks();
                        OPEN_ON_JOIN = null;
                        boolean ooje = UConfiguration.getBoolean("config.yml", "openOnJoinEnabled");
                        if(ooje){
                            String bk = UConfiguration.getString("config.yml", "openOnJoin");
                            for(Book book : BOOKS){
                                if(bk.equals(book.BOOK_NAME)){
                                    OPEN_ON_JOIN = book;
                                    break;
                                }
                            }
                            if(OPEN_ON_JOIN == null){
                                getLogger().info("Invalid onJoin book \""+bk+"\"");
                            }else{
                                OPEN_ON_JOIN_DELAY = UConfiguration.getLong("config.yml", "openOnJoinDelay");
                            }
                        }
                        sender.sendMessage(UUtils.replaceColors("&aКонфигурация и книги перезагружены."));
                    }else{
                        sender.sendMessage(UUtils.replaceColors("&cУ вас нет доступа к этой команде."));
                    }
                }
            }
        }
        return true;
    }
    
    private static String checkVersion(){
        String text = null;
        if(VRS_ST == VersionStatus.OLD || VRS_ST == VersionStatus.OLD_BLOCKED){
            text = "You have old version of BookCommands. Update it."/* manually or enter command /bkco update"*/;
        }else if(VRS_ST == VersionStatus.LATEST || VRS_ST == VersionStatus.LATEST_BLOCKED){
            text = "You have latest version of BookCommands.";
        }else if(VRS_ST == VersionStatus.UNKNOWN){
            text = "Version error.";
        }
        return text;
    }
    
}
