import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class OS_FinalProject {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("CPU Scheduling Algorithms");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Create a dropdown menu for algorithms
        String[] algorithms = {
            "Select an algorithm",
            "Non-Preemptive Priority Scheduling",
            "Round Robin Scheduling",
            "Shortest Seek Time First Scheduling"
        };
        JComboBox<String> dropdown = new JComboBox<>(algorithms);

        // Create a panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Grid layout for inputs

        // Create a panel for results
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced font for alignment
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Add action listener for the dropdown menu
        dropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the input panel and results area
                inputPanel.removeAll();
                resultArea.setText("");

                // Get the selected algorithm
                String selected = (String) dropdown.getSelectedItem();

                if (selected != null && selected.equals("Non-Preemptive Priority Scheduling")) {
                    // Input fields for Non-Preemptive Priority Scheduling
                    JTextField numProcessesField = new JTextField(10);
                    JTextField burstTimesField = new JTextField(10);
                    JTextField prioritiesField = new JTextField(10);
                    JTextField arrivalTimesField = new JTextField(10);

                    inputPanel.add(new JLabel("Number of Processes:"));
                    inputPanel.add(numProcessesField);
                    inputPanel.add(new JLabel("Burst Times (comma-separated):"));
                    inputPanel.add(burstTimesField);
                    inputPanel.add(new JLabel("Priorities (comma-separated):"));
                    inputPanel.add(prioritiesField);
                    inputPanel.add(new JLabel("Arrival Times (comma-separated):"));
                    inputPanel.add(arrivalTimesField);

                    JButton computeButton = new JButton("Compute");
                    inputPanel.add(computeButton);

                    // Action listener for Compute button
                    computeButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
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

                                // Perform scheduling computation
                                String result = performPriorityScheduling(numProcesses, burstTimes, priorities, arrivalTimes);
                                resultArea.setText(result);
                            } catch (Exception ex) {
                                resultArea.setText("Error: " + ex.getMessage());
                            }
                        }
                    });
                }

                // Repaint and revalidate the input panel to show updates
                inputPanel.revalidate();
                inputPanel.repaint();
            }
        });

        // Add components to the frame
        frame.add(dropdown, BorderLayout.NORTH);
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(scrollPane, BorderLayout.SOUTH);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Logic for Non-Preemptive Priority Scheduling
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
    
        // Use a monospaced font for better alignment
        result.append(String.format("%-10s%-10s%-10s%-10s%-10s%-10s%n", "Priority", "Process", "Arrival", "BT", "WT", "TAT"));
        result.append(String.format("%-10s%-10s%-10s%-10s%-10s%-10s%n", "--------", "-------", "-------", "--", "--", "---"));
    
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
    
    
}
