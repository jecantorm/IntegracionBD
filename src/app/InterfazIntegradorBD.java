package app;

import util.CustomOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class InterfazIntegradorBD extends JFrame implements ActionListener {

    private IntegradorBD integradorBD;
    private PrintStream standardOut;
    private Button btnCorrer;

    private static final String CORRER = "Correr";

    public InterfazIntegradorBD(){
        integradorBD = new IntegradorBD(this);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(new Dimension(800,200));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        btnCorrer = new Button(CORRER);
        btnCorrer.setActionCommand(CORRER);
        btnCorrer.addActionListener(this);
        panel.add(btnCorrer, BorderLayout.WEST);

        JTextArea txtLog = new JTextArea(1000, 30);
        txtLog.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(txtLog));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);
        panel.add(new JScrollPane(txtLog), BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }

    public void correr() {
        integradorBD.start();
    }

    public static void main(String[] args){
        InterfazIntegradorBD intefaz = new InterfazIntegradorBD();
    }

    public void desactivarBoton(){
        btnCorrer.setEnabled(false);
    }

    public void activarBoton(){
        btnCorrer.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        if(comando.equals(CORRER)){
            correr();
        }
    }
}
