package logic;

import java.io.Serializable;

/**
 * Description:
 * Class for the messages between the Client and the Server in the system
 */
public class Message implements Serializable {
    /**
     * Serialization of the message
     */
    private static final long serialVersionUID = 1L;

    /**
     * Function command to call and name of table
     */
    private String Command;

    /**
     * Data for usage or to return
     */
    private Object Data;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param Command String.class
     * @param Data Object.class
     */
    public Message(String Command, Object Data) {
        this.Command = Command;
        this.Data = Data;
    }

    /**
     * Description:
     * Getter method for the message's command
     *
     * @return Command String.class
     */
    public String GetCommand() {
        return Command;
    }

    /**
     * Description:
     * Setter method for the message's command
     *
     * @param command String.class
     */
    public void setCommand(String command) {
        Command = command;
    }

    /**
     * Description:
     * Getter method for the message's data
     *
     * @return Data Object.class
     */
    public Object getData() {
        return Data;
    }

    /**
     * Description:
     * Setter method for the message's data
     *
     * @param data Object.class
     */
    public void setData(Object data) {
        Data = data;
    }
}