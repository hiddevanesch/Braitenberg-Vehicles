package nl.group5b.engine;

import nl.group5b.camera.Sensor;
import nl.group5b.model.Body;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Entity;
import nl.group5b.model.Model;
import nl.group5b.model.models.Controllable;
import nl.group5b.shaders.real.RealShader;
import nl.group5b.util.Algebra;
import nl.group5b.util.Settings;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class Renderer {

    private static long lastFrameTime;
    private static float delta;

    private RealShader shader;

    private List<Body> bodies; // used for hitboxes

    public Renderer(RealShader shader) {
        this.shader = shader;

        // Set the clear color
        GL46.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Enable depth testing
        GL46.glEnable(GL46.GL_DEPTH_TEST);
    }

    public void prepareSensor(Sensor sensor) {
        // Update screen size
        int width = sensor.getWidth();
        int height = sensor.getHeight();
        updateProjectionMatrix(width, height, Settings.SENSOR_FOV);

        // Bind the framebuffer
        sensor.bind();

        // Clear the framebuffer
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    public void prepareViewport() {
        // Update screen size
        int width = DisplayBuilder.getWidth();
        int height = DisplayBuilder.getHeight();
        GL46.glViewport(0, 0, width, height);
        updateProjectionMatrix(width, height, Settings.VIEWPORT_FOV);

        // Enable backface culling
        GL46.glEnable(GL46.GL_CULL_FACE);
        GL46.glCullFace(GL46.GL_BACK);

        // Clear the framebuffer
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
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

        // For every body that is instance of controllable
        for (Body body : bodies) {
            if (body instanceof Controllable) {
                // Get position, rotation, and scale from the entity of the body
                Vector3f position = body.getBodyElements()[0].getEntity().getPosition();
                Vector3f rotation = body.getBodyElements()[0].getEntity().getRotation();
                float scale = body.getBodyElements()[0].getEntity().getScale();

                // Create transformation matrix
                Matrix4f transformationMatrix = Algebra.createTransformationMatrix(position, rotation, scale);

                // Get the coordinates of the hitbox
                Vector3f[] coordinates = ((Controllable) body).getHitBox().getCoordinates();
                // Individual coordinates
                Vector3f FrontLeft = coordinates[0];
                Vector3f FrontRight = coordinates[1];
                Vector3f BackLeft = coordinates[2];
                Vector3f BackRight = coordinates[3];
                // Transform each coordinate using the transformation matrix
                Vector4f FrontLeft4 = new Vector4f(FrontLeft, 1.0f);
                transformationMatrix.transform(FrontLeft4);
                Vector4f FrontRight4 = new Vector4f(FrontRight, 1.0f);
                transformationMatrix.transform(FrontRight4);
                Vector4f BackLeft4 = new Vector4f(BackLeft, 1.0f);
                transformationMatrix.transform(BackLeft4);
                Vector4f BackRight4 = new Vector4f(BackRight, 1.0f);
                transformationMatrix.transform(BackRight4);
                // Draw the hitbox between coordinates (0,1) (1,2) (0,2) (1,3)
                // With a thick line
                GL46.glLineWidth(5);
                GL46.glBegin(GL46.GL_LINES);
                GL46.glVertex3f(FrontLeft4.x, FrontLeft4.y, FrontLeft4.z);
                GL46.glVertex3f(FrontRight4.x, FrontRight4.y, FrontRight4.z);
                GL46.glVertex3f(FrontRight4.x, FrontRight4.y, FrontRight4.z);
                GL46.glVertex3f(BackRight4.x, BackRight4.y, BackRight4.z);
                GL46.glVertex3f(BackRight4.x, BackRight4.y, BackRight4.z);
                GL46.glVertex3f(BackLeft4.x, BackLeft4.y, BackLeft4.z);
                GL46.glVertex3f(BackLeft4.x, BackLeft4.y, BackLeft4.z);
                GL46.glVertex3f(FrontLeft4.x, FrontLeft4.y, FrontLeft4.z);
                GL46.glEnd();
            }
        }

    }

        private void prepareModel (Model model){
            // Bind the VAO
            GL46.glBindVertexArray(model.getVaoID());
            GL46.glEnableVertexAttribArray(Settings.VAO_POSITION_ATTR);
            GL46.glEnableVertexAttribArray(Settings.VAO_NORMAL_ATTR);
        }

        private void unbindModel () {
            // Unbind the VAO
            GL46.glDisableVertexAttribArray(Settings.VAO_POSITION_ATTR);
            GL46.glDisableVertexAttribArray(Settings.VAO_NORMAL_ATTR);
            GL46.glBindVertexArray(0);
        }

        private void prepareInstance (BodyElement bodyElement){
            Entity entity = bodyElement.getEntity();

            // Create transformation matrix
            Matrix4f transformationMatrix = Algebra.createTransformationMatrix(entity.getPosition(), entity.getRotation(), entity.getScale());

            // Load transformation matrix into shader
            shader.loadTransformationMatrix(transformationMatrix);

            // Load shine variables into shader
            shader.loadMaterial(bodyElement.getMaterial());
        }

        public void completeSensor (Sensor sensor){
            // Unbind the framebuffer
            sensor.unbind();
        }

        public void completeViewport ( long window){
            // Swap the color buffers
            GLFW.glfwSwapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();

            long currentFrameTime = getCurrentTime();
            delta = (currentFrameTime - lastFrameTime) / 1000f;
            lastFrameTime = currentFrameTime;
        }

        public long getCurrentTime () {
            return System.nanoTime() / 1000000;
        }

        public float getFrameTimeSeconds () {
            return delta;
        }

        private void updateProjectionMatrix ( int width, int height, float fov){
            shader.start();
            shader.loadProjectionMatrix(Algebra.createProjectionMatrix(width, height, fov));
            shader.stop();
        }

        public void setBodies (List < Body > bodies) {
            this.bodies = bodies;
        }

}
