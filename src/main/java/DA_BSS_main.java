import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

/**
 * Created by jeroen on 11/16/17.
 * Main class for starting DA.
 */
public class DA_BSS_main {
    public static void main(String... args) {
        OptionParser parser = new OptionParser();

        parser.accepts("p", "Process number")
                .withRequiredArg().ofType(Integer.class);
        parser.accepts("t", "Total number of processes in DS")
                .withRequiredArg().ofType(Integer.class);

        OptionSet options = parser.parse(args);

        if (!options.has("p") || !options.has("t")) {
            return;
        }

        int processNumber, totalProcesses;
        processNumber = (Integer) options.valueOf("p");
        totalProcesses = (Integer) options.valueOf("t");
        String name = "rmi://localhost/DA_BSS" + processNumber;

        // create local registry so RMI can register itself
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            System.out.println("RemoteException occurred while starting the registry.");
            e.printStackTrace();
        }

        // create DA object and bind RMI name
        DA_BSS da = null;
        try {
            da = new DA_BSS(processNumber, totalProcesses);
            java.rmi.Naming.bind(name, da);
        } catch (AlreadyBoundException e) {
            System.out.println("AlreadyBoundException occurred while binding object with RMI name: " + name);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException occurred while binding object with RMI name: " + name);
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("RemoteException occurred while binding object with RMI name: " + name);
            e.printStackTrace();
        }

        if (da == null) {
            return;
        }

        // Create and install a security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // finally start the worker thread of the process
        System.out.println("Starting process node with id=" + processNumber
                + ", totalProcesses=" + totalProcesses);
        new Thread(da).start();
    }
}
