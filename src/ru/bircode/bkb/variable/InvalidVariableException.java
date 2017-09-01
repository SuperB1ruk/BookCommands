package ru.bircode.bkb.variable;

public class InvalidVariableException extends Exception {
    
    public InvalidVariableException(){ 
        super(); 
    }
    
    public InvalidVariableException(String message){ 
        super(message); 
    }
    
    public InvalidVariableException(String message, Throwable cause){ 
        super(message, cause); 
    }
    
    public InvalidVariableException(Throwable cause){ 
        super(cause); 
    }
    
}
