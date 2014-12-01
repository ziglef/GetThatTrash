package gui;

import jadex.bridge.IExternalAccess;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;

import javax.swing.*;
import javax.swing.border.Border;

public class Interface extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;
    private JCheckBox communicationCB;
    private JCheckBox memoryCB;
    private JRadioButton[] radioComponent;
    private ButtonGroup radioComponentGroup;
    private JRadioButton[] radioComponentType;
    private ButtonGroup radioComponentTypeGroup;
    private JPanel optPane2;
    private GridCity city;

    public Interface() throws FileNotFoundException{
        //super("Garbage Collection");

        memoryCB = new JCheckBox();
        memoryCB.setSelected(true);
        memoryCB.setText("Memory");

        communicationCB = new JCheckBox();
        communicationCB.setSelected(true);
        communicationCB.setText("Communication");
        createAndDisplayGUI();
    }

    private void createAndDisplayGUI(/*String cityFile*/){
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(new Dimension(800,600));

        JPanel contentPane = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        contentPane.setBorder(padding);
        contentPane.setLayout(new BorderLayout(10,10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));

        /*CityMapBuilder ctB = new CityMapBuilder( new File( cityFile ) );
        int currV = 1;

        System.out.println("VertexSet: \n");
        for( Vertex v : ctB.getVertices() ){
            System.out.println( v.toString() );
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                String buttonTerrain = defaultTerrain;
                JButton button = new JButton();
                button.setContentAreaFilled(false);
                button.setBorderPainted(true);
                button.setBorder(BorderFactory.createEmptyBorder(0, 0, -1, -1)); // Right border = -1 to compensate for a swing bug
                if( currV <= ctB.getVertices().size() ) {
                    if (ctB.getVertexByName("v" + currV).getX() == i && ctB.getVertexByName("v" + currV).getY() == j) {
                        buttonTerrain = ctB.getVertexByName("v" + currV).getProperty("img");
                        currV++;
                    }
                }
                button.setIcon(new ImageIcon("resources/assets/images/" + buttonTerrain));
                buttonPanel.add(button);
            }
        }*/

        city = new GridCity("resources/graphs/City4");
        contentPane.add(city, BorderLayout.CENTER);

        //contentPane.add(buttonPanel, BorderLayout.CENTER);

        optPane2 = new JPanel();
        optPane2.setLayout(new GridLayout(0,1));
        optPane2.setBorder(BorderFactory.createTitledBorder("Options"));
        for(int i = 0; i < 4;i++){
        	JButton button = new JButton();

        	switch (i) {
            case 0: button.setText("New");break;
            case 1: button.setText("Pause");break;
            case 2: button.setText("Help");break;
            case 3: button.setText("Exit");break;
			default:break;
			}
            optPane2.add(button);
        }
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
        label= new JLabel("_________________");
        optPane2.add(label);
        label= new JLabel("Advanced options:");
        optPane2.add(label);
        optPane2.add(memoryCB);
        optPane2.add(communicationCB);

        contentPane.add(optPane2, BorderLayout.WEST);

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


    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                try {
                    new Interface().createAndDisplayGUI(/* "resources/graphs/City3";*/);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}