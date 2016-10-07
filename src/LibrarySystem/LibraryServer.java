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

    private HashMap<String, Timer> m_bookTimerMap = new HashMap<>();
    private List<Book> m_booksList = new ArrayList<>();
    private List<Client> mClientList = new ArrayList<>();

    private class BookTimerTask extends TimerTask {

        private Book mBook;

        public BookTimerTask(Book book) {
            super();
            mBook = book;
        }

        @Override
        public void run() {
            if(!mBook.getReservationList().isEmpty()) {
                mBook.getReservationList().remove(0);
                mBook.notifyReservee();
            }
        }
    };

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

    public Boolean lendBook(String client, String bookName) throws RemoteException{
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
                    if(!book.getReservationList().get(0).getName().equals(c.getName()))
                    {
                        return false;
                    }
                    book.getReservationList().remove(0);
                    m_bookTimerMap.get(book.getName()).cancel();
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
        client.removeBook(book);

        if(!book.getReservationList().isEmpty()) {
            book.setReservationExpiryDate(addDaysToDate(new Date(), 5));
            book.notifyReservee();

            Timer timer = new Timer();
            timer.schedule(new BookTimerTask(book), book.getReservationExpiryDate());
            m_bookTimerMap.put(book.getName(), timer);
        }

        return true;
    }

    @Override
    public Boolean addReservationBook(String bookName, ClientCBHandler clientRef) throws RemoteException {
        for(Book book : m_booksList)
        {
            if(book.getName().equals(bookName)) {
                List<ClientCBHandler> reservationList = book.getReservationList();
                for(ClientCBHandler clientCB : reservationList)
                {
                    String client = clientCB.getName();
                    if(client.equals(clientRef.getName()))
                    {
                        return false;
                    }
                }
                reservationList.add(clientRef);
                return true;
            }
        }
        return false;
    }

    public Boolean renovateBook(String bookName)
    {
        Book book = getBook(bookName);
        if(book.getOwner() == null) return false;

        Client client = getClient(book.getOwner());
        if(client.getPenaltyValidationDate() != null
                && client.getPenaltyValidationDate().getTime() > new Date().getTime()) {
            return false;
        }

        for(Book b : getClient(book.getOwner()).getBookList()) {
            if(book.getReturnDate().getTime() < (new Date()).getTime()) {
                return false;
            }
        }

        if(book.getReservationList().isEmpty())
        {
            book.setReturnDate(addDaysToDate(new Date(), 7));
            return true;
        }

        return false;
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
