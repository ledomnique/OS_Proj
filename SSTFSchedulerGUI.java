import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class SSTFSchedulerGUI extends JFrame {
    private JTextField numRequestsField;
    private ArrayList<JTextField> requestFields;
    private JLabel statusLabel;
    private JTextArea logTextArea;
    private JPanel requestInputPanel;
    private JButton setRequestsButton;
    private JButton startButton;
    private JButton clearLogButton;

    public SSTFSchedulerGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        setTitle("SSTF Disk Scheduling");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JLabel numRequestsLabel = new JLabel("Enter number of disk requests: ");
        numRequestsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        numRequestsField = new JTextField(5);
        numRequestsField.setFont(new Font("Arial", Font.PLAIN, 14));
        setRequestsButton = new JButton("Set Requests");
        setRequestsButton.setFont(new Font("Arial", Font.BOLD, 14));
        setRequestsButton.setBackground(new Color(0x4CAF50));
        setRequestsButton.setForeground(Color.WHITE);
        topPanel.add(numRequestsLabel);
        topPanel.add(numRequestsField);
        topPanel.add(setRequestsButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        requestInputPanel = new JPanel(new GridBagLayout());
        requestInputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Disk Request Inputs"));
        requestInputPanel.setBackground(new Color(0xF0F0F0));

        logTextArea = new JTextArea(10, 40);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logTextArea.setLineWrap(true);
        logTextArea.setWrapStyleWord(true);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Execution Log"));
        logScrollPane.setBackground(new Color(0xF0F0F0));

        centerPanel.add(new JScrollPane(requestInputPanel), BorderLayout.NORTH);
        centerPanel.add(logScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statusLabel = new JLabel("Status: Idle");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.BLACK);
        startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(0x2196F3));
        startButton.setForeground(Color.WHITE);
        clearLogButton = new JButton("Clear Log");
        clearLogButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearLogButton.setBackground(new Color(0xF44336));
        clearLogButton.setForeground(Color.WHITE);

        bottomPanel.add(statusLabel);
        bottomPanel.add(startButton);
        bottomPanel.add(clearLogButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setRequestsButton.addActionListener(new SetRequestsListener());
        startButton.addActionListener(new StartButtonListener());
        clearLogButton.addActionListener(new ClearLogListener());

        setVisible(true);
    }

    private void createRequestFields(int numRequests) {
        requestInputPanel.removeAll();
        requestFields = new ArrayList<>();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 1; i <= numRequests; i++) {
            gbc.gridx = 0;
            gbc.gridy = i - 1;
            requestInputPanel.add(new JLabel("Request " + i + ":"), gbc);

            gbc.gridx = 1;
            JTextField field = new JTextField(5);
            requestFields.add(field);
            requestInputPanel.add(field, gbc);
        }

        revalidate();
        repaint();
    }

    private class SetRequestsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int numRequests = Integer.parseInt(numRequestsField.getText());
                if (numRequests < 2 || numRequests > 9) {
                    JOptionPane.showMessageDialog(SSTFSchedulerGUI.this, "Please enter a number between 2 and 9.");
                    return;
                }
                createRequestFields(numRequests);
                logTextArea.append("Set " + numRequests + " request fields.\n");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SSTFSchedulerGUI.this,
                        "Invalid number of requests. Please enter a valid number.");
            }
        }
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ArrayList<Integer> requests = new ArrayList<>();
            try {
                for (JTextField field : requestFields) {
                    requests.add(Integer.parseInt(field.getText()));
                }

                String headInput = JOptionPane.showInputDialog(SSTFSchedulerGUI.this,
                        "Enter the initial head position:");
                int headPosition = Integer.parseInt(headInput);
                if (headPosition < 0 || headPosition > 199) {
                    throw new NumberFormatException("Head position must be between 0 and 199.");
                }

                performSSTF(requests, headPosition);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(SSTFSchedulerGUI.this, "Invalid input. Please enter valid integers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(SSTFSchedulerGUI.this, "Unexpected error occurred: " + ex.getMessage());
            }
        }
    }

    private void performSSTF(ArrayList<Integer> requests, int headPosition) {
        logTextArea.append("Starting SSTF Disk Scheduling...\n");

        ArrayList<Integer> sequence = new ArrayList<>();
        int totalMovement = 0;

        ArrayList<Integer> remainingRequests = new ArrayList<>(requests);

        while (!remainingRequests.isEmpty()) {
            int closestRequest = remainingRequests.get(0);
            int minDistance = Math.abs(closestRequest - headPosition);

            for (int request : remainingRequests) {
                int distance = Math.abs(request - headPosition);
                if (distance < minDistance) {
                    closestRequest = request;
                    minDistance = distance;
                }
            }

            sequence.add(closestRequest);
            totalMovement += minDistance;
            headPosition = closestRequest;

            remainingRequests.remove(Integer.valueOf(closestRequest));
        }

        logTextArea.append("Execution Sequence: " + sequence + "\n");
        logTextArea.append("Total Head Movement: " + totalMovement + "\n");
        logTextArea.append("SSTF Scheduling Complete.\n\n");
        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
    }

    private class ClearLogListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            logTextArea.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SSTFSchedulerGUI::new);
    }
}