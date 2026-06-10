
package lab2.qn4;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Histogram {

    private static final int WIN_W = 900;
    private static final int WIN_H = 600;
    private static final String TITLE = "Histogram – DDA & BLA Line Functions";

    private static final String[] LABELS = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private static final double[] DATA = {
            3.1, 4.8, 9.2, 14.5, 19.8, 24.3,
            27.1, 26.4, 21.0, 14.7, 8.3, 4.0
    };

    private static final int MARGIN_L = 70;
    private static final int MARGIN_R = 30;
    private static final int MARGIN_B = 60;
    private static final int MARGIN_T = 50;

    private static final int CHART_W = WIN_W - MARGIN_L - MARGIN_R;
    private static final int CHART_H = WIN_H - MARGIN_B - MARGIN_T;

    private static final double MAX_VALUE = 30.0;
    private static final int Y_TICKS = 6;

    private static final float[] BG_COL = { 0.08f, 0.08f, 0.12f };
    private static final float[] BAR_COL = { 0.20f, 0.55f, 0.90f };
    private static final float[] BAR_OUTLINE = { 0.60f, 0.85f, 1.00f };
    private static final float[] AXIS_COL = { 0.80f, 0.80f, 0.80f };
    private static final float[] GRID_COL = { 0.22f, 0.22f, 0.28f };

    private long window;

    private static void printLegend() {
        System.out.printf("| %-6s | %-12s |%n", "Month", "Value (°C)");
        System.out.println("|--------|--------------|");
        for (int i = 0; i < DATA.length; i++) {
            System.out.printf("| %-6s | %-12.1f |%n", LABELS[i], DATA[i]);
        }
        System.out.println();
    }

    public void run() {
        init();
        printLegend();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialise GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIN_W, WIN_H, TITLE, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true);
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pW = stack.mallocInt(1);
            IntBuffer pH = stack.mallocInt(1);
            glfwGetWindowSize(window, pW, pH);
            GLFWVidMode vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vm != null) {
                glfwSetWindowPos(window,
                        (vm.width() - pW.get(0)) / 2,
                        (vm.height() - pH.get(0)) / 2);
            }
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // vsync
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WIN_W, 0, WIN_H, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        glPointSize(1.0f);

        while (!glfwWindowShouldClose(window)) {
            glClearColor(BG_COL[0], BG_COL[1], BG_COL[2], 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            drawHistogram();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void drawHistogram() {
        drawGrid();
        drawAxes();
        drawBars();
    }

    private void drawGrid() {
        setColor(GRID_COL);
        for (int i = 0; i <= Y_TICKS; i++) {
            int y = MARGIN_B + (int) Math.round((double) i / Y_TICKS * CHART_H);
            ddaLine(MARGIN_L, y, MARGIN_L + CHART_W, y);
        }
    }

    private void drawAxes() {
        setColor(AXIS_COL);
        blaLine(MARGIN_L, MARGIN_B, MARGIN_L, MARGIN_B + CHART_H);
        blaLine(MARGIN_L, MARGIN_B, MARGIN_L + CHART_W, MARGIN_B);
    }

    private void drawBars() {
        int n = DATA.length;
        int barSpacing = 8;
        int barWidth = (CHART_W / n) - barSpacing;

        for (int i = 0; i < n; i++) {
            int x0 = MARGIN_L + i * (CHART_W / n) + barSpacing / 2;
            int barH = (int) Math.round(DATA[i] / MAX_VALUE * CHART_H);
            int y0 = MARGIN_B;
            int y1 = MARGIN_B + barH;

            setColor(BAR_COL);
            for (int py = y0 + 1; py < y1; py++) {
                ddaLine(x0, py, x0 + barWidth - 1, py);
            }

            setColor(BAR_OUTLINE);
            blaLine(x0, y0, x0 + barWidth, y0);
            blaLine(x0, y1, x0 + barWidth, y1);
            blaLine(x0, y0, x0, y1);
            blaLine(x0 + barWidth, y0, x0 + barWidth, y1);
        }
    }

    private void ddaLine(int x1, int y1, int x2, int y2) {
        int dx = x2 - x1;
        int dy = y2 - y1;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            glBegin(GL_POINTS);
            glVertex2i(x1, y1);
            glEnd();
            return;
        }

        double xInc = (double) dx / steps;
        double yInc = (double) dy / steps;

        double x = x1;
        double y = y1;

        glBegin(GL_POINTS);
        for (int i = 0; i <= steps; i++) {
            glVertex2i((int) Math.round(x), (int) Math.round(y));
            x += xInc;
            y += yInc;
        }
        glEnd();
    }

    private void blaLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        glBegin(GL_POINTS);
        while (true) {
            glVertex2i(x1, y1);

            if (x1 == x2 && y1 == y2)
                break;

            int e2 = 2 * err;

            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        glEnd();
    }

    private static void setColor(float[] rgb) {
        glColor3f(rgb[0], rgb[1], rgb[2]);
    }
}
