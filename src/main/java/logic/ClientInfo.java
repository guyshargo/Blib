package logic;

import java.io.Serializable;

/**
 * Description:
 * Class for representing the Client's info in the system
 */
public class ClientInfo implements Serializable {
    private static final long serialVersionUID = 1L; // Recommended for Serializable classes
    private final String ip;
    private final String host;
    private String status;

    /**
     * Description:
     * Method for constructing the given class
     *
     * @param ip     String.class
     * @param host   String.class
     * @param status String.class
     */
    public ClientInfo(String ip, String host, String status) {
        this.ip = ip;
        this.host = host;
        this.status = status;
    }

    /**
     * Description:
     * Method for constructing the given class (without a host)
     *
     * @param ip     String.class
     * @param status String.class
     */
    public ClientInfo(String ip, String status) {
        this.ip = ip;
        this.host = "";
        this.status = status;
    }

    /**
     * Description:
     * Getter method for the Client's ip
     *
     * @return ip String.class
     */
    public String getIp() {
        return ip;
    }

    /**
     * Description:
     * Getter method for the Client's host
     *
     * @return host String.class
     */
    public String getHost() {
        return host;
    }

    /**
     * Description:
     * Getter method for the Client's status
     *
     * @return status String.class
     */
    public String getStatus() {
        return status;
    }

    /**
     * Description:
     * Setter method for the Client's status
     *
     * @param status String.class
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Description:
     * Method for checking if given object is equal to the current object calling
     *
     * @param o Object.class
     * @return boolean (true if the given object is equal, else return false)
     */
    @Override
    public boolean equals(Object o) {
        // If the two references are identical, they are equal.
        if (this == o) return true;

        // If o is null or not the same class
        if (o == null || getClass() != o.getClass()) return false;

        // Cast the object to ClientInfo
        ClientInfo that = (ClientInfo) o;

        // Compare ip and host
        return ip.equals(that.ip);
    }
}