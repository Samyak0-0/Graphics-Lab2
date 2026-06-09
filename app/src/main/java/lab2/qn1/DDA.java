package lab2.qn1;

import java.lang.Math;

public class DDA {

    private int[] p1;
    private int[] p2;

    public DDA(int x1, int y1, int x2, int y2) {
        p1 = new int[2];
        p2 = new int[2];
        this.p1[0] = x1;
        this.p1[1] = y1;
        this.p2[0] = x2;
        this.p2[1] = y2;
    }

    public void digitize() {

        int dx = (p2[0] - p1[0]);
        int dy = (p2[1] - p1[1]);
        int stepSize;

        if (Math.abs(dx) > Math.abs(dy)) {
            stepSize = Math.abs(dx);
        } else {
            stepSize = Math.abs(dy);
        }

        float x_inc = (float) dx / stepSize;
        float y_inc = (float) dy / stepSize;

        float x = p1[0];
        float y = p1[1];

        System.out.printf("k\t|\t(x, y)\n");
        System.out.printf("-------------------------\n");
        System.out.printf("%d\t|\t(%d, %d)\n", 0, Math.round(x), Math.round(y));
        for (int i = 1; i <= stepSize; i++) {
            x = x + x_inc;
            y = y + y_inc;
            System.out.printf("%d\t|\t(%d, %d)\n", i, Math.round(x), Math.round(y));
        }
    }

}
