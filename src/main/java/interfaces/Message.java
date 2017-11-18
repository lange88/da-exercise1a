package interfaces;

import java.io.Serializable;

/**
 * Created by jeroen on 11/16/17.
 * Implementation for Message that gets passed along between processes.
 */
public class Message implements Serializable {
    public Message(String message, int[] vectorClock, int processSenderId)  {
        this.message = message;
        this.vectorClock = vectorClock;
        this.processSenderId = processSenderId;
    }

    private static final long serialVersionUID = 20120731125400L;
    public String message;
    public int[] vectorClock;
    public int processSenderId;
}
