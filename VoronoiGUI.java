import javax.swing.*;
import java.awt.*;

public class VoronoiGUI extends JFrame {

    private final VoronoiPanel voronoiPanel;

    // Constructor to set up the GUI components
    public VoronoiGUI() {
        // Set the title of the window
        setTitle("Voronoi Diagram Generator");
        // Set the size of the window
        setSize(800, 800);
        // Ensure the application exits when the window is closed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize the VoronoiPanel and add it to the center of the window
        voronoiPanel = new VoronoiPanel();
        add(voronoiPanel, BorderLayout.CENTER);

        // Create a button to generate the Voronoi diagram
        JButton generateButton = new JButton("Generate Voronoi");
        // Add an action listener to the button to generate the Voronoi diagram when clicked
        generateButton.addActionListener(e -> voronoiPanel.generateVoronoi());
        // Add the button to the bottom of the window
        add(generateButton, BorderLayout.SOUTH);

        // Make the window visible
        setVisible(true);
    }

    // Main method to launch the GUI application
    public static void main(String[] args) {
        // Ensure the GUI creation runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(VoronoiGUI::new);
    }
}