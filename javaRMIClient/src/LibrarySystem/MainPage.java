package LibrarySystem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/***
 * Classe que implementa o funcionamento da UI do cliente.
 */
public class MainPage extends UnicastRemoteObject implements ClientCBHandler {
    private JButton searchBtn;
    private JPanel mainForm;
    private JList resultList;
    private JLabel meusLivros;
    private JButton emprestaReservaBtn;
    private JScrollPane meusLivrosScroll;
    private JTextField bookSearchTF;
    private JList meusLivrosJL;
    private JTextField clientTF;
    private JButton mDevolverButton;
    private JButton mRenovarLivroButton;

    private LibraryServerInterface m_libraryStub;

    /***
     * Na construtora os listeners dos eventos da interface são criados. O tratamento dos eventos de clique nos botões
     * invocam as funções de renovação, empréstimo, devolução, entre outras.
     * @throws RemoteException
     */
    public MainPage() throws RemoteException {

        try {
            Registry registry = LocateRegistry.getRegistry();
            m_libraryStub = (LibraryServerInterface) registry.lookup("LibraryServer");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

        clientTF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    List<Book> books = m_libraryStub.getClientBooks(clientTF.getText());
                    DefaultListModel<Book> listModel = new DefaultListModel<Book>();
                    for(Book book : books) {
                        listModel.addElement(book);
                    }
                    meusLivrosJL.setModel(listModel);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                clientTF.setEnabled(false);
            }
        });

        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DefaultListModel<Book> listModel = new DefaultListModel<Book>();
                    List<Book> books = m_libraryStub.getBooksByName(bookSearchTF.getText());
                    for(Book book : books) {
                        listModel.addElement(book);
                    }
                    resultList.setModel(listModel);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });
        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {

                    if (resultList.getSelectedIndex() == -1) {
                        //No selection, disable fire button.
                        emprestaReservaBtn.setEnabled(false);
                    } else {
                        //Selection, enable the fire button.
                        emprestaReservaBtn.setEnabled(true);
                    }
                }
            }
        });
        emprestaReservaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Book book = (Book) resultList.getSelectedValue();
                try {
                    if(book.getOwner() == null)
                    {
                        if( ! m_libraryStub.lendBook(clientTF.getText(), book.getName()))
                        {
                            JOptionPane.showMessageDialog(null, clientTF.getText()+", voce nao pode emprestar o livro " + book.getName());
                        }
                    }
                    else
                    {
                        if( m_libraryStub.addReservationBook(book.getName(), MainPage.this) )
                        {
                            JOptionPane.showMessageDialog(null, clientTF.getText()+", voce esta agora na lista de reserva do livro " + book.getName());
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, clientTF.getText()+", impossivel reservar o livro " + book.getName());
                        }
                    }
                    clientTF.postActionEvent();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });
        meusLivrosJL.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {

                    if (meusLivrosJL.getSelectedIndex() == -1) {
                        //No selection, disable fire button.
                        mDevolverButton.setEnabled(false);
                        mRenovarLivroButton.setEnabled(false);
                    } else {
                        //Selection, enable the fire button.
                        mDevolverButton.setEnabled(true);
                        mRenovarLivroButton.setEnabled(true);
                    }
                }
            }
        });
        mDevolverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Book book = (Book) meusLivrosJL.getSelectedValue();
                try {
                    m_libraryStub.returnBook(book.getName());
                    clientTF.postActionEvent();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }


            }
        });
        mRenovarLivroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String bookName = ((Book) meusLivrosJL.getSelectedValue()).getName();
                    if(! m_libraryStub.renovateBook(bookName) )
                    {
                        JOptionPane.showMessageDialog(null, clientTF.getText()+", voce nao pode renovar o livro " + bookName);
                    }
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }

                clientTF.postActionEvent();
            }
        });
    }

    /***
     * Função que avisa o cliente que um novo livro de interesse está reservado para ele no momento.
     * @param bookName - livro que foi recentemente devolvido
     */
    public void callback(String bookName)
    {
        System.out.println(clientTF.getText() + ", voce ja pode emprestar o livro " + bookName);
        JOptionPane.showMessageDialog(null, clientTF.getText() + ", voce ja pode emprestar o livro " + bookName);
    }

    public String getName()
    {
        return clientTF.getText();
    }

    /***
     * Cria o frame da UI e configura alguns de seus parâmetros.
     * @param args
     */
    public static void main(String[] args) {
        JFrame frame = new JFrame("Library System v0.2");
        try
        {
            frame.setContentPane(new MainPage().mainForm);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(450, 400);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
