package lab2;

import lab2.qn1.DDA;
import lab2.qn2.BLA;
import lab2.qn3.MidpointCircle;
import lab2.qn4.Histogram;
import lab2.qn5.PieChart;

public class Main {

    public static void main(String[] args) {

        new DDA().digitize();
        new BLA().digitize();
        new MidpointCircle().run();
        new Histogram().run();
        new PieChart().run();
    }
}
