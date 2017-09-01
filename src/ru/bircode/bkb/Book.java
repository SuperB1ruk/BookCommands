package ru.bircode.bkb;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import ru.bircode.bkb.utils.universal.gui.book.UBook;
import ru.bircode.bkb.utils.universal.gui.book.UBook.UBPage;
import ru.bircode.bkb.variable.InvalidVariableException;
import ru.bircode.bkb.variable.Variable;
import ru.bircode.bkb.variable.VariableScanner;

public class Book {
    
    private final UBook BOOK = new UBook();
    public final String BOOK_NAME;
    private final List<List<String>> PAGES;
    
    public Book(String name, List<List<String>> pages){
        BOOK_NAME = name;
        PAGES = pages;
        try {
            createBook();
        } catch (InvalidVariableException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createBook() throws InvalidVariableException {
        for(List<String> page : PAGES){
            UBPage bpage = BOOK.addPage();
            for(String line : page){
                if(line.equalsIgnoreCase("$CLEAR")){
                    bpage.addText("\n");
                    continue;
                }
                Object[] found = VariableScanner.findVariables(line);
                if(VariableScanner.sortVars(found).isEmpty()){
                    bpage.addText(line);
                }else{
                    for(Object obj : found){
                        if(obj instanceof String){
                            bpage.addText((String)obj);
                        }else if(obj instanceof Variable){
                            buildVariable(bpage, (Variable)obj);
                        }
                    }
                }
                bpage.addText("\n");
            }
        }
    }
    
    private void buildVariable(UBPage bpage, Variable variable){
        String action = variable.getName();
        String[] args = variable.getArgs();
        switch (action) {
            case "hover":
                bpage.addHoverText(args[0], args[1]);
                break;
            case "url_button":
                bpage.addURLButton(args[0], args[1]);
                break;
            case "hover_url_button":
                bpage.addHoverURLButton(args[0], args[1], args[2]);
                break;
            case "command_button":
                bpage.addCommandButton(args[0], "/"+args[1]);
                break;
            case "hover_command_button":
                bpage.addHoverCommandButton(args[0], args[1], "/"+args[2]);
                break;
            case "open_book":
                bpage.addCommandButton(args[0], "/bkco open "+args[1]);
                break;
            case "hover_open_book":
                bpage.addHoverCommandButton(args[0], args[1], "/bkco open "+args[2]);
                break;
        }
    }
    
    public void open(Player target){
        BOOK.open(target);
    }
    
    public void close(Player target){
        BOOK.close(target);
    }
    
    public String getPermission(){
        return "bookcommands.open."+BOOK_NAME;
    }
    
}
