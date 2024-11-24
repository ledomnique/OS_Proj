import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class OS_SchedulerGUI extends JFrame {
    private JComboBox<String> algorithmSelector;
    private JPanel inputPanel;
    private JTextField numRequestsField;
    private JTextArea logTextArea;
    private JTextArea resultsArea; // Results container
    private JButton executeButton, clearLogButton;
    private ArrayList<JTextField> requestFields;

    public OS_SchedulerGUI() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("CPU And Disk Scheduling Algorithms");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Top Panel with Algorithm Selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Select Scheduling Algorithm:"));

        algorithmSelector = new JComboBox<>(new String[]{
                "SSTF Disk Scheduling",
                "Non-Preemptive Priority Scheduling",
                "Round Robin Scheduling"
        });
        topPanel.add(algorithmSelector);

        JLabel numRequestsLabel = new JLabel("Number of Requests/Processes:");
        topPanel.add(numRequestsLabel);
        numRequestsField = new JTextField(5);
        topPanel.add(numRequestsField);

        JButton setRequestsButton = new JButton("Set Inputs");
        topPanel.add(setRequestsButton);

        add(topPanel, BorderLayout.NORTH);

        // Input Panel for Requests/Processes
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Area"));
        add(new JScrollPane(inputPanel), BorderLayout.CENTER);

        // Bottom Panel with Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        logTextArea = new JTextArea(15, 70);
        logTextArea.setEditable(false);
        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane logScrollPane = new JScrollPane(logTextArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Execution Log"));
        add(logScrollPane, BorderLayout.SOUTH);

        resultsArea = new JTextArea(10, 40); // Results container
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultsArea.setEditable(false);
        JScrollPane resultsScrollPane = new JScrollPane(resultsArea);
        resultsScrollPane.setBorder(BorderFactory.createTitledBorder("Execution Log"));

        executeButton = new JButton("Execute");
        clearLogButton = new JButton("Clear Log");
        bottomPanel.add(executeButton);
        bottomPanel.add(clearLogButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
        setRequestsButton.addActionListener(e -> setInputFields());
        executeButton.addActionListener(e -> executeSelectedAlgorithm());
        clearLogButton.addActionListener(e -> logTextArea.setText(""));

        setVisible(true);
    }

    private void setInputFields() {
        try {
            int numRequests = Integer.parseInt(numRequestsField.getText());
            if (numRequests < 2 || numRequests > 9) {
                JOptionPane.showMessageDialog(this, "Please enter a number between 2 and 9.");
                return;
            }

            inputPanel.removeAll();
            requestFields = new ArrayList<>();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            for (int i = 1; i <= numRequests; i++) {
                gbc.gridx = 0;
                gbc.gridy = i - 1;
                inputPanel.add(new JLabel("Input " + i + ":"), gbc);

                gbc.gridx = 1;
                JTextField field = new JTextField(10);
                requestFields.add(field);
                inputPanel.add(field, gbc);
            }

            revalidate();
            repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number of requests.");
        }
    }

    private void executeSelectedAlgorithm() {
        String selectedAlgorithm = (String) algorithmSelector.getSelectedItem();
        ArrayList<Integer> inputs = new ArrayList<>();
        try {
            for (JTextField field : requestFields) {
                inputs.add(Integer.parseInt(field.getText()));
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric inputs.");
            return;
        }

        logTextArea.append("Executing " + selectedAlgorithm + "...\n");
        switch (selectedAlgorithm) {
            case "SSTF Disk Scheduling":
                executeSSTF(inputs);
                break;
            case "Non-Preemptive Priority Scheduling":
                executePriorityScheduling(inputs);
                break;
            case "Round Robin Scheduling":
                executeRoundRobin(inputs);
                break;
        }
    }

    private void executeSSTF(ArrayList<Integer> requests) {
        String headInput = JOptionPane.showInputDialog(this, "Enter the initial head position:");
        int headPosition;
        try {
            headPosition = Integer.parseInt(headInput);
            if (headPosition < 0 || headPosition > 199) {
                throw new NumberFormatException("Head position must be between 0 and 199.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid head position.");
            return;
        }

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

        logTextArea.append("SSTF Execution Sequence: " + sequence + "\n");
        logTextArea.append("Total Head Movement: " + totalMovement + "\n\n");
    }

    private void executePriorityScheduling(ArrayList<Integer> priorities) {
        int n = priorities.size();
        ArrayList<Integer> burstTimes = new ArrayList<>();
        ArrayList<Integer> processIds = new ArrayList<>();
    
        // Input burst times for processes
        for (int i = 1; i <= n; i++) {
            String burstTimeInput = JOptionPane.showInputDialog(this, 
                "Enter Burst Time for Process " + i + " (Priority: " + priorities.get(i - 1) + "):");
            try {
                int burstTime = Integer.parseInt(burstTimeInput);
                burstTimes.add(burstTime);
                processIds.add(i); // Assigning process IDs starting from 1
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid burst time for Process " + i);
                return;
            }
        }
    
        // Sorting by priority (low number = high priority)
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (priorities.get(j) > priorities.get(j + 1)) {
                    // Swap priorities
                    int tempPriority = priorities.get(j);
                    priorities.set(j, priorities.get(j + 1));
                    priorities.set(j + 1, tempPriority);
    
                    // Swap burst times
                    int tempBurst = burstTimes.get(j);
                    burstTimes.set(j, burstTimes.get(j + 1));
                    burstTimes.set(j + 1, tempBurst);
    
                    // Swap process IDs
                    int tempId = processIds.get(j);
                    processIds.set(j, processIds.get(j + 1));
                    processIds.set(j + 1, tempId);
                }
            }
        }
    
        // Calculating waiting time and turnaround time
        int[] waitingTimes = new int[n];
        int[] turnaroundTimes = new int[n];
    
        waitingTimes[0] = 0; // First process has no waiting time
        for (int i = 1; i < n; i++) {
            waitingTimes[i] = waitingTimes[i - 1] + burstTimes.get(i - 1);
        }
    
        for (int i = 0; i < n; i++) {
            turnaroundTimes[i] = waitingTimes[i] + burstTimes.get(i);
        }
    
        // Output results
        logTextArea.append("Non-Preemptive Priority Scheduling Results:\n");
        logTextArea.append(String.format("%-10s%-15s%-15s%-15s%-15s\n", "Process", "Priority", "Burst Time", "Waiting Time", "Turnaround Time"));
        for (int i = 0; i < n; i++) {
            logTextArea.append(String.format("%-10d%-15d%-15d%-15d%-15d\n", processIds.get(i), priorities.get(i), burstTimes.get(i), waitingTimes[i], turnaroundTimes[i]));
        }
    
        int totalWaitingTime = Arrays.stream(waitingTimes).sum();
        int totalTurnaroundTime = Arrays.stream(turnaroundTimes).sum();
        logTextArea.append("\nAverage Waiting Time: " + (totalWaitingTime / (float) n) + "\n");
        logTextArea.append("Average Turnaround Time: " + (totalTurnaroundTime / (float) n) + "\n\n");
    }
    

    private void executeRoundRobin(ArrayList<Integer> inputs) {
        String quantumInput = JOptionPane.showInputDialog(this, "Enter the time quantum:");
        int quantum;
        try {
            quantum = Integer.parseInt(quantumInput);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time quantum.");
            return;
        }

        logTextArea.append("Round Robin Scheduling is under development.\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OS_SchedulerGUI::new);
    }
}
