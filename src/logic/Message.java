package logic;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String Command;
    private Object Data;
    //Message constructor
    public Message(String Command, Object Data){
        this.Command = Command;
        this.Data = Data;
    }
    /***Getters and Setters***/
    public String GetCommand(){
        return Command;
    }
    public Object getData(){
        return Data;
    }
    public void setCommand(String command) {
        Command = command;
    }
    public void setData(Object data) {
        Data = data;
    }
}