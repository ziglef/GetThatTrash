/**
 * 
 */
package aiadp1.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Tiago
 *
 */
public class Menu extends JFrame{

	private static final long serialVersionUID = 1L;
	private static int gridSize = 10;

    public Menu(){
        super("Recolha de lixo numa cidade");
    }

    private void createAndDisplayGUI(){       
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(gridSize, gridSize, 0,0));
        for (int i = 0; i < gridSize; i++)
        {
            for (int j = 0; j < gridSize; j++)
            {
                JButton button = new JButton(+ i + "-" + j);
                buttonPanel.add(button);
            }
        }
        contentPane.add(buttonPanel);
        JPanel optPane = new JPanel();
        optPane.setBorder(BorderFactory.createTitledBorder("Opções"));
        
        optPane.setLayout(new GridLayout(3,1, 10,10));
        for(int i = 0; i < 3;i++){
        	JButton button = new JButton();
        	switch (i) {
			case 0: button.setText("Adicionar Camião");break;
			case 1: button.setText("Adicionar Contentor");break;
			case 2: button.setText("Adicionar Depósito");break;
			default:break;
			}
        	optPane.add(button);
        }
        contentPane.add(optPane);
        
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