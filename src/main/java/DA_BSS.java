import interfaces.DA_BSS_RMI;
import interfaces.Message;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by jeroen on 11/16/17.
 */
public class DA_BSS implements DA_BSS_RMI {
    int[] V;
    int e = 0;
    int processNumber;
    Queue<Message> B = new LinkedList<Message>();

    public DA_BSS(int processNumber, int totalProcesses) {
        V = new int[totalProcesses];
        for (int index = 0; index < V.length; index++) {
            V[index] = 0;
        }
        this.processNumber = processNumber;
    }

    public void broadCast(String message) {
        e++;
        V[processNumber] = e;

        Message m = new Message(message, V, processNumber);

        //TODO actually broadcast m using RMI
    }

    public void receive(Message m) throws RemoteException {
        /**
        upon receipt of (m,Vm ) do
        if Dj (m) then
            deliver(m)
            while ({(m, k, Vm ) ∈ B|Dk (m)} ̸= ∅) do
            deliver(m) with (m, Vm) ∈ B such that Dj (m)
        else add (m, j, Vm ) to B
        */
        int[] tmpV = V;
        tmpV[m.processSenderId] = m.V[m.processSenderId];

        if (isDeliverable(m, tmpV)) {
            deliver(m);
            for(Message k : B) {
                if (k.processSenderId != m.processSenderId) {
                    // check if message k is now deliverable using tmpV
                    if (isDeliverable(k, m.V)) {
                        deliver(k);
                    }
                }
            }
        }
        else {
            B.add(m);
        }
    }

    public void deliver(Message m) {
        System.out.println("Delivering message: " + m.message);
        V[m.processSenderId] = m.V[m.processSenderId];
        B.remove(m);
    }

    private boolean isDeliverable(Message m, int[] V) {
        for (int index = 0; index < processNumber; index++) {
            if (V[index] < m.V[index]) {
                return false;
            }
        }
        return true;
    }
}

