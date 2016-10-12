package LibrarySystem;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Classe do servidor da biblioteca. Contém a lógica de funcionamento do sistema da biblioteca.
 */
public class LibraryServer extends UnicastRemoteObject implements LibraryServerInterface {

    private HashMap<String, Timer> m_bookTimerMap = new HashMap<>();
    private List<Book> m_booksList = new ArrayList<>();
    private List<Client> mClientList = new ArrayList<>();

    /**
     * Task usada para dar sequência na lista de reservas em caso de estouro do tempo de reserva.
     */
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

                if(!mBook.getReservationList().isEmpty())
                {
                    mBook.setReservationExpiryDate(addDaysToDate(new Date(), 5));
                    mBook.notifyReservee();

                    Timer timer = new Timer();
                    timer.schedule(new BookTimerTask(mBook), mBook.getReservationExpiryDate());
                    m_bookTimerMap.put(mBook.getName(), timer);
                }
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

    public synchronized Boolean lendBook(String client, String bookName) throws RemoteException{
        Client c = getClient(client);
        //Check the pre-requisites for the client
        List<Book> clientBookList = c.getBookList();

        if(c.getPenaltyValidationDate() != null)
        {
            if(c.getPenaltyValidationDate().getTime() > new Date().getTime()) {
                return false;
            }
        }

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

    /***
     * Adiciona um número de dias a data. Na implementação atual, está na verdade adicionando segundos.
     * @param date - data na qual o tempo será adicionado
     * @param days - número de dias a ser adicionado
     * @return resultado da adição
     */
    private Date addDaysToDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
//        cal.add(Calendar.DATE, days);
        cal.add(Calendar.SECOND, days);
        return cal.getTime();
    }

    /***
     * Cria o registry e adiciona o LibraryServer a ele.
     * @param args
     */
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
