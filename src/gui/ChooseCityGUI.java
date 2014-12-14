package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Class responsible for show the menu to change the city.
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 * @see ActionListener
 */
public class ChooseCityGUI extends JDialog implements ActionListener{

    private String city = "None";
    private JButton city1, city2, city3, city4, city5;

    /**
     * Constructor of ChooseCityGui the creates
     * the interface of the menu to change the city
     */
    public ChooseCityGUI() {
        this.setModal(true);
        this.setTitle("Choose city");
        this.setMinimumSize(new Dimension(840,210));
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        setResizable(false);
        JPanel cities = new JPanel(new FlowLayout());
        city1 = createNewButton(cities, city1, 1);
        city2 = createNewButton(cities, city2, 2);
        city3 = createNewButton(cities, city3, 3);
        city4 = createNewButton(cities, city4, 4);
        city5 = createNewButton(cities, city5, 5);
        this.add(cities, BorderLayout.CENTER);

    }

    /**
     * Method that creates a new button with a bakground image
     * given a panel, a button and an identifier.
     *
     * @param cities - the JPanel to add the button
     * @param button - the JButton to create
     * @param i - the identifier
     * @return - the new JButton
     */
    private JButton createNewButton(JPanel cities, JButton button, int i) {
        button = new JButton();
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(true);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setIcon(new ImageIcon("resources/assets/images/mini" + i + ".png"));
        button.addActionListener(this);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cities.add(button);

        return button;
    }

    /**
     * Method that dispose this Dialong
     */
    private void closeDialong() {
        this.dispose();
    }

    /**
     * Method that returns the new city name
     *
     * @return - the city name
     */
    public String getCity() {
        return city;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton button = (JButton) e.getSource();

        if(button == city1) {
            city = "City1";
        }
        if(button == city2)
            city = "City2";
        if(button == city3)
            city = "City3";
        if(button == city4)
            city = "City4";
        if(button == city5)
            city = "City5";

        closeDialong();

    }
}


