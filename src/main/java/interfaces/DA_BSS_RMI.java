package interfaces;

import java.rmi.Remote;

/**
 * Created by jeroen on 11/16/17.
 */
public interface DA_BSS_RMI extends Remote{
    public void receive(Message m) throws java.rmi.RemoteException;
}
