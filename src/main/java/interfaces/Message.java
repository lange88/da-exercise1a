package interfaces;

/**
 * Created by jeroen on 11/16/17.
 */
public class Message {
    public Message(String message, int[] v, int processSenderId) {
        this.message = message;
        V = v;
        this.processSenderId = processSenderId;
    }

    public String message;
    public int[] V;
    public int processSenderId;
}
