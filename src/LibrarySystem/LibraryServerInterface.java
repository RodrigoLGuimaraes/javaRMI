package LibrarySystem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by rodrigoguimaraes on 2016-09-28.
 */
public interface LibraryServerInterface extends Remote {

    List<Book> getClientBooks(String client) throws RemoteException;
    List<Book> getBooksByName(String name) throws RemoteException;
    Boolean lendBook(String client, String bookName) throws RemoteException;
    Boolean giveBookBack(int bookId) throws RemoteException;
    Boolean bookReservation(int bookId) throws RemoteException;

    void addClientCBHandler(ClientCBHandler handler, ServerEvent event) throws RemoteException;
}
