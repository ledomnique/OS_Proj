import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryManagementApp extends JFrame {
    
    private DefaultListModel<String> taskListModel;
    private DefaultListModel<String> requestListModel;
    private DefaultListModel<String> diskListModel;

    private JTextField taskField;
    private JTextField priorityField;
    private JTextField requestField;
    private JTextField trackField;
    private JTextField currentHeadField;

    public LibraryManagementApp() {
        setTitle("Library Management System Scheduler");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        // Task Scheduling Panel (Priority Scheduling)
        JPanel taskPanel = new JPanel(new BorderLayout());
        taskPanel.setBorder(BorderFactory.createTitledBorder("Priority Task Scheduling"));
        taskListModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(taskListModel);
        taskPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel taskInputPanel = new JPanel();
        taskField = new JTextField(10);
        priorityField = new JTextField(5);
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new AddTaskListener());
        JButton processTasksButton = new JButton("Process Tasks");
        processTasksButton.addActionListener(new ProcessTaskListener());
        taskInputPanel.add(new JLabel("Task:"));
        taskInputPanel.add(taskField);
        taskInputPanel.add(new JLabel("Priority:"));
        taskInputPanel.add(priorityField);
        taskInputPanel.add(addTaskButton);
        taskInputPanel.add(processTasksButton);
        taskPanel.add(taskInputPanel, BorderLayout.SOUTH);
        add(taskPanel);

        // User Requests Panel (Round-Robin Scheduling)
        JPanel requestPanel = new JPanel(new BorderLayout());
        requestPanel.setBorder(BorderFactory.createTitledBorder("Round-Robin User Requests"));
        requestListModel = new DefaultListModel<>();
        JList<String> requestList = new JList<>(requestListModel);
        requestPanel.add(new JScrollPane(requestList), BorderLayout.CENTER);

        JPanel requestInputPanel = new JPanel();
        requestField = new JTextField(10);
        JButton addRequestButton = new JButton("Add Request");
        addRequestButton.addActionListener(new AddRequestListener());
        JButton processRequestsButton = new JButton("Process Requests");
        processRequestsButton.addActionListener(new ProcessRequestListener());
        requestInputPanel.add(new JLabel("Request:"));
        requestInputPanel.add(requestField);
        requestInputPanel.add(addRequestButton);
        requestInputPanel.add(processRequestsButton);
        requestPanel.add(requestInputPanel, BorderLayout.SOUTH);
        add(requestPanel);

        // Disk Requests Panel (SSTF Disk Scheduling)
        JPanel diskPanel = new JPanel(new BorderLayout());
        diskPanel.setBorder(BorderFactory.createTitledBorder("SSTF Disk Scheduling"));
        diskListModel = new DefaultListModel<>();
        JList<String> diskList = new JList<>(diskListModel);
        diskPanel.add(new JScrollPane(diskList), BorderLayout.CENTER);

        JPanel diskInputPanel = new JPanel();
        trackField = new JTextField(5);
        currentHeadField = new JTextField(5);
        JButton addDiskRequestButton = new JButton("Add Disk Request");
        addDiskRequestButton.addActionListener(new AddDiskRequestListener());
        JButton processDiskRequestsButton = new JButton("Process Disk Requests");
        processDiskRequestsButton.addActionListener(new ProcessDiskRequestListener());
        diskInputPanel.add(new JLabel("Track Position:"));
        diskInputPanel.add(trackField);
        diskInputPanel.add(new JLabel("Current Head:"));
        diskInputPanel.add(currentHeadField);
        diskInputPanel.add(addDiskRequestButton);
        diskInputPanel.add(processDiskRequestsButton);
        diskPanel.add(diskInputPanel, BorderLayout.SOUTH);
        add(diskPanel);
    }

    // Listeners for Task Scheduling
    private class AddTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String task = taskField.getText();
            int priority = Integer.parseInt(priorityField.getText());
            taskListModel.addElement("Task: " + task + " | Priority: " + priority);
            taskField.setText("");
            priorityField.setText("");
        }
    }

    private class ProcessTaskListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<String> tasks = new ArrayList<>();
            for (int i = 0; i < taskListModel.getSize(); i++) {
                tasks.add(taskListModel.getElementAt(i));
            }
            taskListModel.clear();
            tasks.sort(Comparator.comparingInt(t -> Integer.parseInt(t.split(": ")[2])));
            for (String task : tasks) {
                taskListModel.addElement("Processing " + task);
            }
        }
    }

    // Listeners for Round-Robin Scheduling
    private class AddRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String request = requestField.getText();
            requestListModel.addElement("Request: " + request);
            requestField.setText("");
        }
    }

    private class ProcessRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            List<String> requests = new ArrayList<>();
            for (int i = 0; i < requestListModel.getSize(); i++) {
                requests.add(requestListModel.getElementAt(i));
            }
            requestListModel.clear();
            for (String request : requests) {
                requestListModel.addElement("Processing " + request);
            }
        }
    }

    // Listeners for SSTF Disk Scheduling
    private class AddDiskRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int trackPosition = Integer.parseInt(trackField.getText());
            diskListModel.addElement("Track Position: " + trackPosition);
            trackField.setText("");
        }
    }

    private class ProcessDiskRequestListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int[] currentHeadPosition = {Integer.parseInt(currentHeadField.getText())};
            List<String> diskRequests = new ArrayList<>();
            for (int i = 0; i < diskListModel.getSize(); i++) {
                diskRequests.add(diskListModel.getElementAt(i));
            }
            diskListModel.clear();
            diskRequests.sort(Comparator.comparingInt(r -> Math.abs(Integer.parseInt(r.split(": ")[1]) - currentHeadPosition[0])));
            for (String request : diskRequests) {
                diskListModel.addElement("Processing " + request);
                currentHeadPosition[0] = Integer.parseInt(request.split(": ")[1]);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryManagementApp app = new LibraryManagementApp();
            app.setVisible(true);
        });
    }
}
