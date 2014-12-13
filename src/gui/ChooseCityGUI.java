package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ChooseCityGUI extends JDialog implements ActionListener{

    private String city = "None";
    private JButton city1, city2, city3, city4, city5;

    public ChooseCityGUI() {
        this.setModal(true);
        this.setTitle("Choose city");
        this.setMinimumSize(new Dimension(840,210));
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        setResizable(false);
        /*for(int i = 1 ; i <= cities.length; i++){
            cities[i-1] = "City"+i;
        }

        cb = new JComboBox<>(cities);
        cb.setSelectedIndex(0);*/
        JPanel cities = new JPanel(new FlowLayout());
        city1 = createNewButton(cities, city1, 1);
        city2 = createNewButton(cities, city2, 2);
        city3 = createNewButton(cities, city3, 3);
        city4 = createNewButton(cities, city4, 4);
        city5 = createNewButton(cities, city5, 5);
        this.add(cities, BorderLayout.CENTER);

    }

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

    private void fecharDialog() {
        this.dispose();
    }

    public String getCity() {
        return city;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JButton button = (JButton) e.getSource();

        if(button == city1) {
            city = "City1";
            System.out.println("Carreguei no 1");
        }
        if(button == city2)
            city = "City2";
        if(button == city3)
            city = "City3";
        if(button == city4)
            city = "City4";
        if(button == city5)
            city = "City5";

        fecharDialog();

    }
}


