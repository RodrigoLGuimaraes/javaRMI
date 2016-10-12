package LibrarySystem;

import java.rmi.Remote;
import java.rmi.RemoteException;

/***
 * Interface remota do cliente. Possui os métodos de callback, usado para notificação de novo livro de interesse
 * disponível, e a função getName, que retorna o nome do cliente.
 */
public interface ClientCBHandler extends Remote{
    void callback(String bookName) throws RemoteException;
    String getName() throws RemoteException;
}
