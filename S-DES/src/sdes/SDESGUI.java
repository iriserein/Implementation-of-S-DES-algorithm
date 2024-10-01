package sdes;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SDESGUI extends JFrame {
    private JTextField plaintextField;
    private JTextField keyField;
    private JTextArea resultArea;
    private JButton encryptButton;
    private JButton decryptButton;

    public SDESGUI() {
        setTitle("S-DES Encryption/Decryption");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel plaintextLabel = new JLabel("Plaintext (8 bits):");
        plaintextLabel.setBounds(10, 10, 150, 25);
        add(plaintextLabel);

        plaintextField = new JTextField();
        plaintextField.setBounds(160, 10, 200, 25);
        add(plaintextField);

        JLabel keyLabel = new JLabel("Key (10 bits):");
        keyLabel.setBounds(10, 40, 150, 25);
        add(keyLabel);

        keyField = new JTextField();
        keyField.setBounds(160, 40, 200, 25);
        add(keyField);

        encryptButton = new JButton("Encrypt");
        encryptButton.setBounds(10, 80, 100, 25);
        add(encryptButton);

        decryptButton = new JButton("Decrypt");
        decryptButton.setBounds(120, 80, 100, 25);
        add(decryptButton);

        resultArea = new JTextArea();
        resultArea.setBounds(10, 120, 350, 130);
        add(resultArea);

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String plaintext = plaintextField.getText();
                String key = keyField.getText();
                String ciphertext = SDES.encrypt(plaintext, key);
                resultArea.setText("Ciphertext: " + ciphertext);
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ciphertext = plaintextField.getText();
                String key = keyField.getText();
                String decryptedText = SDES.decrypt(ciphertext, key);
                resultArea.setText("Decrypted: " + decryptedText);
            }
        });
    }

    public static void main(String[] args) {
        SDESGUI gui = new SDESGUI();
        gui.setVisible(true);
    }
}
