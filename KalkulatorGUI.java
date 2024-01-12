import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class KalkulatorGUI extends JFrame implements ActionListener {
    private JTextField textField;
    private double angkaPertama, angkaKedua, hasil;
    private int operasi;
    private ArrayList<String> historyList;

    public KalkulatorGUI() {
        // Konfigurasi frame
        setTitle("Kalkulator");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4));

        // TextField untuk menampilkan input dan output
        textField = new JTextField();
        textField.setEditable(false);
        panel.add(textField);

        // Tombol-tombol angka dan operasi matematika
        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "%", "^2"  // Tambahan tombol persen dan kuadrat
        };

        for (String label : buttonLabels) {
            JButton button = createStyledButton(label);
            button.addActionListener(this);
            panel.add(button);
        }

        // Tombol History
        JButton historyButton = createStyledButton("History");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHistory();
            }
        });
        panel.add(historyButton);

        // Tombol Clear (C)
        JButton clearButton = createStyledButton("C");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLastDigit();
            }
        });
        panel.add(clearButton);

        // Tombol Clear All (CE)
        JButton clearAllButton = createStyledButton("CE");
        clearAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });
        panel.add(clearAllButton);

        add(panel);
        setVisible(true);

        // Inisialisasi daftar sejarah
        historyList = new ArrayList<>();
    }

    private JButton createStyledButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);

        // Set warna sesuai dengan label
        if (label.equals("=") || label.equals("/") || label.equals("*") || label.equals("-") || label.equals("+")) {
            button.setBackground(new Color(0x87CEEB));  // Warna biru langit untuk operator
        } else if (label.equals("%") || label.equals("^2")) {
            button.setBackground(new Color(0x7FFFD4));  // Warna aquamarine untuk persen dan kuadrat
        } else {
            button.setBackground(new Color(0x98FB98));  // Warna hijau pastel untuk angka dan . (titik)
        }

        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(Color.GRAY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(getButtonColor(label));
            }
        });

        return button;
    }

    private Color getButtonColor(String label) {
        if (label.equals("=") || label.equals("/") || label.equals("*") || label.equals("-") || label.equals("+")) {
            return new Color(0x87CEEB);  // Warna biru langit untuk operator
        } else if (label.equals("%") || label.equals("^2")) {
            return new Color(0x7FFFD4);  // Warna aquamarine untuk persen dan kuadrat
        } else {
            return new Color(0x98FB98);  // Warna hijau pastel untuk angka dan . (titik)
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.matches("[0-9]|\\.")) {
            textField.setText(textField.getText() + command);
        } else if (command.matches("[*/\\-+]")) {
            angkaPertama = Double.parseDouble(textField.getText());
            switch (command) {
                case "/":
                    operasi = 4;
                    break;
                case "*":
                    operasi = 3;
                    break;
                case "-":
                    operasi = 2;
                    break;
                case "+":
                    operasi = 1;
                    break;
            }
            textField.setText("");
        } else if (command.equals("=")) {
            angkaKedua = Double.parseDouble(textField.getText());
            switch (operasi) {
                case 1:
                    hasil = angkaPertama + angkaKedua;
                    break;
                case 2:
                    hasil = angkaPertama - angkaKedua;
                    break;
                case 3:
                    hasil = angkaPertama * angkaKedua;
                    break;
                case 4:
                    if (angkaKedua == 0) {
                        textField.setText("Error: Tidak bisa dibagi dengan nol");
                        return;
                    } else {
                        hasil = angkaPertama / angkaKedua;
                    }
                    break;
            }
            textField.setText(String.valueOf(hasil));

            // Tambahkan ke daftar sejarah
            String historyEntry = angkaPertama + " " + getOperationSymbol(operasi) + " " + angkaKedua + " = " + hasil;
            historyList.add(historyEntry);
        } else if (command.equals("%")) {
            angkaKedua = Double.parseDouble(textField.getText());
            hasil = angkaPertama * (angkaKedua / 100);
            textField.setText(String.valueOf(hasil));
            historyList.add(angkaPertama + " % " + angkaKedua + " = " + hasil);
        } else if (command.equals("^2")) {
            angkaPertama = Double.parseDouble(textField.getText());
            hasil = Math.pow(angkaPertama, 2);
            textField.setText(String.valueOf(hasil));
            historyList.add(angkaPertama + "^2 = " + hasil);
        }
    }

    private String getOperationSymbol(int operation) {
        switch (operation) {
            case 1:
                return "+";
            case 2:
                return "-";
            case 3:
                return "*";
            case 4:
                return "/";
            default:
                return "";
        }
    }

    private void showHistory() {
        JTextArea historyTextArea = new JTextArea();
        for (String entry : historyList) {
            historyTextArea.append(entry + "\n");
        }

        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "History", JOptionPane.PLAIN_MESSAGE);
    }

    private void clearLastDigit() {
        String currentText = textField.getText();
        if (!currentText.isEmpty()) {
            textField.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void clearAll() {
        textField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KalkulatorGUI());
    }
}
