package LibrarySystem;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Created by rodrigoguimaraes on 2016-09-27.
 */
public class MainPage {
    private JButton searchBtn;
    private JPanel mainForm;
    private JList resultList;
    private JLabel meusLivros;
    private JButton emprestaReservaBtn;
    private JScrollPane meusLivrosScroll;
    private JTextField bookSearchTF;
    private JList meusLivrosJL;
    private JTextField clientTF;

    private LibraryServerInterface m_libraryStub;

    public MainPage() {

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

        emprestaReservaBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

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
                    m_libraryStub.lendBook(clientTF.getText(), book.getName());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Library System v0.2");
        frame.setContentPane(new MainPage().mainForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
