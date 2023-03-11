package nl.group5b.model.models;

import nl.group5b.engine.Entity;
import nl.group5b.engine.Renderer;
import nl.group5b.model.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;

public class Dragon extends Body implements ControlHandler {
    private static final float SPEED = 5;
    private static final float ROTATION_SPEED = 180;

    private float currentSpeed = 0;
    private float currentRotationSpeed = 0;

    public Dragon(ModelLoader modelLoader) throws FileNotFoundException {
        Model dragon = OBJLoader.loadOBJ("dragon", modelLoader);

        Material pinkMaterial = new Material(1, 0.5f, 0.5f, 10, 0.5f);

        Vector3f nullPosition = new Vector3f(0, 0, 0);

        Model[] loadedModels = {dragon};
        Material[] materialSets = {pinkMaterial};
        Vector3f[] startingPositions = {nullPosition};
        float[] scales = {0.1f};

        super.setBody(loadedModels, materialSets, startingPositions, scales);
    }

    public void move(long window, Renderer renderer) {
        checkInput(window, renderer);
        Entity entity = super.getBodyElements()[0].getEntity();
        entity.rotate(0, currentRotationSpeed * renderer.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * renderer.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(entity.getRotation().getY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(entity.getRotation().getY())));
        entity.move(dx, 0, dz);
    }

    private void checkInput(long window, Renderer renderer) {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            // Move forward
            currentSpeed = SPEED;
        } else if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            // Move backward
            currentSpeed = -SPEED;
        } else {
            // Slow down
            if (currentSpeed < 0.1f && currentSpeed > -0.1f)
                currentSpeed = 0;
            else {
                // Slow down based on frame time
                currentSpeed *= 1 - (renderer.getFrameTimeSeconds() * 10);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            // Rotate right
            currentRotationSpeed = -ROTATION_SPEED;
        } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            // Rotate left
            currentRotationSpeed = ROTATION_SPEED;
        } else {
            // Stop rotating
            currentRotationSpeed = 0;
        }
    }

    @Override
    public void setPosition(Vector3f position) {
        super.getBodyElements()[0].getEntity().setPosition(position);
    }

    @Override
    public void setRotation(Vector3f rotation) {
        super.getBodyElements()[0].getEntity().setRotation(rotation);
    }

    @Override
    public Vector3f getPosition() {
        return super.getBodyElements()[0].getEntity().getPosition();
    }

    @Override
    public Vector3f getRotation() {
        return super.getBodyElements()[0].getEntity().getRotation();
    }
}
