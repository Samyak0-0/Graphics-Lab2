package lab2.qn1;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class DDA {

    private long window;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;


    private static final int[][] PIXEL_LINES = {
            { 50, 400, 750, 400 }, 
            { 400, 50, 400, 750 }, 
            { 100, 700, 700, 100 },
           
    };

    private static final float[][] COLORS = {
            { 0.0f, 1.0f, 0.8f },
            { 1.0f, 0.6f, 0.0f },
            { 1.0f, 0.2f, 0.5f },
            
    };

    //Pixel → NDC conversion
    // NDC_x = (px / WIDTH) * 2 - 1 maps [0,W] → [-1, +1]
    // NDC_y = -(py / HEIGHT) * 2 + 1 maps [0,H] → [+1, -1] (y-flip)
    private static float toNdcX(float px) {
        return (px / WIDTH) * 2.0f - 1.0f;
    }

    private static float toNdcY(float py) {
        return -(py / HEIGHT) * 2.0f + 1.0f;
    }

    private void ddaLine(int x1, int y1, int x2, int y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0)
            return;

        float xInc = dx / steps;
        float yInc = dy / steps;

        float x = x1;
        float y = y1;

        glBegin(GL_POINTS);
        for (int i = 0; i <= steps; i++) {
            glVertex2f(toNdcX(Math.round(x)), toNdcY(Math.round(y)));
            x += xInc;
            y += yInc;
        }
        glEnd();
    }

    public void digitize() {
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

        window = glfwCreateWindow(WIDTH, HEIGHT, "DDA Line Drawing – Pixel → NDC", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

        glfwSetKeyCallback(window, (win, key, sc, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(win, true);
        });

        try (MemoryStack stack = stackPush()) {
            var pw = stack.mallocInt(1);
            var ph = stack.mallocInt(1);
            glfwGetWindowSize(window, pw, ph);
            var vm = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,
                    (vm.width() - pw.get(0)) / 2,
                    (vm.height() - ph.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();
        glClearColor(0.1f, 0.1f, 0.15f, 1.0f);
        glPointSize(2.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT);

            glColor3f(0.35f, 0.35f, 0.35f);
            ddaLine(0, 400, 800, 400); 
            ddaLine(400, 0, 400, 800); 

            // Coloured demo lines
            for (int i = 0; i < PIXEL_LINES.length; i++) {
                glColor3f(COLORS[i][0], COLORS[i][1], COLORS[i][2]);
                int[] L = PIXEL_LINES[i];
                ddaLine(L[0], L[1], L[2], L[3]);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
}