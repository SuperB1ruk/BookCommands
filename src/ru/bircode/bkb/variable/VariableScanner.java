package ru.bircode.bkb.variable;


import java.util.ArrayList;
import java.util.List;

public class VariableScanner {

    private static boolean initialized = false;
    private static String opener, closer;
    
    public static void initialize(String opener, String closer){
        if(initialized) return;
        VariableScanner.opener = opener;
        VariableScanner.closer = closer;
        initialized = true;
    }
    
    public static String getOpener(){
        return opener;
    }
    
    public static String getCloser(){
        return closer;
    }
    
    public static Object[] findVariables(String target) throws InvalidVariableException {
        List<Object> found = new ArrayList<>();
        int openerLength = opener.length();
        int closerLength = closer.length();
        String[] spl = target.split("");
        int state = 0;
        String strData = "";
        String varData = "";
        for(int i = 0; i < spl.length; i++){
            if(state == 0){
                if(i+openerLength > spl.length) break;
                String mO = buildMOpener(spl, i);
                if(mO.equals(opener)){
                    state = 1;
                    i +=openerLength-1;
                    found.add(strData);
                    strData = "";
                }else{
                    strData = strData+spl[i];
                }
            }else if(state == 1){
                if(i+closerLength > spl.length){
                    throw new InvalidVariableException("Not found variable closer! Invalid variable: "+varData);
                }
                String mC = buildMCloser(spl, i);
                if(mC.equals(closer)){
                    state = 0;
                    Variable variable = new Variable(varData);
                    found.add(variable);
                    varData = "";
                    i +=closerLength-1;
                }else{
                    varData = varData+spl[i];
                }
            }
        }
        if(!strData.equals("")) found.add(strData);
        Object[] ofound = new Object[found.size()];
        for(int i = 0; i < found.size(); i++){
            ofound[i] = found.get(i);
        }
        return ofound;
    }
    
    public static List<Variable> sortVars(Object[] found){
        List<Variable> result = new ArrayList<>();
        for(Object obj : found){
            if(obj instanceof Variable)
                result.add((Variable)obj);
        }
        return result;
    }
    
    public static List<String> sortStrings(Object[] found){
        List<String> result = new ArrayList<>();
        for(Object obj : found){
            if(obj instanceof String)
                result.add((String)obj);
        }
        return result;
    }
    
    private static String buildMOpener(String[] target, int l){
        int finish = l+opener.length();
        String b = "";
        for(int i = l; i < finish; i++){
            b = b+target[i];
        }
        return b;
    }
    
    private static String buildMCloser(String[] target, int l){
        int finish = l+closer.length();
        String b = "";
        for(int i = l; i < finish; i++){
            b = b+target[i];
        }
        return b;
    }
    
}
