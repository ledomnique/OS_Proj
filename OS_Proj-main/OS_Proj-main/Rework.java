import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

public class Rework {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the frame for the main window
        JFrame frame = new JFrame("Side Panel Menu Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create the side panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.LIGHT_GRAY);

        // Add buttons to the side panel
        JButton button1 = new JButton("Round Robin Scheduler");
        JButton button2 = new JButton("Non-Preemptive Priority Scheduler");
        JButton button3 = new JButton("Shortest Seek Time First Scheduler");
        
        // Set the preferred size of all buttons to be the same
        Dimension buttonSize = new Dimension(150, 40); // Set width and height
        button1.setPreferredSize(buttonSize);
        button2.setPreferredSize(buttonSize);
        button3.setPreferredSize(buttonSize);

        // Create panels for each option
        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.CYAN);
        panel1.add(new JLabel("This is Option 1"));

        JPanel panel2 = new JPanel();
        panel2.setBackground(Color.YELLOW);
        panel2.add(new JLabel("This is Option 2"));

        JPanel panel3 = new JPanel();
        panel3.setBackground(Color.PINK);
        panel3.add(new JLabel("This is Option 3"));

        // Add action listeners for buttons
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to panel1 when Option 1 is clicked
                frame.getContentPane().removeAll();
                frame.getContentPane().add(sidePanel, BorderLayout.WEST);
                frame.getContentPane().add(panel1, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to panel2 when Option 2 is clicked
                frame.getContentPane().removeAll();
                frame.getContentPane().add(sidePanel, BorderLayout.WEST);
                frame.getContentPane().add(panel2, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Switch to panel3 when Option 3 is clicked
                frame.getContentPane().removeAll();
                frame.getContentPane().add(sidePanel, BorderLayout.WEST);
                frame.getContentPane().add(panel3, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });

        sidePanel.add(button1);
        sidePanel.add(button2);
        sidePanel.add(button3);

        // Create the main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.gray);
        
        // Set the layout for the frame
        frame.setLayout(new BorderLayout());
        frame.add(sidePanel, BorderLayout.WEST);
        frame.add(contentPanel, BorderLayout.CENTER);
        
        // Show the frame
        frame.setVisible(true);
    }
}
