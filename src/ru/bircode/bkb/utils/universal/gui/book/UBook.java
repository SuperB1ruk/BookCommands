package ru.bircode.bkb.utils.universal.gui.book;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import ru.bircode.bkb.utils.universal.UUtils;

public class UBook {
    
    private final List<UBPage> PAGES = new ArrayList<>();
    private final List<Player> OPENED = new ArrayList<>();
    
    public UBPage addPage(){
        UBPage p = new UBPage();
        PAGES.add(p);
        return p;
    }
    
    public void open(Player target){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) book.getItemMeta();
        meta.setTitle("");
        meta.setAuthor("");
        UBUtil.setPages(meta, buildPages());
        book.setItemMeta(meta);
        UBUtil.openBook(book, target);
        OPENED.add(target);
    }
    
    public void close(Player target){
        if(OPENED.contains(target)) target.closeInventory();
    }
    
    private List<String> buildPages(){
        List<String> pges = new ArrayList<>();
        for(UBPage page : PAGES){
            pges.add(ComponentSerializer.toString(page.getPageContent()));
        }
        return pges;
    }
    
    public UBPage getPage(int page){
        return PAGES.get(page);
    }
    
    public List<UBPage> getPages(){
        return PAGES;
    }
    
    public static class UBPage {
    
        private final TextComponent PAGE_CONTENT = new TextComponent("");

        public TextComponent getPageContent(){
            return PAGE_CONTENT;
        }

        public UBPage addText(String text){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

        public UBPage addHoverText(String text, String hover){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            element.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(UUtils.replaceColors(hover+"&r")).create()));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

        public UBPage addURLButton(String text, String url){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            element.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

        public UBPage addHoverURLButton(String text, String hover, String url){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            element.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(UUtils.replaceColors(hover+"&r")).create()));
            element.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

        public UBPage addCommandButton(String text, String command){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            element.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

        public UBPage addHoverCommandButton(String text, String hover, String command){
            TextComponent element = new TextComponent("");
            element.addExtra(UUtils.replaceColors(text+"&r"));
            element.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(UUtils.replaceColors(hover+"&r")).create()));
            element.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            PAGE_CONTENT.addExtra(element);
            return this;
        }

    }
    
}
