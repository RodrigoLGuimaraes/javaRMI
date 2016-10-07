package LibrarySystem;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by hudo on 10/2/16.
 */
public interface ClientCBHandler extends Remote{
    void callback(String bookName) throws RemoteException;
    String getName() throws RemoteException;
}
