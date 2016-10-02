package LibrarySystem;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rodrigoguimaraes on 2016-09-28.
 */
public class LibraryServer extends UnicastRemoteObject implements LibraryServerInterface{

    private HashMap<ServerEvent, List<ClientCBHandler>> m_cbMap = new HashMap<>();
    private List<Book> m_booksList = new ArrayList<>();

    public LibraryServer() throws RemoteException {
        super();

        m_booksList.add(new Book("Teste1"));
        m_booksList.add(new Book("Teste2"));
        m_booksList.add(new Book("Teste3"));
        m_booksList.add(new Book("Teste4"));
    }

    public List<Book> getClientBooks(String client)
    {
        List<Book> clientBooks = new ArrayList<>();
        for(Book book : m_booksList) {
            if(book.getOwner() == null) continue;

            if(book.getOwner().equals(client)) {
                clientBooks.add(book);
            }
        }
        return clientBooks;
    }

    public List<Book> getBooksByName(String name){
        List<Book> booksFound = new ArrayList<>();
        for(Book book : m_booksList) {
            if(book.getName().trim().toLowerCase().contains(name.toLowerCase())) {
                booksFound.add(book);
            }
        }

        return booksFound;
    }

    public Boolean lendBook(String client, String bookName) {
        //TODO(Hudo): Check the pre-requisites for the client

        for(Book book : m_booksList) {
            if(book.getName().equals(bookName)) {
                //TODO(Hudo): Check the pre-requisites for the book

                book.setOwner(client);
                return true;
            }
        }

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

    @Override
    public void addClientCBHandler(ClientCBHandler handler, ServerEvent event) {
        if(m_cbMap.containsKey(event)) {
            m_cbMap.get(event).add(handler);
        } else {
            ArrayList<ClientCBHandler> list = new ArrayList<>();
            list.add(handler);
            m_cbMap.put(event, list);
        }
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("LibraryServer", new LibraryServer());

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
