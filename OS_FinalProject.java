import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

public class OS_FinalProject extends JFrame {

    private JTextField arrivalField, burstField, quantumField;
    private JTextArea resultArea;
    private JButton calculateButton;

    private void performRoundRobin() {
        try {
            String[] arrivalInput = arrivalField.getText().split("\\s+");
            String[] burstInput = burstField.getText().split("\\s+");

            if (arrivalInput.length != burstInput.length) {
                resultArea.setText("Error: Arrival and Burst times must have the same number of entries.");
                return;
            }

            int n = arrivalInput.length;
            int[][] processes = new int[n][2]; // To store [arrivalTime, burstTime]

            for (int i = 0; i < n; i++) {
                processes[i][0] = Integer.parseInt(arrivalInput[i].trim());
                processes[i][1] = Integer.parseInt(burstInput[i].trim());
            }

            // Sort processes based on arrival time
            Arrays.sort(processes, Comparator.comparingInt(o -> o[0]));

            // Extract sorted arrival and burst times
            int[] arrivalTime = new int[n];
            int[] burstTime = new int[n];
            int[] remainingTime = new int[n];
            int[] completionTime = new int[n];
            int[] waitingTime = new int[n];
            int[] turnaroundTime = new int[n];

            for (int i = 0; i < n; i++) {
                arrivalTime[i] = processes[i][0];
                burstTime[i] = processes[i][1];
                remainingTime[i] = burstTime[i];
            }

            int timeQuantum = Integer.parseInt(quantumField.getText().trim());
            int currentTime = 0, completed = 0;
            boolean[] isInQueue = new boolean[n];
            java.util.Queue<Integer> queue = new java.util.LinkedList<>();

            while (completed < n) {
                // Add processes that have arrived to the queue
                for (int i = 0; i < n; i++) {
                    if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                        queue.add(i);
                        isInQueue[i] = true;
                    }
                }

                if (!queue.isEmpty()) {
                    int process = queue.poll();
                    if (remainingTime[process] > timeQuantum) {
                        currentTime += timeQuantum;
                        remainingTime[process] -= timeQuantum;
                    } else {
                        currentTime += remainingTime[process];
                        remainingTime[process] = 0;
                        completionTime[process] = currentTime;
                        completed++;
                    }

                    // Add processes that have arrived during this time
                    for (int i = 0; i < n; i++) {
                        if (!isInQueue[i] && arrivalTime[i] <= currentTime) {
                            queue.add(i);
                            isInQueue[i] = true;
                        }
                    }

                    // Add the process back to the queue if it's not completed
                    if (remainingTime[process] > 0) {
                        queue.add(process);
                    }
                } else {
                    currentTime++;
                }
            }

            // Calculate turnaround and waiting times
            int totalTurnaroundTime = 0;
            int totalWaitingTime = 0;

            StringBuilder output = new StringBuilder("Process\tAT\tBT\tCT\tTAT\tWT\n");
            for (int i = 0; i < n; i++) {
                turnaroundTime[i] = completionTime[i] - arrivalTime[i];
                waitingTime[i] = turnaroundTime[i] - burstTime[i];

                totalTurnaroundTime += turnaroundTime[i];
                totalWaitingTime += waitingTime[i];

                output.append(String.format("%d\t%d\t%d\t%d\t%d\t%d\n",
                        i + 1, arrivalTime[i], burstTime[i], completionTime[i], turnaroundTime[i], waitingTime[i]));
            }

            // Calculate averages
            double avgTurnaroundTime = (double) totalTurnaroundTime / n;
            double avgWaitingTime = (double) totalWaitingTime / n;

            // Append averages to the output
            output.append("\nAverage Turnaround Time: ").append(String.format("%.2f", avgTurnaroundTime));
            output.append("\nAverage Waiting Time: ").append(String.format("%.2f", avgWaitingTime));

            resultArea.setText(output.toString());

        } catch (NumberFormatException ex) {
            resultArea.setText("Error: Please enter valid numeric values.");
        } catch (Exception ex) {
            resultArea.setText("Error: An unexpected error occurred.");
        }
    }
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the main frame
        JFrame frame = new JFrame("Scheduling Algorithms - OS Finals");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
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
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Top: 10px, Left: 0px, Bottom: 0px, Right: 0px
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10)); // Grid layout for inputs

        // Create a panel for results
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); // Monospaced font for alignment
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
                    //Label for process
                    JLabel titleLabel = new JLabel("Enter Data for NPP Scheduling");
                    titleLabel.setFont(new Font("Monospaced",Font.BOLD, 12));
                    titleLabel.setForeground(Color.BLUE); // Set font and size
                    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the title horizontally
                    // Add the title label to the panel
                    inputPanel.add(titleLabel);
                    // Add some spacing below the title
                    inputPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 10px vertical space

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
                    computeButton.setPreferredSize(new Dimension(150, 30)); // Set the button width
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
                } else if (selected != null && selected.equals("Round Robin Scheduling")) {
                    JLabel titleLabel = new JLabel("Enter Data for Round Robin Scheduling");
                    titleLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
                    titleLabel.setForeground(Color.BLUE);
                    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    inputPanel.add(titleLabel);
                    inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));

                    // Input fields for Round Robin
                    JTextField arrivalField = new JTextField(10);
                    JTextField burstField = new JTextField(10);
                    JTextField quantumField = new JTextField(10);
                    JButton calculateButton = new JButton("Calculate");

                    inputPanel.add(new JLabel("Arrival Times (e.g. 0 2 4 6 8):"));
                    inputPanel.add(arrivalField);

                    inputPanel.add(new JLabel("Burst Times (e.g. 5 3 8 6):"));
                    inputPanel.add(burstField);

                    inputPanel.add(new JLabel("Time Quantum:"));
                    inputPanel.add(quantumField);

                    inputPanel.add(new JLabel()); // Empty label for spacing
                    inputPanel.add(calculateButton);

                    // Action listener for Calculate button
                    calculateButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new RoundRobinWorker().execute();
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

        SwingUtilities.invokeLater(() -> new RoundRobinSchedulerGUI());

        
    }

    private class RoundRobinWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            performRoundRobin();
            return null;
        }
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
