import javax.swing.*;
import java.awt.*;

public class VoronoiGUI extends JFrame {

    private final VoronoiPanel voronoiPanel;

    public VoronoiGUI() {
        setTitle("Voronoi Diagram Generator");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        voronoiPanel = new VoronoiPanel();
        add(voronoiPanel, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate Voronoi");
        generateButton.addActionListener(e -> voronoiPanel.generateVoronoi());
        add(generateButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VoronoiGUI::new);
    }
}