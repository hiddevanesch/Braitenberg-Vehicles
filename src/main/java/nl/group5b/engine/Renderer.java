package nl.group5b.engine;

import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.shaders.StaticShader;
import nl.group5b.util.Algebra;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class Renderer {

    private static final float FOV = 75;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private static long lastFrameTime;
    private static float delta;

    private Matrix4f projectionMatrix;
    private StaticShader shader;

    public Renderer(StaticShader shader) {
        this.shader = shader;
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void prepare() {
        // Set the clear color
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Clear the framebuffer
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        // Enable depth testing
        GL46.glEnable(GL46.GL_DEPTH_TEST);

        // Enable backface culling
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glCullFace(GL46.GL_BACK);
    }

    public void render(Map<Model, List<BodyElement>> renderMap) {
        // Iterate over all the different models
        for (Model model : renderMap.keySet()) {
            prepareModel(model);

            // Get all the BodyElements that use this model
            List<BodyElement> bodyElements = renderMap.get(model);

            // Iterate over all the BodyElements
            for (BodyElement bodyElement : bodyElements) {
                prepareInstance(bodyElement);

                // Render the BodyElement by drawing the triangles
                GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
            }
            unbindModel();
        }
    }

    private void prepareModel (Model model) {
        // Bind the VAO
        GL46.glBindVertexArray(model.getVaoID());
        GL46.glEnableVertexAttribArray(Model.POSITION_ATTR);
        GL46.glEnableVertexAttribArray(Model.NORMAL_ATTR);
    }

    private void unbindModel () {
        // Unbind the VAO
        GL46.glDisableVertexAttribArray(Model.POSITION_ATTR);
        GL46.glDisableVertexAttribArray(Model.NORMAL_ATTR);
        GL46.glBindVertexArray(0);
    }

    private void prepareInstance (BodyElement bodyElement) {
        Entity entity = bodyElement.getEntity();

        // Create transformation matrix
        Matrix4f transformationMatrix = Algebra.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());

        // Load transformation matrix into shader
        shader.loadTransformationMatrix(transformationMatrix);

        // Load shine variables into shader
        shader.loadMaterial(bodyElement.getMaterial());
    }

    public void complete(long window) {
        // Swap the color buffers
        GLFW.glfwSwapBuffers(window);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        GLFW.glfwPollEvents();

        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;
    }

    public long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }

    public float getFrameTimeSeconds() {
        return delta;
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) DisplayBuilder.getWidth() / (float) DisplayBuilder.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
    }
}
