package gui;

import jadex.bridge.IExternalAccess;
import main.GarbageCollector;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.border.Border;

public class Interface extends JFrame implements ActionListener, ItemListener{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;
    private JCheckBox communicationCB;
    private JCheckBox memoryCB;
    private JButton newBTN, pauseBTN, helpBTN, exitBTN;
    private JRadioButton[] radioComponent;
    private ButtonGroup radioComponentGroup;
    private JRadioButton[] radioComponentType;
    private ButtonGroup radioComponentTypeGroup;
    private JPanel optPane2, infoPanel, elementsPane;
    private GridCity city;
    public static Interface graphInt;
    private JTextField agentName, agentCapacity;
    private JLabel agentNameLabel;
    private JLabel agentCapacityLabel;
    private JLabel info;
    private IExternalAccess agent;
    private boolean pause;

    public Interface(final IExternalAccess agent) throws FileNotFoundException{
        //super("Garbage Collection");

        memoryCB = new JCheckBox();
        memoryCB.setSelected(false);
        memoryCB.setText("Memory");
        memoryCB.addItemListener(this);

        communicationCB = new JCheckBox();
        communicationCB.setSelected(false);
        communicationCB.setText("Communication");
        communicationCB.addItemListener(this);
        createAndDisplayGUI(agent);
        graphInt = this;
        pause = false;
    }



    private void createAndDisplayGUI(IExternalAccess agent){
        this.agent = agent;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(1024,768));
        setMinimumSize(new Dimension(800,600));

        JPanel contentPane = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        contentPane.setBorder(padding);
        contentPane.setLayout(new BorderLayout(10,10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));

        city = new GridCity("resources/graphs/City5", agent);
        contentPane.add(city, BorderLayout.CENTER);

        optPane2 = new JPanel();
        optPane2.setLayout(new GridLayout(0,1));
        optPane2.setBorder(BorderFactory.createTitledBorder("Options"));

        newBTN = new JButton("New");
        pauseBTN = new JButton("Pause");
        helpBTN = new JButton("Help");
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
        optPane2.add(helpBTN);
        helpBTN.addActionListener(this);
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton clicked = (JButton) e.getSource();

        if(clicked == newBTN){
            ChooseCityGUI dialog = new ChooseCityGUI();
            dialog.setVisible(true);
            Container panel = getContentPane();
            panel.remove(city);
            GarbageCollector.getInstance().restartCity();
            city = new GridCity("resources/graphs/"+dialog.getCity(), this.agent);
            panel.add(city);
            validate();
            repaint();
        }else if(clicked == pauseBTN){
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
            System.out.println("Cliquei na checkbox da comunicacao");

    }

    public JTextField getAgentName() {
        return agentName;
    }

    public JTextField getAgentCapacity() {
        return agentCapacity;
    }

    public JRadioButton[] getRadioComponent() {
        return radioComponent;
    }

    public JRadioButton[] getRadioComponentType() {
        return radioComponentType;
    }

    public String getInfo() {
        return info.getText();
    }

    public void setInfo(String info) {
        this.info.setText(info);
    }

    public void setInfoVisible(boolean value) {
         infoPanel.setVisible(value);
    }

    public GridCity getCity() {
        return city;
    }

    public boolean getPause() {
        return pause;
    }


}