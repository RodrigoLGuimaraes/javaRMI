package LibrarySystem;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by rodrigoguimaraes on 2016-09-28.
 */
public class LibraryServer implements LibraryServerInterface{

    public Object[][] getClientBooks(int clientID)
    {
        //TODO: This is just a mockup
        Object[][] data = {
                {"Livro 1 SERVER", new Integer(0),
                        "21/10/2016"},
                {"Livro 2 SERVER", new Integer(1),
                        "20/10/2016"},
                {"Livro 3 SERVER", new Integer(2),
                        "22/10/2016"}
        };
        return data;
    }
    public Object[][] getBooksOnCatalog(String query)
    {
        return null;
    }
    public Boolean lendBook(int bookId)
    {
        return false;
    }
    public Boolean giveBookBack(int bookId)
    {
        return false;
    }
    public Boolean bookReservation(int bookId)
    {
        return false;
    }

    public static void main(String[] args) {
        try {
            LibraryServer obj = new LibraryServer();
            LibraryServerInterface stub = (LibraryServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("LibraryServer", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
