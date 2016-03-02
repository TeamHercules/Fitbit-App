package ca.uwo.csd.cs2212.team07;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * Creates a Test Window that displays the program in test mode to the user.
 *
 * @author team07
 */
public class TestWindow extends JFrame implements ActionListener {

    private FitbitInfo fitbitInfo;

    private JToggleButton dashboardButton;
    private JToggleButton dailyGoalsButton;
    private JButton refreshButton;
    private JLabel lastRefresh;
    private JButton exitButton;

    private Dashboard dashboard;
    private DailyGoals dailyGoals;

    private ButtonGroup buttonGroup;
    private JPanel cardPane;
    private CardLayout cardLayout;

    /**
     * Constructor for the TestWindow class
     */
    public TestWindow() {
        this.getUserData();
        this.getUserConfig();
        this.initUI();
    }

    /**
     * Generates test data to display to the user
     */
    private void getUserData() {
        fitbitInfo = new FitbitInfo();
        fitbitInfo.generateTestData();
    }

    /**
     * Generates test configurations to display
     */
    private void getUserConfig() {

    }

    /**
     * Initializes the UI displayed in the Test Window
     */
    private void initUI() {
        this.setTitle("CS2212 Team07");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); //
        this.setLayout(new BorderLayout());

        // Creation of the Menu Bar
        JPanel menuBar = new JPanel();
        menuBar.setBackground(Color.WHITE);
        menuBar.setLayout(new BoxLayout(menuBar, BoxLayout.LINE_AXIS));

        buttonGroup = new ButtonGroup();
        dashboardButton = makeMenuButton("Dashboard", "dashboard.png", "dashboard_pressed.png");
        dailyGoalsButton = makeMenuButton("Daily Goals", "dailygoals.png", "dailygoals_pressed.png");

        menuBar.add(dashboardButton);
        menuBar.add(dailyGoalsButton);
        menuBar.add(Box.createHorizontalGlue());

        lastRefresh = new JLabel("TEST MODE");
        lastRefresh.setFont(new Font(lastRefresh.getFont().getName(), Font.PLAIN, 10));

        refreshButton = new JButton(new ImageIcon(FileReader.getImage("refresh.png")));
        refreshButton.setBorderPainted(false);
        refreshButton.setRolloverIcon(new ImageIcon(FileReader.getImage("refresh_pressed.png")));
        refreshButton.addActionListener(this);

        exitButton = new JButton(new ImageIcon(FileReader.getImage("exit.png")));
        exitButton.setBorderPainted(false);
        exitButton.setRolloverIcon(new ImageIcon(FileReader.getImage("exit_pressed.png")));
        exitButton.addActionListener(this);

        menuBar.add(lastRefresh);
        menuBar.add(refreshButton);
        menuBar.add(exitButton);
        // End of Menu Bar creation
        this.add(menuBar, BorderLayout.NORTH);

        // Creation of the CardLayout for displays
        dashboard = new Dashboard(fitbitInfo);
        dailyGoals = new DailyGoals(fitbitInfo);

        cardPane = new JPanel(new CardLayout());
        cardPane.add(dashboard, "Dashboard");
        cardPane.add(dailyGoals, "Daily Goals");
        cardLayout = (CardLayout) cardPane.getLayout();
        // End of CardLayout creation
        this.add(cardPane, BorderLayout.CENTER);

        dashboardButton.doClick();
    }

    /**
     * Creates a toggle button for a display such as Dashboard
     *
     * @param name name of display
     * @param iconFile name of icon stored in resources folder
     * @param iconSelectedFile name of selected icon stored in resources folder
     * @return the created menu button
     */
    private JToggleButton makeMenuButton(String name, String iconFile, String iconSelectedFile) {
        JToggleButton button = new JToggleButton();

        button.addActionListener(this);

        buttonGroup.add(button);
        button.setToolTipText(name);
        button.setBorderPainted(false);
        ImageIcon icon = new ImageIcon(FileReader.getImage(iconFile));
        ImageIcon iconSelected = new ImageIcon(FileReader.getImage(iconSelectedFile));
        button.setIcon(icon);
        button.setRolloverIcon(iconSelected);
        button.setSelectedIcon(iconSelected);
        button.setRolloverSelectedIcon(iconSelected);

        return button;
    }

    /**
     * Sets the results of clicking different buttons on the Dashboard
     *
     * @param e event called when button is pressed
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == dashboardButton) {
            cardLayout.show(cardPane, "Dashboard");
        } else if (e.getSource() == dailyGoalsButton) {
            cardLayout.show(cardPane, "Daily Goals");
        } else if (e.getSource() == refreshButton) {
            JOptionPane.showMessageDialog(new JFrame(), "Nice try. Unable to refresh in Test Mode");
        } else if (e.getSource() == exitButton) {
            System.exit(0); //exit the program
        }
    }

}