import interfaces.DA_BSS_RMI;
import interfaces.Message;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by jeroen on 11/16/17.
 * Implementation for Birman-Schiper-Stephenson algorithm.
 */
public class DA_BSS extends UnicastRemoteObject implements DA_BSS_RMI, Runnable {
    private int[] V;
    private int e = 0;
    private int processNumber;
    private Queue<Message> B;
    private int totalProcesses;

    DA_BSS(int processNumber, int totalProcesses) throws RemoteException {
        V = new int[totalProcesses];
        for (int index = 0; index < V.length; index++) {
            V[index] = 0;
        }
        this.processNumber = processNumber;
        this.totalProcesses = totalProcesses;
        B = new LinkedList<Message>();
    }

    /**
     * Starting point of thread.
     * Sleeps random intervals between 0 and 5 seconds then broadcasts a message.
     */
    public void run() {
        while(true) {
            try {
                Thread.sleep(new Random().nextInt(5000));
                broadCast("woopwoop");
            } catch (InterruptedException e1) {
                System.out.println("InterruptedException occurred while running thread.");
                e1.printStackTrace();
                return;
            }
        }
    }

    /**
     * Broadcast a message to other processes using RMI.
     * @param message String message to send to other processes.
     */
    private void broadCast(String message) {
        e++;
        V[processNumber] = e;

        // send to all processes
        for (int index = 0; index < totalProcesses; index++) {
            String name = "rmi://localhost/DA_BSS" + index;
            try {
                Message m = new Message(message, V, processNumber);
                DA_BSS_RMI o = (DA_BSS_RMI) java.rmi.Naming.lookup(name);
                o.receive(m);
            } catch (NotBoundException e1) {
                System.out.println("NotBoundException while sending message for name: " + name);
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                System.out.println("MalformedURLException while sending message for name: " + name);
                e1.printStackTrace();
            } catch (RemoteException e1) {
                System.out.println("RemoteException while sending message for name: " + name);
                e1.printStackTrace();
            }
        }
    }

    /**
     * Receive a message through RMI.
     * @param m Message to be received.
     * @throws RemoteException Occurs when an error is generated.
     */
    public void receive(Message m) throws RemoteException {
        /*
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

    /**
     * Deliver a message by printing it on the screen and
     * removing it from the buffer.
     * @param m Message to be delivered.
     */
    private void deliver(Message m) {
        System.out.println("Delivering message: " + m.message);
        V[m.processSenderId] = m.V[m.processSenderId];
        B.remove(m);
    }

    /**
     * Check if message is deliverable according to BSS algorithm.
     * @param m Message to be checked.
     * @param V Time vector to be checked.
     * @return True iff message is deliverable.
     */
    private boolean isDeliverable(Message m, int[] V) {
        for (int index = 0; index < processNumber; index++) {
            if (V[index] < m.V[index]) {
                return false;
            }
        }
        return true;
    }
}

