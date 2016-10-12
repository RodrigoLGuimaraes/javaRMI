package LibrarySystem;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/***
 * Interface remota do servidor. Possui os métodos de busca de livros, de empréstimo, devolução e de gerenciamento
 * de reservas e renovações.
 */
public interface LibraryServerInterface extends Remote {

    List<Book> getClientBooks(String client) throws RemoteException;
    List<Book> getBooksByName(String name) throws RemoteException;
    Boolean lendBook(String client, String bookName) throws RemoteException;
    Boolean returnBook(String bookName) throws RemoteException;

    Boolean addReservationBook(String bookName, ClientCBHandler clientRef) throws RemoteException;
    Boolean renovateBook(String bookName) throws RemoteException;
}
