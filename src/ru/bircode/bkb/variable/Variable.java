package ru.bircode.bkb.variable;

public class Variable {
    
    private final String NORMAL_VARIABLE;
    private final String VARIABLE_DATA;
    private final String VARIABLE_NAME;
    private String[] VARIABLE_ARGS;
    
    public Variable(String variableData){
        NORMAL_VARIABLE = VariableScanner.getOpener()+variableData+VariableScanner.getCloser();
        VARIABLE_DATA = variableData;
        VARIABLE_NAME = variableData.split(">")[0];
        try {
            VARIABLE_ARGS = variableData.substring(VARIABLE_NAME.length()+1).split(";");
        } catch (Exception exc) { }
    }
    
    public String getVariable(){
        return NORMAL_VARIABLE;
    }
    
    public String getData(){
        return VARIABLE_DATA;
    }
    
    public String getName(){
        return VARIABLE_NAME;
    }
    
    public String[] getArgs(){
        return VARIABLE_ARGS;
    }
    
}
