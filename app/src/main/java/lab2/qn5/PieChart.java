package lab2.qn5;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class PieChart {

    private long window;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private static final String[] LABELS = { "Java", "Python", "C++", "Go", "Other" };
    private static final float[] VALUES = { 35f, 25f, 20f, 10f, 10f };

    private static final float[][] COLORS = {
            { 0.26f, 0.54f, 0.96f },
            { 0.18f, 0.80f, 0.44f },
            { 0.95f, 0.39f, 0.32f },
            { 0.99f, 0.72f, 0.07f },
            { 0.61f, 0.35f, 0.71f },
    };
    private static final String[] Color_Labels = {
            "blue", "green", "red", "yellow", "purple"
    };

    private static final int SLICES = 360;

    public void run() {
        init();
        loop();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "OpenGL Pie Chart – LWJGL", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true);
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pW = stack.mallocInt(1);
            IntBuffer pH = stack.mallocInt(1);
            glfwGetWindowSize(window, pW, pH);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,
                    (vidmode.width() - pW.get(0)) / 2,
                    (vidmode.height() - pH.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        System.out.println("Pie Chart Data");
        System.out.println("---------------------------------------------------------------------------");
        for (int i = 0; i < LABELS.length; i++) {
            System.out.printf("Label: %-7s | Percentage: %5.1f%% | Color: %s\n",
                    LABELS[i], VALUES[i], Color_Labels[i]);
        }
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(0.12f, 0.12f, 0.14f, 1.0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-1.2, 1.2, -1.2, 1.2, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            drawPie();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void drawPie() {
        float cx = 0f, cy = 0.05f;
        float r = 0.70f;

        float startAngle = 0f;

        for (int i = 0; i < VALUES.length; i++) {
            float sweepAngle = VALUES[i] / 100f * 360f;
            float[] col = COLORS[i];

            glColor3f(col[0], col[1], col[2]);
            glBegin(GL_TRIANGLE_FAN);
            glVertex2f(cx, cy);
            for (int s = 0; s <= SLICES; s++) {
                float angle = (float) Math.toRadians(startAngle + sweepAngle * s / SLICES);
                glVertex2f(cx + r * (float) Math.cos(angle),
                        cy + r * (float) Math.sin(angle));
            }
            glEnd();

            glColor3f(0.12f, 0.12f, 0.14f);
            glLineWidth(2.0f);
            glBegin(GL_LINE_LOOP);
            glVertex2f(cx, cy);
            for (int s = 0; s <= SLICES; s++) {
                float angle = (float) Math.toRadians(startAngle + sweepAngle * s / SLICES);
                glVertex2f(cx + r * (float) Math.cos(angle),
                        cy + r * (float) Math.sin(angle));
            }
            glEnd();

            startAngle += sweepAngle;
        }
    }

}
