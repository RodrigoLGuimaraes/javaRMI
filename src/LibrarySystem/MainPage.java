package LibrarySystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Created by rodrigoguimaraes on 2016-09-27.
 */
public class MainPage {
    private JButton searchBtn;
    private JPanel mainForm;
    private JTextArea textArea1;
    private JList resultList;
    private JLabel meusLivros;
    private JLabel searchLabel;
    private JButton emprestaReservaBtn;
    private JTable meusLivrosTbl;
    private JScrollPane meusLivrosScroll;
    private DefaultListModel<String> listOfResults;

    public MainPage() {


        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Please implement actual search logic");

                if(listOfResults == null)
                {
                    listOfResults = new DefaultListModel();
                    listOfResults.addElement(textArea1.getText());
                    resultList.setModel(listOfResults);
                }
                else
                {
                    listOfResults.addElement(textArea1.getText());
                }
            }
        });


        //TODO: Retrieve my books from DB
        String[] columnNames = {"Livro",
                "Renovações",
                "Devolução"};
        Object[][] data = {
                {"Livro 1", new Integer(0),
                        "21/10/2016"},
                {"Livro 2", new Integer(1),
                        "20/10/2016"},
                {"Livro 3", new Integer(2),
                        "22/10/2016"}
        };


        DefaultTableModel tblModel = new DefaultTableModel(data, columnNames);
        meusLivrosTbl.setModel(tblModel);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Library System v0.1");
        frame.setContentPane(new MainPage().mainForm);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400, 400);
        frame.setVisible(true);

    }
}
