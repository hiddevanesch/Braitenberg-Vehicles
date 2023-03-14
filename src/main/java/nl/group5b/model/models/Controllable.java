package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import java.io.FileNotFoundException;

import static org.lwjgl.glfw.GLFW.*;

public class Controllable extends BraitenbergVehicle implements ControlHandler {

    public Controllable(ModelLoader modelLoader) throws FileNotFoundException {
        super(modelLoader);
    }

    public Controllable(ModelLoader modelLoader, Vector3f position,
                        Vector3f rotation) throws FileNotFoundException {
        super(modelLoader, position, rotation);
    }

    @Override
    public void move(long window, Renderer renderer) {
        checkInput(window, renderer);

        float frameTime = renderer.getFrameTimeSeconds();

        // Compute rotation angle based on wheel speeds
        float rotationAngle = (rightWheelSpeed - leftWheelSpeed) * frameTime * 180;
        Vector3f deltaRotation = new Vector3f(0, rotationAngle, 0);
        moveRotation(deltaRotation);

        // Compute position based on wheel speeds
        float distance = (leftWheelSpeed + rightWheelSpeed) * frameTime;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotation().y)));
        Vector3f deltaPosition = new Vector3f(dx, 0, dz);
        movePosition(deltaPosition);

        // Rotate the wheels
        rotateWheels(frameTime);
    }

    private void checkInput(long window, Renderer renderer) {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            // Move forward
            // TODO acceleration?
            leftWheelSpeed = SPEED;
            rightWheelSpeed = SPEED;
        } else {
            // Slow down
            if (leftWheelSpeed < 0.1f)
                leftWheelSpeed = 0;
            else {
                // Slow down based on frame time
                leftWheelSpeed *= 1 - (renderer.getFrameTimeSeconds() * 10);
            }
            if (rightWheelSpeed < 0.1f)
                rightWheelSpeed = 0;
            else {
                // Slow down based on frame time
                rightWheelSpeed *= 1 - (renderer.getFrameTimeSeconds() * 10);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            // Decrease right wheel speed
            rightWheelSpeed *= 0.5f;
        } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            // Decrease left wheel speed
            leftWheelSpeed *= 0.5f;
        }
    }
}
