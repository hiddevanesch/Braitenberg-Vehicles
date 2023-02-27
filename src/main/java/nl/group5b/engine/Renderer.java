package nl.group5b.engine;

import nl.group5b.models.Model;
import nl.group5b.shaders.StaticShader;
import nl.group5b.util.Algebra;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Matrix4f;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

public class Renderer {

    private static final float FOV = 90;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    public Renderer(StaticShader shader) {
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public static void init() {
        GL.createCapabilities();
    }

    public void prepare() {
        // Set the clear color
        GL46.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Clear the framebuffer
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    public void render(Entity entity, StaticShader shader) {
        // Get the model
        Model model = entity.getModel();

        // Bind the VAO
        GL46.glBindVertexArray(model.getVaoID());
        GL46.glEnableVertexAttribArray(Model.POSITION_ATTR);

        // Create transformation matrix
        Matrix4f transformationMatrix = Algebra.createTransformationMatrix(entity.getPosition(), entity.getRx(), entity.getRy(), entity.getRz(), entity.getScale());

        // Load transformation matrix into shader
        shader.loadTransformationMatrix(transformationMatrix);

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

    private void createProjectionMatrix() {
        float aspectRatio = (float) DisplayBuilder.getWidth() / (float) DisplayBuilder.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }
}
