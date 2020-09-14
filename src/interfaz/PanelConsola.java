package interfaz;

import util.CustomOutputStream;

import javax.swing.*;
import java.io.PrintStream;

public class PanelConsola extends JPanel {

    private PrintStream standardOut;
    private JTextArea txtLog;

    public PanelConsola(){
        txtLog = new JTextArea(20, 40);
        txtLog.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(txtLog));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);
        add(new JScrollPane(txtLog));
    }
}
