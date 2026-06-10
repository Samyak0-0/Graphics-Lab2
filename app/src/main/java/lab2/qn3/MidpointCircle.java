package lab2.qn3;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class MidpointCircle {

    static final int WIDTH = 800;
    static final int HEIGHT = 800;

    private long window;

    private void drawCircle(int cx, int cy, int r) {
        int x = 0;
        int y = r;
        int d = 1 - r;

        glBegin(GL_POINTS);
        plotCirclePoints(cx, cy, x, y);

        while (x < y) {
            if (d < 0) {
                d += 2 * x + 3;
            } else {
                d += 2 * (x - y) + 5;
                y--;
            }
            x++;
            plotCirclePoints(cx, cy, x, y);
        }
        glEnd();
    }

    private void plotCirclePoints(int cx, int cy, int x, int y) {
        plot(cx + x, cy + y);
        plot(cx - x, cy + y);
        plot(cx + x, cy - y);
        plot(cx - x, cy - y);
        plot(cx + y, cy + x);
        plot(cx - y, cy + x);
        plot(cx + y, cy - x);
        plot(cx - y, cy - x);
    }

    private void plot(int px, int py) {
        float ndcX = (px / (WIDTH / 2.0f)) - 1.0f;
        float ndcY = (py / (HEIGHT / 2.0f)) - 1.0f;
        glVertex2f(ndcX, ndcY);
    }

    private void drawAxes() {

        glColor3f(0.5f, 0.5f, 0.5f);
        glBegin(GL_LINES);

        glVertex2f(-1.0f, 0.0f);
        glVertex2f(1.0f, 0.0f);

        glVertex2f(0.0f, -1.0f);
        glVertex2f(0.0f, 1.0f);
        glEnd();

        float tickLen = 0.02f;
        glBegin(GL_LINES);
        for (int px = 50; px < WIDTH / 2; px += 50) {
            float t = px / (WIDTH / 2.0f);

            glVertex2f(t, -tickLen);
            glVertex2f(t, tickLen);
            glVertex2f(-t, -tickLen);
            glVertex2f(-t, tickLen);

            glVertex2f(-tickLen, t);
            glVertex2f(tickLen, t);
            glVertex2f(-tickLen, -t);
            glVertex2f(tickLen, -t);
        }
        glEnd();
    }

    public void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Midpoint Circle – LWJGL", 0, 0);
        if (window == 0)
            throw new RuntimeException("Failed to create the GLFW window");

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
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glPointSize(2.0f);

        int cx = 400, cy = 400, radius = 300;

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            drawAxes();

            glColor3f(1.0f, 1.0f, 1.0f);
            drawCircle(cx, cy, radius);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

}
