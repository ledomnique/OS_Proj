import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class OS_FinalProject extends JFrame {

    private JTextField arrivalField, burstField, quantumField;
    private JTextArea resultArea;

    public OS_FinalProject() {
        // Set Nimbus Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        setTitle("Scheduling Algorithms - OS Finals");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());

        // Create a dropdown menu for algorithms
        String[] algorithms = {
                "Select an algorithm",
                "Non-Preemptive Priority Scheduling",
                "Round Robin Scheduling",
                "SSTF Disk Scheduling"
        };
        JComboBox<String> dropdown = new JComboBox<>(algorithms);

        // Create a panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));

        // Create a panel for results
        resultArea = new JTextArea(15, 50);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add action listener for the dropdown menu
        dropdown.addActionListener(e -> {
            // Clear the input panel and results area
            inputPanel.removeAll();
            resultArea.setText("");

            // Get the selected algorithm
            String selected = (String) dropdown.getSelectedItem();

            if (selected != null && selected.equals("Non-Preemptive Priority Scheduling")) {
                // Non-Preemptive Priority Scheduling
                inputPanel.add(new JLabel("Number of Processes:"));
                JTextField numProcessesField = new JTextField(10);
                inputPanel.add(numProcessesField);

                inputPanel.add(new JLabel("Burst Times (comma-separated):"));
                JTextField burstTimesField = new JTextField(10);
                inputPanel.add(burstTimesField);

                inputPanel.add(new JLabel("Priorities (comma-separated):"));
                JTextField prioritiesField = new JTextField(10);
                inputPanel.add(prioritiesField);

                inputPanel.add(new JLabel("Arrival Times (comma-separated):"));
                JTextField arrivalTimesField = new JTextField(10);
                inputPanel.add(arrivalTimesField);

                JButton computeButton = new JButton("Compute");
                inputPanel.add(new JLabel()); // Empty label for spacing
                inputPanel.add(computeButton);

                // Action listener for Compute button
                computeButton.addActionListener(ev -> {
                    try {
                        int numProcesses = Integer.parseInt(numProcessesField.getText().trim());
                        int[] burstTimes = Arrays.stream(burstTimesField.getText().split(","))
                                .mapToInt(Integer::parseInt).toArray();
                        int[] priorities = Arrays.stream(prioritiesField.getText().split(","))
                                .mapToInt(Integer::parseInt).toArray();
                        int[] arrivalTimes = Arrays.stream(arrivalTimesField.getText().split(","))
                                .mapToInt(Integer::parseInt).toArray();

                        if (burstTimes.length != numProcesses || priorities.length != numProcesses || arrivalTimes.length != numProcesses) {
                            throw new IllegalArgumentException("Input sizes do not match the number of processes.");
                        }

                        String result = performPriorityScheduling(numProcesses, burstTimes, priorities, arrivalTimes);
                        resultArea.setText(result);
                    } catch (Exception ex) {
                        resultArea.setText("Error: " + ex.getMessage());
                    }
                });

            } else if (selected != null && selected.equals("Round Robin Scheduling")) {
                // Round Robin Scheduling
                inputPanel.add(new JLabel("Arrival Times (space-separated):"));
                arrivalField = new JTextField(10);
                inputPanel.add(arrivalField);

                inputPanel.add(new JLabel("Burst Times (space-separated):"));
                burstField = new JTextField(10);
                inputPanel.add(burstField);

                inputPanel.add(new JLabel("Time Quantum:"));
                quantumField = new JTextField(10);
                inputPanel.add(quantumField);

                JButton calculateButton = new JButton("Calculate");
                inputPanel.add(new JLabel()); // Empty label for spacing
                inputPanel.add(calculateButton);

                // Action listener for Calculate button
                calculateButton.addActionListener(ev -> new RoundRobinWorker().execute());
            }

            // Repaint and revalidate the input panel to show updates
            inputPanel.revalidate();
            inputPanel.repaint();
        });
        
        

        // Add components to the frame
        add(dropdown, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }

    private static String performPriorityScheduling(int numProcesses, int[] burstTimes, int[] priorities, int[] arrivalTimes) {
        String[] processes = new String[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            processes[i] = "P" + (i + 1);
        }

        // Sort by priority
        for (int i = 0; i < numProcesses - 1; i++) {
            for (int j = 0; j < numProcesses - 1; j++) {
                if (priorities[j] > priorities[j + 1]) {
                    // Swap priorities
                    int temp = priorities[j];
                    priorities[j] = priorities[j + 1];
                    priorities[j + 1] = temp;

                    // Swap burst times
                    temp = burstTimes[j];
                    burstTimes[j] = burstTimes[j + 1];
                    burstTimes[j + 1] = temp;

                    // Swap arrival times
                    temp = arrivalTimes[j];
                    arrivalTimes[j] = arrivalTimes[j + 1];
                    arrivalTimes[j + 1] = temp;

                    // Swap processes
                    String tempProcess = processes[j];
                    processes[j] = processes[j + 1];
                    processes[j + 1] = tempProcess;
                }
            }
        }

        int[] waitingTime = new int[numProcesses];
        int[] tat = new int[numProcesses];
        int completionTime = 0;

        for (int i = 0; i < numProcesses; i++) {
            if (completionTime < arrivalTimes[i]) {
                completionTime = arrivalTimes[i];
            }

            waitingTime[i] = completionTime - arrivalTimes[i];
            tat[i] = waitingTime[i] + burstTimes[i];
            completionTime += burstTimes[i];
        }

        // Generate result string
        StringBuilder result = new StringBuilder();
        result.append(String.format("%-10s%-10s%-10s%-10s%-10s%-10s%n", "Priority", "Process", "Arrival", "BT", "WT", "TAT"));

        int totalWT = 0, totalTAT = 0;

        for (int i = 0; i < numProcesses; i++) {
            result.append(String.format(
                    "%-10d%-10s%-10d%-10d%-10d%-10d%n",
                    priorities[i], processes[i], arrivalTimes[i], burstTimes[i], waitingTime[i], tat[i]
            ));
            totalWT += waitingTime[i];
            totalTAT += tat[i];
        }

        result.append("\nAverage Waiting Time: ").append(String.format("%.2f", (double) totalWT / numProcesses));
        result.append("\nAverage Turnaround Time: ").append(String.format("%.2f", (double) totalTAT / numProcesses));

        return result.toString();
    }

    private void performRoundRobin() {
        try {
            String[] arrivalInput = arrivalField.getText().split("\\s+");
            String[] burstInput = burstField.getText().split("\\s+");

            if (arrivalInput.length != burstInput.length) {
                resultArea.setText("Error: Arrival and Burst times must have the same number of entries.");
                return;
            }

            int n = arrivalInput.length;
            int[] arrivalTime = new int[n];
            int[] burstTime = new int[n];
            int[] remainingTime = new int[n];
            int[] completionTime = new int[n];
            int[] waitingTime = new int[n];
            int[] turnaroundTime = new int[n];

            for (int i = 0; i < n; i++) {
                arrivalTime[i] = Integer.parseInt(arrivalInput[i]);
                burstTime[i] = Integer.parseInt(burstInput[i]);
                remainingTime[i] = burstTime[i];
            }

            int timeQuantum = Integer.parseInt(quantumField.getText().trim());
            int currentTime = 0, completed = 0;
            Queue<Integer> queue = new LinkedList<>();
            boolean[] isInQueue = new boolean[n];

            while (completed < n) {
                for (int i = 0; i < n; i++) {
                    if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                        queue.add(i);
                        isInQueue[i] = true;
                    }
                }

                if (!queue.isEmpty()) {
                    int process = queue.poll();
                    if (remainingTime[process] <= timeQuantum) {
                        currentTime += remainingTime[process];
                        completionTime[process] = currentTime;
                        waitingTime[process] = currentTime - arrivalTime[process] - burstTime[process];
                        turnaroundTime[process] = currentTime - arrivalTime[process];
                        remainingTime[process] = 0;
                        completed++;
                    } else {
                        currentTime += timeQuantum;
                        remainingTime[process] -= timeQuantum;
                        for (int i = 0; i < n; i++) {
                            if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                                queue.add(i);
                                isInQueue[i] = true;
                            }
                        }
                        queue.add(process);
                    }
                } else {
                    currentTime++;
                }
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("%-10s%-10s%-10s%-10s%-10s%-10s%n", "Process", "AT", "BT", "CT", "WT", "TAT"));

            int totalWT = 0, totalTAT = 0;
            for (int i = 0; i < n; i++) {
                result.append(String.format(
                        "%-10s%-10d%-10d%-10d%-10d%-10d%n",
                        "P" + (i + 1), arrivalTime[i], burstTime[i], completionTime[i], waitingTime[i], turnaroundTime[i]
                ));
                totalWT += waitingTime[i];
                totalTAT += turnaroundTime[i];
            }

            result.append("\nAverage Waiting Time: ").append(String.format("%.2f", (double) totalWT / n));
            result.append("\nAverage Turnaround Time: ").append(String.format("%.2f", (double) totalTAT / n));

            resultArea.setText(result.toString());
        } catch (NumberFormatException ex) {
            resultArea.setText("Error: Please enter valid numeric values.");
        } catch (Exception ex) {
            resultArea.setText("Error: An unexpected error occurred.");
        }
    }

    private class RoundRobinWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            performRoundRobin();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OS_FinalProject::new);
    }
}
