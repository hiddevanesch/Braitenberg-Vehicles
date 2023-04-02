package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;

public class ControllableLamp extends Lamp implements ControlHandler {
    public ControllableLamp(ModelLoader modelLoader, Vector3f position, Vector3f colour, Vector3f attenuation) throws FileNotFoundException {
        super(modelLoader, position, colour, attenuation);
    }

    @Override
    public void move(long window, Renderer renderer) {
        float frameTime = renderer.getFrameTimeSeconds();

        // Compute position based on wheel speeds
        float distance = Settings.LAMP_MOVEMENT_SPEED * frameTime;

        float dx = 0;
        float dy = 0;
        float dz = 0;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            dz -= distance;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            dz += distance;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            dx -= distance;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            dx += distance;
        }
        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW.GLFW_PRESS) {
            dy += distance;
        }
        if (glfwGetKey(window, GLFW_KEY_E) == GLFW.GLFW_PRESS) {
            dy -= distance;
        }

        Vector3f deltaPosition = new Vector3f(dx, dy, dz);

        movePosition(deltaPosition);
    }
}
