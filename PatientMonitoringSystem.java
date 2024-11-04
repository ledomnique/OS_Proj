import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PriorityQueue;

class PatientAlert implements Comparable<PatientAlert> {
    String patientID;
    String alertType;
    int priority;
    int remainingTime;

    public PatientAlert(String patientID, String alertType, int priority, int timeNeeded) {
        this.patientID = patientID;
        this.alertType = alertType;
        this.priority = priority;
        this.remainingTime = timeNeeded;
    }

    @Override
    public int compareTo(PatientAlert other) {
        // Higher priority (lower number) comes first
        return Integer.compare(this.priority, other.priority);
    }
}

public class PatientMonitoringSystem extends JFrame {
    private JTextField patientIDField, alertTypeField, priorityField;
    private JButton addAlertButton, startMonitoringButton;
    private JTextArea outputArea;
    private PriorityQueue<PatientAlert> alertQueue;
    private PatientAlert currentAlert;

    public PatientMonitoringSystem() {
        setTitle("Patient Monitoring System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize alert queue
        alertQueue = new PriorityQueue<>();

        // Top panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Patient ID:"));
        patientIDField = new JTextField();
        inputPanel.add(patientIDField);

        inputPanel.add(new JLabel("Alert Type (e.g., Heart Rate Drop):"));
        alertTypeField = new JTextField();
        inputPanel.add(alertTypeField);

        inputPanel.add(new JLabel("Priority (lower = more urgent):"));
        priorityField = new JTextField();
        inputPanel.add(priorityField);

        addAlertButton = new JButton("Add Alert");
        startMonitoringButton = new JButton("Start Monitoring");
        inputPanel.add(addAlertButton);
        inputPanel.add(startMonitoringButton);

        // Output area for displaying active alerts
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Add action listeners
        addAlertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAlert();
            }
        });

        startMonitoringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMonitoring();
            }
        });
    }

    private void addAlert() {
        String patientID = patientIDField.getText();
        String alertType = alertTypeField.getText();
        int priority = Integer.parseInt(priorityField.getText());

        PatientAlert newAlert = new PatientAlert(patientID, alertType, priority, 5); // 5 seconds as alert time
        alertQueue.offer(newAlert);

        outputArea.append("Added Alert - Patient: " + patientID + ", Alert: " + alertType + ", Priority: " + priority + "\n");

        // Clear input fields
        patientIDField.setText("");
        alertTypeField.setText("");
        priorityField.setText("");

        // Preempt if the new alert has higher priority than the current
        if (currentAlert != null && newAlert.priority < currentAlert.priority) {
            outputArea.append("Preempting current alert for higher priority alert.\n");
            alertQueue.offer(currentAlert); // Return current alert to queue
            currentAlert = null; // Preempt current alert
        }
    }

    private void startMonitoring() {
        if (currentAlert == null && !alertQueue.isEmpty()) {
            outputArea.append("Starting Monitoring...\n");
            processNextAlert();
        }
    }

    private void processNextAlert() {
        if (alertQueue.isEmpty()) {
            outputArea.append("All alerts have been processed.\n");
            return;
        }

        currentAlert = alertQueue.poll();
        outputArea.append("Processing Alert - Patient: " + currentAlert.patientID + ", Alert: " + currentAlert.alertType + "\n");

        // Simulate alert handling with a timer
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentAlert != null) {
                    currentAlert.remainingTime--;
                    if (currentAlert.remainingTime <= 0) {
                        outputArea.append("Alert for Patient " + currentAlert.patientID + " resolved.\n");
                        ((Timer) e.getSource()).stop();
                        currentAlert = null;
                        processNextAlert(); // Process the next alert in the queue
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PatientMonitoringSystem app = new PatientMonitoringSystem();
            app.setVisible(true);
        });
    }
}
