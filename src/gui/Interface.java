package gui;

import jadex.bridge.IExternalAccess;
import main.Chart;
import main.GarbageCollector;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class responsible for prepare and show all the Interface to the user.
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 * @see java.awt.event.ActionListener
 * @see java.awt.event.ItemListener
 */
public class Interface extends JFrame implements ActionListener, ItemListener{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;
    private JCheckBox communicationCB, memoryCB;
    private JButton newBTN, pauseBTN, statisticsBTN, showChart, exitBTN;
    private JRadioButton[] radioComponent, radioComponentType;
    private ButtonGroup radioComponentGroup, radioComponentTypeGroup;
    private JPanel optPane2, infoPanel, elementsPane;
    private GridCity city;
    public static Interface graphInt;
    private JTextField agentName, agentCapacity;
    private JLabel agentNameLabel, agentCapacityLabel, info;
    private IExternalAccess agent;
    private boolean pause;
    private JSlider slider;
    private Chart localMemoryUsageDemo;
    private JFrame localJFrame;
    private boolean showingChart = false;

    /**
     * Constructor of Interface
     *
     * @param agent - External acess to interface agent
     * @throws FileNotFoundException - if dont found the city file
     */
    public Interface(final IExternalAccess agent) throws FileNotFoundException{
        super("AIAD 2014/1015 - Garbage Collector - Rui Grandão - Tiago Coelho");
        createAndDisplayGUI(agent);
        graphInt = this;
        pause = false;
    }

