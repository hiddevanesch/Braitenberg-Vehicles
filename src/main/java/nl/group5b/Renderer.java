package nl.group5b;

import org.lwjgl.opengl.GL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

public class Renderer {

    public void init() {
        GL.createCapabilities();
    }

    public void prepare() {
        // Set the clear color
        GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Clear the framebuffer
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Model model) {
        // Bind the VAO
        GL46.glBindVertexArray(model.getVaoID());
        GL46.glEnableVertexAttribArray(Model.POSITION_ATTR);

        // Draw the triangles
        GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);

        // Unbind the VAO
        GL46.glDisableVertexAttribArray(Model.POSITION_ATTR);
        GL46.glBindVertexArray(0);
    }

    public void complete(long window) {
        GLFW.glfwSwapBuffers(window); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        GLFW.glfwPollEvents();
    }
}
