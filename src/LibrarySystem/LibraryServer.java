package LibrarySystem;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Created by rodrigoguimaraes on 2016-09-28.
 */
public class LibraryServer extends UnicastRemoteObject implements LibraryServerInterface{

    private HashMap<ServerEvent, List<ClientCBHandler>> m_cbMap = new HashMap<>();
    private List<Book> m_booksList = new ArrayList<>();
    private List<Client> mClientList = new ArrayList<>();

    private Client getClient(String clientName)
    {
        for(Client c : mClientList)
        {
            if(c.getName().equals(clientName))
            {
                return c;
            }
        }

        Client c = new Client(clientName);
        mClientList.add(c);
        return c;
    }

    private Book getBook(String bookName)
    {
        for(Book bk : m_booksList)
        {
            if(bk.getName().equals(bookName))
            {
                return bk;
            }
        }
        return null;
    }

    public LibraryServer() throws RemoteException {
        super();

        m_booksList.add(new Book("Teste1"));
        m_booksList.add(new Book("Teste2"));
        m_booksList.add(new Book("Teste3"));
        m_booksList.add(new Book("Teste4"));
    }

    public List<Book> getClientBooks(String client)
    {
        return getClient(client).getBookList();
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
        Client c = getClient(client);
        //Check the pre-requisites for the client
        List<Book> clientBookList = c.getBookList();
        if(clientBookList.size() >= 3)
        {
            return false;
        }
        for(Book b : clientBookList)
        {
            if(b.getReturnDate().getTime() < (new Date()).getTime())
            {
                return false;
            }
        }

        for(Book book : m_booksList) {
            if(book.getName().equals(bookName)) {
                //Check the pre-requisites for the book
                if(book.getOwner() != null)
                {
                    return false;
                }

                if(!book.getReservationList().isEmpty())
                {
                    if(book.getReservationList().get(0) != c)
                    {
                        return false;
                    }

                    book.removeReservation(c);
                }


                book.setOwner(client);

                book.setReturnDate((addDaysToDate(new Date(), 7)));

                c.addBook(book);
                return true;
            }
        }

        return false;
    }

    public Boolean returnBook(String bookName)
    {
        Book book = getBook(bookName);
        if(book.getOwner() == null) return false;

        Client client = getClient(book.getOwner());

        if(book.getReturnDate().getTime() < new Date().getTime()) {
            client.setPenaltyValidationDate((addDaysToDate(new Date(), 7)));
        }

        book.setOwner(null);
        if(!book.getReservationList().isEmpty()) {
            book.setReservationExpiryDate(addDaysToDate(new Date(), 5));
            try {
                book.getReservationList().get(0).callback(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            book.scheduleReservation();

        }



        return true;
    }

    @Override
    public void addReservationBook(String bookName, ClientCBHandler handler) {
        for(Book book : m_booksList) {
            if(book.getName().equals(bookName)) {
                book.getReservationList().add(handler);
                return;
            }
        }
    }

    public void removeReservationBook(String bookName, ClientCBHandler handler) {
        for(Book book : m_booksList) {
            if(book.getName().equals(bookName)) {
                book.getReservationList().remove(handler);
                return;
            }
        }
    }

    public void renovateBook(String bookName)
    {
        Book book = getBook(bookName);
        if(book.getOwner() == null) return;

        Client client = getClient(book.getOwner());
        if(client.getPenaltyValidationDate() != null
                && client.getPenaltyValidationDate().getTime() > new Date().getTime()) {
            return;
        }

        for(Book b : getClient(book.getOwner()).getBookList()) {
            if(book.getReturnDate().getTime() < (new Date()).getTime()) {
                return;
            }
        }

        if(book.getReservationList().isEmpty())
        {
            book.setReturnDate(addDaysToDate(new Date(), 7));
        }
    }

    private Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
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
