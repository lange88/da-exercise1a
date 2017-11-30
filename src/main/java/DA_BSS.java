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
    int[] vectorClock;
    private int timeStamp = 0;
    private int processNumber;
    Queue<Message> buffer;
    private int totalProcesses;

    DA_BSS(int processNumber, int totalProcesses) throws RemoteException {
        vectorClock = new int[totalProcesses];
        for (int index = 0; index < vectorClock.length; index++) {
            vectorClock[index] = 0;
        }
        this.processNumber = processNumber;
        this.totalProcesses = totalProcesses;
        buffer = new LinkedList<Message>();
    }

    /**
     * Starting point of thread.
     * Sleeps random intervals between 0 and 5 seconds then broadcasts a message.
     */
    public void run() {
        while(true) {
            try {
                Thread.sleep(new Random().nextInt(5000));
                broadCast("woopwoop" + processNumber);
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
        timeStamp++;
        vectorClock[processNumber] = timeStamp;

        // send to all processes
        for (int index = 0; index < totalProcesses; index++) {
            String name = "rmi://";
            if (index == processNumber) {
                name += "localhost/DA_BSS" + index;
            }
            else {
                name += "145.94.153.54/DA_BSS" + index;
            }

            try {
                Message m = new Message(message, vectorClock, processNumber);
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
     * @param message Message to be received.
     * @throws RemoteException Occurs when an error is generated.
     */
    public void receive(Message message) throws RemoteException {
        /*
        upon receipt of (m,Vm ) do
        if Dj (m) then
            deliver(m)
            while ({(m, k, Vm ) ∈ B|Dk (m)} ̸= ∅) do
            deliver(m) with (m, Vm) ∈ B such that Dj (m)
        else add (m, j, Vm ) to B
        */
        int[] temporaryVectorClock = new int[vectorClock.length];
        System.arraycopy(vectorClock, 0, temporaryVectorClock, 0, vectorClock.length);
        temporaryVectorClock[message.processSenderId] = message.vectorClock[message.processSenderId];

        if (isDeliverable(totalProcesses, message, vectorClock)) {
            deliver(message);
            for(Message k : buffer) {
                if (k.processSenderId != message.processSenderId) {
                    // check if message k is now deliverable using tmpV
                    if (isDeliverable(totalProcesses, k, temporaryVectorClock)) {
                        deliver(k);
                    }
                }
            }
        }
        else {
            buffer.add(message);
        }
    }

    /**
     * Deliver a message by printing it on the screen and
     * removing it from the buffer.
     * @param m Message to be delivered.
     */
    private void deliver(Message m) {
        System.out.println("[" + processNumber + "] message delivered from [" + m.processSenderId + "]: " + m.message);
        vectorClock[m.processSenderId] = m.vectorClock[m.processSenderId];
        buffer.remove(m);
    }

    /**
     * Check if message is deliverable according to BSS algorithm.
     * @param totalProcesses Total number of processes
     * @param message Message to be checked.
     * @param vectorClock Time vector to be checked.
     * @return True iff message is deliverable.
     */
    static boolean isDeliverable(int totalProcesses, Message message, int[] vectorClock) {
        for (int index = 0; index < totalProcesses; index++) {
            if (vectorClock[index] < message.vectorClock[index]) {
                return false;
            }
        }
        return true;
    }
}