    /**
     * Method that create and display all the interface components
     * @param agent - External acess for interface agent
     */
    private void createAndDisplayGUI(IExternalAccess agent){
        this.agent = agent;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1024,768));
        setMinimumSize(new Dimension(800,600));

        memoryCB = new JCheckBox();
        memoryCB.setSelected(false);
        memoryCB.setText("Use memory");
        memoryCB.addItemListener(this);
        communicationCB = new JCheckBox();
        communicationCB.setSelected(false);
        communicationCB.setText("Use communication");
        communicationCB.addItemListener(this);

        JPanel contentPane = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        contentPane.setBorder(padding);
        contentPane.setLayout(new BorderLayout(10,10));

        localJFrame = new JFrame("AIAD 2014/2015 - Garbage Collector - Statistics");
        localMemoryUsageDemo = new Chart();
        localJFrame.setResizable(false);
        localJFrame.setSize(new Dimension(800,600));
        localJFrame.getContentPane().add(localMemoryUsageDemo.getChartPanel());
        localJFrame.setVisible(false);
        Chart tmp56_55 = localMemoryUsageDemo;
        tmp56_55.getClass();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));

        city = new GridCity("resources/graphs/City1", agent);
        contentPane.add(city, BorderLayout.CENTER);

        optPane2 = new JPanel();
        optPane2.setLayout(new GridLayout(0,1));
        optPane2.setBorder(BorderFactory.createTitledBorder("Options"));

        newBTN = new JButton("New");
        pauseBTN = new JButton("Pause");
        statisticsBTN = new JButton("Generate Statistics");
        showChart = new JButton("Show Chart");
        exitBTN = new JButton("Exit");
        agentName = new JTextField("");
        agentName.setMaximumSize(new Dimension(100,5));
        agentCapacity = new JTextField("");
        agentNameLabel = new JLabel("Identifier: ");
        agentCapacityLabel = new JLabel("Capacity: ");

        optPane2.add(newBTN);
        newBTN.addActionListener(this);
        optPane2.add(pauseBTN);
        pauseBTN.addActionListener(this);
        optPane2.add(statisticsBTN);
        statisticsBTN.addActionListener(this);
        optPane2.add(showChart);
        showChart.addActionListener(this);
        optPane2.add(exitBTN);
        exitBTN.addActionListener(this);

        JLabel label = new JLabel("_________________");
        optPane2.add(label);
        label = new JLabel("Component:");
        optPane2.add(label);
        radioComponent =  new JRadioButton[3];
        radioComponent[0] = new JRadioButton("Truck");
        radioComponent[0].setSelected(true);
        radioComponent[1] = new JRadioButton("Collector");
        radioComponent[2] = new JRadioButton("Deposit");
        radioComponentGroup = new ButtonGroup();
        for(int i = 0 ; i < radioComponent.length; i++) {
            radioComponentGroup.add(radioComponent[i]);
            optPane2.add(radioComponent[i]);
        }

        label= new JLabel("_________________");
        optPane2.add(label);
        label= new JLabel("Component type:");
        optPane2.add(label);
        radioComponentType =  new JRadioButton[4];
        radioComponentType[0] = new JRadioButton("Glass");
        radioComponentType[1] = new JRadioButton("Paper");
        radioComponentType[2] = new JRadioButton("Plastic");
        radioComponentType[3] = new JRadioButton("Undifferentiated");
        radioComponentType[3].setSelected(true);
        radioComponentTypeGroup = new ButtonGroup();
        for(int i = 0 ; i < radioComponentType.length; i++) {
            radioComponentTypeGroup.add(radioComponentType[i]);
            optPane2.add(radioComponentType[i]);
        }
        optPane2.add(agentNameLabel);
        optPane2.add(agentName);
        optPane2.add(agentCapacityLabel);
        optPane2.add(agentCapacity);
        label= new JLabel("_________________");
        optPane2.add(label);
        label= new JLabel("Advanced options:");
        optPane2.add(label);
        slider = new JSlider(JSlider.HORIZONTAL, 1, 3, 3);
        slider.setMajorTickSpacing(3);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                GarbageCollector.getInstance().setVelocity(slider.getValue());
            }
        });
        label= new JLabel("Choose velocity");
        optPane2.add(label);
        label = new JLabel("( 1-Low / 2-Med / 3-Fast ) :");
        optPane2.add(label);
        optPane2.add(slider);
        optPane2.add(memoryCB);
        optPane2.add(communicationCB);

        contentPane.add(optPane2,BorderLayout.WEST);


        elementsPane = new JPanel();
        elementsPane.setLayout(new GridLayout(0, 1));


        infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        infoPanel.setVisible(false);

        info  = new JLabel();
        info.setForeground(Color.red);
        info.setFont (info.getFont().deriveFont(16.0f));
        infoPanel.add(info);
        contentPane.add(infoPanel,BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        validate();
        repaint();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                int close = JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (close == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        localJFrame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent paramAnonymousWindowEvent)
            {
                localJFrame.dispose();
                showingChart = false;
                showChart.setText("Show Chart");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton clicked = (JButton) e.getSource();

        if(clicked == newBTN){
            ChooseCityGUI dialog = new ChooseCityGUI();
            dialog.setVisible(true);
            if(!dialog.getCity().equals("None")) {
                Container panel = getContentPane();
                panel.remove(city);
                GarbageCollector.getInstance().restartCity();
                city = new GridCity("resources/graphs/" + dialog.getCity(), this.agent);
                panel.add(city);
                validate();
                repaint();
            }
        }else if(clicked == statisticsBTN) {
            localJFrame.setVisible(true);
            statisticsBTN.setEnabled(false);
            showingChart = true;
            showChart.setText("Hide Chart");
            GarbageCollector.getInstance().setGenerateStatistics(true);
        }else if(clicked == showChart){
                if(!showingChart){
                    localJFrame.setVisible(true);
                    showChart.setText("Hide Chart");
                    showingChart = true;
                }else{
                    localJFrame.dispose();
                    showChart.setText("Show Chart");
                    showingChart = false;
                }
        } if(clicked == pauseBTN){
            if(pause) {
                pause = false;
                pauseBTN.setText("Pause");
            }
            else {
                pauseBTN.setText("Unpause");
                pause = true;
            }
        }else if(clicked == exitBTN){
            int option = JOptionPane.showConfirmDialog(null, "Are you sure?", "Exit application", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option == JOptionPane.YES_OPTION)
                System.exit(0);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {

        JCheckBox checkbox = (JCheckBox) e.getItemSelectable();

        if(checkbox == memoryCB)
            GarbageCollector.getInstance().setMemory(checkbox.isSelected());

        if(checkbox == communicationCB)
            GarbageCollector.getInstance().setCommunication(checkbox.isSelected());

    }

    /**
     * Method tha returns the agent name created
     *
     * @return - agent name
     */
    public JTextField getAgentName() {
        return agentName;
    }

    /**
     * Method tha returns the agent capacity created
     *
     * @return - agent capacity
     */
    public JTextField getAgentCapacity() {
        return agentCapacity;
    }

    /**
     * Method tha returns the radioButton Group to check
     * the component to created
     *
     * @return - JRadioButton[]
     */
    public JRadioButton[] getRadioComponent() {
        return radioComponent;
    }

    /**
     * Method tha returns the radioButton Group to check
     * the type component to created
     *
     * @return - JRadioButton[]
     */
    public JRadioButton[] getRadioComponentType() {
        return radioComponentType;
    }

    /**
     * Methot that set the text to be displayed on the info panel
     *
     * @param info - text to be displayed
     */
    public void setInfo(String info) {
        this.info.setText(info);
    }

    /**
     * Methot that set infoPanel visible or not
     *
     * @param value - true to be displayed, false otherwise
     */
    public void setInfoVisible(boolean value) {
         infoPanel.setVisible(value);
    }

    /**
     * Method that returns the actual gridCity
     *
     * @return - the city
     */
    public GridCity getCity() {
        return city;
    }

    /**
     * Method that return the pause state
     *
     * @return - true if it pause, false otherwise
     */
    public boolean getPause() {
        return pause;
    }

}