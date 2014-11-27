package gui;

import map.CityMapBuilder;
import map.Vertex;

import java.awt.*;
import java.io.File;

import javax.swing.*;
import javax.swing.border.Border;

class Menu extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;

    public Menu(){
        super("Garbage collection");
    }

    private void createAndDisplayGUI(String cityFile){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        contentPane.setBorder(padding);
        contentPane.setLayout(new BorderLayout(10,10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));

        CityMapBuilder ctB = new CityMapBuilder( new File( cityFile ) );
        int currV = 1;
        String defaultTerrain = "terrainTile3.png";

        System.out.println("VertexSet: \n");
        for( Vertex v : ctB.getVertices() ){
            System.out.println( v.toString() );
        }

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                String buttonTerrain = defaultTerrain;
                JButton button = new JButton();
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, -1)); // Right border = -1 to compensate for a swing bug
                if( currV <= ctB.getVertices().size() ) {
                    if (ctB.getVertexByName("v" + currV).getX() == i && ctB.getVertexByName("v" + currV).getY() == j) {
                        buttonTerrain = ctB.getVertexByName("v" + currV).getProperty("img");
                        currV++;
                    }
                }
                button.setIcon(new ImageIcon("resources/assets/images/" + buttonTerrain));
                buttonPanel.add(button);
            }
        }
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        JPanel optPane = new JPanel();
        optPane.setBorder(BorderFactory.createTitledBorder("Options"));
        
        optPane.setLayout(new FlowLayout());
        for(int i = 0; i < 5;i++){
        	JButton button = new JButton();
        	switch (i) {
			case 0: button.setText("Add truck");break;
			case 1: button.setText("Add container");break;
			case 2: button.setText("Add deposit");break;
            case 3: button.setText("Pause");break;
            case 4: button.setText("Exit");break;
			default:break;
			}
        	optPane.add(button);
        }
        contentPane.add(optPane, BorderLayout.SOUTH);
        
        setContentPane(contentPane);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    

    public static void main(String[] args){

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new Menu().createAndDisplayGUI( "resources/graphs/sampleCity" );
            }
        });
    }
}