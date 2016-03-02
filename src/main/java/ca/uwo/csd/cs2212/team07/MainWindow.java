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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import org.json.JSONException;

/**
 * Creates a Main Window that displays the program to the user.
 *
 * @author team07
 */
public class MainWindow extends JFrame implements ActionListener {

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
     * Constructs a new Main Window
     */
    public MainWindow() {
        this.getUserData();
        this.getUserConfig();
        this.initUI();
    }

    /**
     * Loads the serialized user data into a FitbitInfo object
     */
    private void getUserData() {
        try {
            fitbitInfo = loadInfo();
        } catch (Exception e) {
            fitbitInfo = new FitbitInfo();
            try {
                fitbitInfo.refreshInfo(Calendar.getInstance());
            } catch (JSONException ex) {
                System.err.println("JSONException - Error Accessing API: " + ex.getMessage());
            } catch (RefreshTokenException ex) {
                System.err.println("RefreshTokenException - Error Accessing API: " + ex.getMessage());
            }
        }
    }

    /**
     * Loads the serialized user configuration into a UserConfig object
     */
    private void getUserConfig() {

    }

    /**
     * Initializes the UI displayed in the Main Window
     */
    private void initUI() {
        this.setTitle("CS2212 Team07");
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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

        lastRefresh = new JLabel("last synced: " + fitbitInfo.getLastRefreshTime().getTime());
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
            this.refreshInfo();
        } else if (e.getSource() == exitButton) {
            try {
                this.storeInfo();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), "ERROR! Unable to store user data.");
            }
            System.exit(0); //exit the program
        }
    }

    /**
     * Loads the user info from a stored data file
     *
     * @return FitbitInfo object with stored user data
     * @throws Exception thrown when file is not found
     */
    public FitbitInfo loadInfo() throws Exception {

        ObjectInputStream in;
        in = new ObjectInputStream(new FileInputStream("user.info"));

        return (FitbitInfo) in.readObject();
    }

    /**
     * Stores the user info from the stored data file
     *
     * @throws Exception thrown when the file is unable to be stored
     */
    public void storeInfo() throws Exception {

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("user.info"));
        out.writeObject(fitbitInfo);

    }

    /**
     * Refreshes the info in the FitbitInfo object and then refreshes each of
     * the displays
     */
    private void refreshInfo() {
        try {
            fitbitInfo.refreshInfo(Calendar.getInstance());
            lastRefresh.setText("last synced: " + fitbitInfo.getLastRefreshTime().getTime());
            dashboard.refresh();
            dailyGoals.refresh();
        } catch (JSONException ex) {
            System.err.println("Error Accessing API");
        } catch (RefreshTokenException ex) {
            System.err.println("Error Accessing API");
        }
    }

}
