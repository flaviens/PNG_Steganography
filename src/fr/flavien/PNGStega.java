// : c14:PNGStega.java
// Demonstration of File dialog boxes.
// From 'Thinking in Java, 3rd ed.' (c) Bruce Eckel 2002
// www.BruceEckel.com. See copyright notice in CopyRight.txt.

package fr.flavien;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.swing.*;

public class PNGStega extends JFrame {
    private JLabel toHideAreaLabel = new JLabel("Hidden text: "), base64Label = new JLabel("Base64 encoded hidden text: "), aesLabel = new JLabel("Password: ");
    private JTextArea toHideArea = new JTextArea(5, 20), base64Area = new JTextArea(5, 20);;
    private JTextField fieldPassword = new JTextField();

    private JButton decryptButton = new JButton("Decrypt"), encryptButton = new JButton("Encrypt");
    private JButton openButton = new JButton("Open"), saveButton = new JButton("Save");

    PNGFile pngFile = null;

    ToHideAreaL toHideAreaL = new ToHideAreaL();

    public PNGStega() {
        JPanel p = new JPanel();
        openButton.addActionListener(new OpenButtonL());
        p.add(openButton);
        saveButton.addActionListener(new SaveButtonL());
        p.add(saveButton);

        Container cp = getContentPane();
        cp.add(p, BorderLayout.SOUTH);
        p = new JPanel();
        p.setLayout(new GridLayout(5, 1));

        p.add(toHideAreaLabel);
        p.add(toHideArea);
        toHideArea.addKeyListener(toHideAreaL);
        toHideArea.setText("<Please start by opening an existing file.>");

        p.add(base64Label);
        p.add(base64Area);
        base64Area.setEditable(false);

        JPanel aesPanel = new JPanel();
        aesPanel.setLayout(new GridLayout(2, 2));
        aesPanel.add(aesLabel);
        aesPanel.add(fieldPassword);

        aesPanel.add(encryptButton);
        encryptButton.addActionListener(new EncryptButtonL());
        aesPanel.add(decryptButton);
        decryptButton.addActionListener(new DecryptButtonL());
        p.add(aesPanel);

        cp.add(p, BorderLayout.NORTH);
    }

    class OpenButtonL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser();
            // Demonstrate "openButton" dialog:
            int rVal = c.showOpenDialog(PNGStega.this);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                try {
                    pngFile = new PNGFile();

                    byte[] chunkData = pngFile.readFile(c.getSelectedFile().getPath());

                    if(chunkData == null) {
                        System.out.println("No chunk data detected.");
                        base64Area.setText("");
                        toHideArea.setText("<No hidden data detected>");
                    } else {
                        base64Area.setText(Base64.getEncoder().encodeToString(chunkData));

                        try {
                            toHideArea.setText(new String(chunkData, "UTF-8"));
                        } catch (Exception ex) {
                            toHideArea.setText("<Decoding from hidden text failed. Is it encrypted?>");
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (IllegalArgumentException e1) {
                    System.out.println("Error: Not a PNG file!");
                }
            }
        }
    }

    class SaveButtonL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser c = new JFileChooser();

            int rVal = c.showOpenDialog(PNGStega.this);
            if (rVal == JFileChooser.APPROVE_OPTION && pngFile != null) {
                try {
                    pngFile.insertChunk(ByteUtil.hiddenChunkName.getBytes("UTF-8"), Base64.getDecoder().decode(base64Area.getText().getBytes("ASCII")));

                    pngFile.writeFile(c.getSelectedFile().getPath());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    class EncryptButtonL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            base64Area.setText(AESUtil.encrypt(base64Area.getText(), fieldPassword.getText()));

            try {
                toHideArea.setText(new String(Base64.getDecoder().decode(base64Area.getText()), "UTF-8"));
            } catch (Exception ex) {
                toHideArea.setText("<Decoding from hidden text failed. Is it encrypted?>");
            }
        }
    }

    class DecryptButtonL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            base64Area.setText(AESUtil.decrypt(base64Area.getText(), fieldPassword.getText()));

            try {
                toHideArea.setText(new String(Base64.getDecoder().decode(base64Area.getText()), "UTF-8"));
            } catch (Exception ex) {
                toHideArea.setText("<Decoding from hidden text failed. Is it encrypted?>");
            }
        }
    }

    class ToHideAreaL implements KeyListener {
        public void actionPerformed(KeyEvent e) {
            try {
                String base64Str = Base64.getEncoder().encodeToString(toHideArea.getText().getBytes("UTF-8"));
                base64Area.setText(base64Str);
            } catch (UnsupportedEncodingException e1) {
                base64Area.setText("<Encoding failed>");
                e1.printStackTrace();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
            actionPerformed(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            actionPerformed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            actionPerformed(e);
        }
    }


    public static void main(String[] args) {
        run(new PNGStega(), 600, 500);
    }

    public static void run(JFrame frame, int width, int height) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setVisible(true);
    }
}