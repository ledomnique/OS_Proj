import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

public class RoundRobinSchedulerGUI extends JFrame {

    private JTextField arrivalField, burstField, quantumField;
    private JTextArea resultArea;
    private JButton calculateButton;

    public RoundRobinSchedulerGUI() {
        setTitle("Round Robin Scheduler");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Labels and Fields
        inputPanel.add(new JLabel("Arrival Times (e.g. 0 2 4 6 8):"));
        arrivalField = new JTextField();
        inputPanel.add(arrivalField);

        inputPanel.add(new JLabel("Burst Times (e):"));
        burstField = new JTextField();
        inputPanel.add(burstField);

        inputPanel.add(new JLabel("Time Quantum:"));
        quantumField = new JTextField();
        inputPanel.add(quantumField);

        calculateButton = new JButton("Calculate");
        inputPanel.add(new JLabel());
        inputPanel.add(calculateButton);

        add(inputPanel, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Button Action
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RoundRobinWorker().execute();
            }
        });

        setVisible(true);
    }

    private class RoundRobinWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() {
            performRoundRobin();
            return null;
        }
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
        SwingUtilities.invokeLater(() -> new RoundRobinSchedulerGUI());
    }
}
