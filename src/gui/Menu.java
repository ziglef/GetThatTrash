/**
 * 
 */
package gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * @author Tiago
 *
 */
public class Menu extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;

    public Menu(){
        super("Garbage collection");
    }

    private void createAndDisplayGUI(){       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel();
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        contentPane.setBorder(padding);
        contentPane.setLayout(new BorderLayout(10,10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton button = new JButton();
                button.setOpaque(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(true);
                button.setMargin(new Insets(0,0,0,0));
                button.setIcon(new ImageIcon("resources/assets/images/roadTile2.png"));
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
                new Menu().createAndDisplayGUI();
            }
        });
    }
}