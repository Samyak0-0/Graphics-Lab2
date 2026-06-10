package lab2.qn2;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;

public class BLA {

    final int WIDTH = 800;
    final int HEIGHT = 600;

    public void digitize() {
        if (!glfwInit())
            throw new RuntimeException("Failed to init GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        long window = glfwCreateWindow(WIDTH, HEIGHT, "Bresenham Line", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create window");

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true);
        });

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode != null)
            glfwSetWindowPos(window,
                    (vidMode.width() - WIDTH) / 2,
                    (vidMode.height() - HEIGHT) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();

        glClearColor(0f, 0f, 0f, 1f);
        glPointSize(2f);

        int[][] lines = {
                { 100, 75, 700, 525 },
                { 175, 550, 625, 50 },
                { 50, 300, 750, 300 },
                { 400, 500, 400, 50 },
        };

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            glColor3f(0.45f, 0.45f, 0.45f);
            glBegin(GL_LINES);
            glVertex2f(-1f, 0f);
            glVertex2f(1f, 0f);
            glVertex2f(0f, -1f);
            glVertex2f(0f, 1f);
            glEnd();

            glBegin(GL_LINES);
            float tickSize = 0.015f;
            int tickSpacing = 50;
            for (int px = tickSpacing; px < WIDTH; px += tickSpacing) {
                float nx = toNdcX(px);
                glVertex2f(nx, -tickSize);
                glVertex2f(nx, tickSize);
            }
            for (int py = tickSpacing; py < HEIGHT; py += tickSpacing) {
                float ny = toNdcY(py);
                glVertex2f(-tickSize, ny);
                glVertex2f(tickSize, ny);
            }
            glEnd();

            glColor3f(1f, 1f, 1f);
            glBegin(GL_POINTS);
            for (int[] ln : lines) {
                drawLine(ln[0], ln[1], ln[2], ln[3]);
            }
            glEnd();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    float toNdcX(int px) {
        return (2f * px / WIDTH) - 1f;
    }

    float toNdcY(int py) {
        return (2f * py / HEIGHT) - 1f;
    }

    void drawLine(int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        if (dx >= dy) {
            bresenhamLow(x0, y0, x1, y1);
        } else {
            bresenhamHigh(x0, y0, x1, y1);
        }
    }

    void bresenhamLow(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int yi = 1;
        if (dy < 0) {
            yi = -1;
            dy = -dy;
        }
        int D = 2 * dy - dx;
        int y = y0;
        int stepX = (dx >= 0) ? 1 : -1;
        if (dx < 0)
            dx = -dx;

        for (int x = x0; x != x1 + stepX; x += stepX) {
            glVertex2f(toNdcX(x), toNdcY(y));
            if (D > 0) {
                y += yi;
                D += 2 * (dy - dx);
            } else {
                D += 2 * dy;
            }
        }
    }

    void bresenhamHigh(int x0, int y0, int x1, int y1) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int xi = 1;
        if (dx < 0) {
            xi = -1;
            dx = -dx;
        }
        int D = 2 * dx - dy;
        int x = x0;
        int stepY = (dy >= 0) ? 1 : -1;
        if (dy < 0)
            dy = -dy;

        for (int y = y0; y != y1 + stepY; y += stepY) {
            glVertex2f(toNdcX(x), toNdcY(y));
            if (D > 0) {
                x += xi;
                D += 2 * (dx - dy);
            } else {
                D += 2 * dx;
            }
        }
    }
}
