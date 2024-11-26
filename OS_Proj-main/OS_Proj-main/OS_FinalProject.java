import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class OS_FinalProject extends JFrame {

    private JTextField arrivalField, burstField, quantumField;
    private JTextArea resultArea;

    public OS_FinalProject() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Scheduling Algorithms - OS Finals");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        String[] algorithms = {
            "Select an algorithm",
            "Non-Preemptive Priority Scheduling",
            "Round Robin Scheduling",
            "SSTF Disk Scheduling"
        };

        // Dropdown to select the algorithm
        JComboBox<String> dropdown = new JComboBox<>(algorithms);
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        resultArea = new JTextArea(15, 50);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        dropdown.addActionListener(e -> {
            inputPanel.removeAll();
            resultArea.setText("");

            String selected = (String) dropdown.getSelectedItem();

            // Display input fields based on the selected algorithm
            if ("Non-Preemptive Priority Scheduling".equals(selected)) {
                setupPrioritySchedulingInput(inputPanel);
            } else if ("Round Robin Scheduling".equals(selected)) {
                setupRoundRobinInput(inputPanel);
            } else if ("SSTF Disk Scheduling".equals(selected)) {
                setupSSTFInput(inputPanel);
            }

            inputPanel.revalidate();
            inputPanel.repaint();
        });

        add(dropdown, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Setup input fields for Non-Preemptive Priority Scheduling
    private void setupPrioritySchedulingInput(JPanel inputPanel) {
        inputPanel.add(new JLabel("Number of Processes:"));
        JTextField numProcessesField = new JTextField(10);
        inputPanel.add(numProcessesField);

        inputPanel.add(new JLabel("Burst Times (comma-separated):"));
        JTextField burstField = new JTextField(10);
        inputPanel.add(burstField);

        inputPanel.add(new JLabel("Priorities (comma-separated):"));
        JTextField priorityField = new JTextField(10);
        inputPanel.add(priorityField);

        inputPanel.add(new JLabel("Arrival Times (comma-separated):"));
        JTextField arrivalField = new JTextField(10);
        inputPanel.add(arrivalField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(new JLabel());
        inputPanel.add(computeButton);

        computeButton.addActionListener(ev -> {
            try {
                int numProcesses = Integer.parseInt(numProcessesField.getText().trim());
                int[] burstTimes = parseInput(burstField.getText(), numProcesses);
                int[] priorities = parseInput(priorityField.getText(), numProcesses);
                int[] arrivalTimes = parseInput(arrivalField.getText(), numProcesses);

                String result = performPriorityScheduling(numProcesses, burstTimes, priorities, arrivalTimes);
                resultArea.setText(result);
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });
    }

    private void setupRoundRobinInput(JPanel inputPanel) {
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
        inputPanel.add(new JLabel());
        inputPanel.add(calculateButton);

        calculateButton.addActionListener(ev -> new RoundRobinWorker().execute());
    }

    private void setupSSTFInput(JPanel inputPanel) {
        inputPanel.add(new JLabel("Number of Disk Requests:"));
        JTextField numRequestsField = new JTextField(10);
        inputPanel.add(numRequestsField);

        inputPanel.add(new JLabel("Disk Requests (space-separated):"));
        JTextField requestsField = new JTextField(20);
        inputPanel.add(requestsField);

        inputPanel.add(new JLabel("Initial Head Position:"));
        JTextField headPositionField = new JTextField(10);
        inputPanel.add(headPositionField);

        JButton computeButton = new JButton("Compute");
        inputPanel.add(new JLabel());
        inputPanel.add(computeButton);

        computeButton.addActionListener(ev -> {
            try {
                int numRequests = Integer.parseInt(numRequestsField.getText().trim());
                int[] requests = Arrays.stream(requestsField.getText().split("\\s+"))
                        .mapToInt(Integer::parseInt).toArray();
                int headPosition = Integer.parseInt(headPositionField.getText().trim());

                String result = performSSTF(requests, headPosition);
                resultArea.setText(result);
            } catch (Exception ex) {
                resultArea.setText("Error: " + ex.getMessage());
            }
        });
    }

    private int[] parseInput(String input, int expectedLength) {
        int[] array = Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).toArray();
        if (array.length != expectedLength) {
            throw new IllegalArgumentException("Number of inputs does not match the expected length.");
        }
        return array;
    }

    // Performs the Non-Preemptive Priority Scheduling
    private static String performPriorityScheduling(int numProcesses, int[] burstTimes, int[] priorities, int[] arrivalTimes) {
        String[] processes = new String[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            processes[i] = "P" + (i + 1);
        }

        // Arrays to store results
        int[] completionTime = new int[numProcesses];
        int[] waitingTime = new int[numProcesses];
        int[] turnaroundTime = new int[numProcesses];

        // Array to keep track of visited processes
        boolean[] isCompleted = new boolean[numProcesses];
        int currentTime = 0, completed = 0;

        while (completed < numProcesses) {
            int idx = -1; // Index of the next process to execute
            int minPriority = Integer.MAX_VALUE;

            for (int i = 0; i < numProcesses; i++) {
                // Select the process with the highest priority (smallest priority value)
                // that has arrived and is not completed
                if (!isCompleted[i] && arrivalTimes[i] <= currentTime && priorities[i] < minPriority) {
                    minPriority = priorities[i];
                    idx = i;
                }
            }

            if (idx != -1) {
                // Execute the selected process
                currentTime = Math.max(currentTime, arrivalTimes[idx]);
                completionTime[idx] = currentTime + burstTimes[idx];
                turnaroundTime[idx] = completionTime[idx] - arrivalTimes[idx];
                waitingTime[idx] = turnaroundTime[idx] - burstTimes[idx];

                // Mark as completed
                isCompleted[idx] = true;
                completed++;
                currentTime += burstTimes[idx];
            } else {
                // If no process is ready, increment time (idle state)
                currentTime++;
            }
        }

        // Generates output in tabular format
        StringBuilder result = new StringBuilder();
        result.append(String.format("%-10s%-10s%-10s%-10s%-10s%-10s%n", "Priority", "Process", "Arrival", "BT", "WT", "TAT"));

        int totalWT = 0, totalTAT = 0;
        for (int i = 0; i < numProcesses; i++) {
            result.append(String.format(
                    "%-10d%-10s%-10d%-10d%-10d%-10d%n",
                    priorities[i], processes[i], arrivalTimes[i], burstTimes[i], waitingTime[i], turnaroundTime[i]
            ));
            totalWT += waitingTime[i];
            totalTAT += turnaroundTime[i];
        }

        result.append("\nAverage Waiting Time: ").append(String.format("%.2f", (double) totalWT / numProcesses));
        result.append("\nAverage Turnaround Time: ").append(String.format("%.2f", (double) totalTAT / numProcesses));

        return result.toString();
    }
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private String performSSTF(int[] requests, int headPosition) {
        StringBuilder result = new StringBuilder("SSTF Disk Scheduling Execution Log:\n");

        ArrayList<Integer> requestList = new ArrayList<>();
        for (int req : requests) requestList.add(req);

        int totalMovement = 0;
        ArrayList<Integer> sequence = new ArrayList<>();

        // SSTF algorithm implementation starts here:
        while (!requestList.isEmpty()) {
            int closestRequest = requestList.get(0);
            int minDistance = Math.abs(closestRequest - headPosition);

            // Find the closest request to the current head position
            for (int req : requestList) {
                int distance = Math.abs(req - headPosition);
                if (distance < minDistance) {
                    closestRequest = req;
                    minDistance = distance;
                }
            }

            // Process the closest request
            sequence.add(closestRequest);
            totalMovement += minDistance;
            headPosition = closestRequest;
            
            // Remove the processed request
            requestList.remove(Integer.valueOf(closestRequest));
        }
        // SSTF algorithm implementation ends here

        result.append("Execution Sequence: ").append(sequence).append("\n");
        result.append("Total Head Movement: ").append(totalMovement).append("\n");

        return result.toString();
    }

    private void performRoundRobin() {
        try {
            // Parse input from text fields
            String[] arrivalInput = arrivalField.getText().trim().split("\\s+");
            String[] burstInput = burstField.getText().trim().split("\\s+");
            int quantum = Integer.parseInt(quantumField.getText().trim());

            if (arrivalInput.length != burstInput.length) {
                throw new IllegalArgumentException("Mismatched arrival and burst times.");
            }

            int n = arrivalInput.length; // Number of processes
            int[] arrival = Arrays.stream(arrivalInput).mapToInt(Integer::parseInt).toArray();
            int[] burst = Arrays.stream(burstInput).mapToInt(Integer::parseInt).toArray();
            int[] remainingBurst = Arrays.copyOf(burst, burst.length);

            // Initialize arrays to store results
            int[] waitingTime = new int[n];
            int[] turnaroundTime = new int[n];
            boolean[] completed = new boolean[n];

            int time = 0; // Current time
            Queue<Integer> queue = new LinkedList<>();

            // Start with processes that have arrived at time 0
            for (int i = 0; i < n; i++) {
                if (arrival[i] == 0) {
                    queue.add(i);
                }
            }

            while (!queue.isEmpty()) {
                int current = queue.poll();

                // Execute the current process for a quantum or its remaining time
                if (remainingBurst[current] <= quantum) {
                    time += remainingBurst[current];
                    remainingBurst[current] = 0;
                    completed[current] = true;

                    // Calculate waiting and turnaround times
                    waitingTime[current] = time - arrival[current] - burst[current];
                    turnaroundTime[current] = time - arrival[current];
                } else {
                    time += quantum;
                    remainingBurst[current] -= quantum;
                }

                // Add newly arrived processes to the queue
                for (int i = 0; i < n; i++) {
                    if (!completed[i] && arrival[i] <= time && !queue.contains(i) && i != current) {
                        queue.add(i);
                    }
                }

                // Re-add the current process to the queue if it's not completed
                if (!completed[current]) {
                    queue.add(current);
                }
            }

            // Build the result
            StringBuilder result = new StringBuilder();
            result.append(String.format("%-10s%-10s%-10s%-10s%-10s%n", "Process", "Arrival", "Burst", "Waiting", "Turnaround"));

            int totalWT = 0, totalTAT = 0;
            for (int i = 0; i < n; i++) {
                result.append(String.format("%-10s%-10d%-10d%-10d%-10d%n", "P" + (i + 1), arrival[i], burst[i], waitingTime[i], turnaroundTime[i]));
                totalWT += waitingTime[i];
                totalTAT += turnaroundTime[i];
            }

            result.append("\nAverage Waiting Time: ").append(String.format("%.2f", (double) totalWT / n));
            result.append("\nAverage Turnaround Time: ").append(String.format("%.2f", (double) totalTAT / n));

            resultArea.setText(result.toString());
        } catch (Exception e) {
            resultArea.setText("Error: " + e.getMessage());
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
