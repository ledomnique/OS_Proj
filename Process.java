import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PriorityQueue;

class Process implements Comparable<Process> {
    String name;
    int burstTime;
    int priority;
    int remainingTime;

    public Process(String name, int burstTime, int priority) {
        this.name = name;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
    }

    @Override
    public int compareTo(Process other) {
        // Higher priority (lower number) comes first
        return Integer.compare(this.priority, other.priority);
    }
}

public class PrioritySchedulerApp extends JFrame {
    private JTextField nameField, burstTimeField, priorityField;
    private JButton addButton, startButton;
    private JTextArea outputArea;
    private PriorityQueue<Process> processQueue;
    private Process currentProcess;

    public PrioritySchedulerApp() {
        setTitle("Preemptive Priority Scheduler");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize process queue
        processQueue = new PriorityQueue<>();

        // Top panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Process Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Burst Time:"));
        burstTimeField = new JTextField();
        inputPanel.add(burstTimeField);

        inputPanel.add(new JLabel("Priority (lower = higher priority):"));
        priorityField = new JTextField();
        inputPanel.add(priorityField);

        addButton = new JButton("Add Process");
        startButton = new JButton("Start Scheduler");
        inputPanel.add(addButton);
        inputPanel.add(startButton);

        // Output area for displaying process execution
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProcess();
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startScheduler();
            }
        });
    }

    private void addProcess() {
        String name = nameField.getText();
        int burstTime = Integer.parseInt(burstTimeField.getText());
        int priority = Integer.parseInt(priorityField.getText());

        Process newProcess = new Process(name, burstTime, priority);
        processQueue.offer(newProcess);

        outputArea.append("Added Process: " + name + " with Burst Time: " + burstTime + " and Priority: " + priority + "\n");

        // Clear input fields
        nameField.setText("");
        burstTimeField.setText("");
        priorityField.setText("");

        // Preempt if the new process has higher priority than the current
        if (currentProcess != null && newProcess.priority < currentProcess.priority) {
            outputArea.append("Preempting " + currentProcess.name + " for higher priority process " + newProcess.name + "\n");
            processQueue.offer(currentProcess);  // Return the current process back to the queue
            currentProcess = null; // Current process is preempted
        }
    }

    private void startScheduler() {
        if (currentProcess == null && !processQueue.isEmpty()) {
            outputArea.append("Starting Scheduler...\n");
            scheduleNextProcess();
        }
    }

    private void scheduleNextProcess() {
        if (processQueue.isEmpty()) {
            outputArea.append("All processes completed.\n");
            return;
        }

        currentProcess = processQueue.poll();
        outputArea.append("Executing Process: " + currentProcess.name + "\n");

        // Simulate burst time decrement (process execution)
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentProcess != null) {
                    currentProcess.remainingTime--;
                    if (currentProcess.remainingTime <= 0) {
                        outputArea.append("Process " + currentProcess.name + " completed.\n");
                        ((Timer) e.getSource()).stop();
                        currentProcess = null;
                        scheduleNextProcess(); // Schedule next process
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PrioritySchedulerApp app = new PrioritySchedulerApp();
            app.setVisible(true);
        });
    }
}
