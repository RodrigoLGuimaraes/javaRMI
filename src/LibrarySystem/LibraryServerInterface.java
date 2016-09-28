package LibrarySystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by rodrigoguimaraes on 2016-09-28.
 */
public interface LibraryServerInterface extends Remote {
    Object[][] getClientBooks(int clientID) throws RemoteException;
    Object[][] getBooksOnCatalog(String query) throws RemoteException;
    Boolean lendBook(int bookId) throws RemoteException;
    Boolean giveBookBack(int bookId) throws RemoteException;
    Boolean bookReservation(int bookId) throws RemoteException;
}
