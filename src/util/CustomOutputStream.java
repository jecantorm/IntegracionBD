package util;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

public class CustomOutputStream extends OutputStream {

    private JTextArea txtLog;

    public CustomOutputStream(JTextArea txtLog){
        this.txtLog = txtLog;
    }

    @Override
    public void write(int b) throws IOException {
        txtLog.append(String.valueOf((char) b));
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
}
