package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ChooseCityGUI extends JDialog {

    private String city;
    private JComboBox<String> cb;
    private String[] cities;

    public ChooseCityGUI() {
        this.setModal(true);
        this.setTitle("Choose city");
        this.setSize(200,100);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        cities = new String[5];
        for(int i = 1 ; i <= cities.length; i++){
            cities[i-1] = "City"+i;
        }

        cb = new JComboBox<>(cities);
        cb.setSelectedIndex(0);
        this.add(cb, BorderLayout.CENTER);

        JPanel aux = new JPanel(new FlowLayout());

        JButton okBTN = new JButton("Ok");

        okBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedPos = cb.getSelectedIndex();
                city = cities[selectedPos];
                fecharDialog();
            }
        });

        aux.add(okBTN);
        this.add(aux, BorderLayout.SOUTH);

    }

    private void fecharDialog(){
        this.dispose();
    }

    public String getCity() {
        return city;
    }
}


