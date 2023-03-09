package nl.group5b.model.models;

import nl.group5b.engine.Entity;
import nl.group5b.engine.Renderer;
import nl.group5b.model.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;

public class Dragon extends Body {
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
        checkInput(window);
        Entity entity = super.getBodyElements()[0].getEntity();
        entity.rotate(0, currentRotationSpeed * renderer.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * renderer.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(entity.getRy())));
        float dz = (float) (distance * Math.cos(Math.toRadians(entity.getRy())));
        entity.move(dx, 0, dz);
    }

    private void checkInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            currentSpeed = SPEED;
        } else if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            currentSpeed = -SPEED;
        } else {
            if (currentSpeed > 0) {
                currentSpeed -= 0.1f;
            } else if (currentSpeed < 0) {
                currentSpeed += 0.1f;
            }
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            currentRotationSpeed = -ROTATION_SPEED;
        } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            currentRotationSpeed = ROTATION_SPEED;
        } else {
            currentRotationSpeed = 0;
        }
    }

}
