package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.Material;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import java.io.FileNotFoundException;
import static org.lwjgl.glfw.GLFW.*;

public class Controllable extends BraitenbergVehicle implements ControlHandler {

    // Brown body material
    static private final Material bodyMaterial = new Material(new Vector3f(0.45f, 0.30f, 0.2f), 10, 0.5f);

    public Controllable(ModelLoader modelLoader, Vector3f position,
                        Vector3f rotation) throws FileNotFoundException {
        super(modelLoader, bodyMaterial, position, rotation);
    }

    @Override
    public void move(long window, Renderer renderer) {
        // 'checkInput' sets the wheel speeds based on keyboard input
        checkInput(window, renderer);

        // Move the vehicle based on the new wheel speeds
        super.updatePosition(renderer);
    }

    private void checkInput(long window, Renderer renderer) {
        float frameTime = renderer.getFrameTimeSeconds();

        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            // Accelerate both wheels
            accelerateLeftWheel(frameTime);
            accelerateRightWheel(frameTime);
        } else {
            // Slow down
            if (leftWheelSpeed < Settings.VEHICLE_CLAMP_SPEED) {
                leftWheelSpeed = 0;
            } else {
                decelerateLeftWheel(frameTime);
            }
            if (rightWheelSpeed < Settings.VEHICLE_CLAMP_SPEED) {
                rightWheelSpeed = 0;
            } else {
                decelerateRightWheel(frameTime);
            }
        }
        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            turnRight(frameTime);

        } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            turnLeft(frameTime);
        }

        // Bound the wheel speeds
        if (leftWheelSpeed > Settings.CONTROLLABLE_VEHICLE_SPEED) {
            leftWheelSpeed = Settings.CONTROLLABLE_VEHICLE_SPEED;
        }
        if (rightWheelSpeed > Settings.CONTROLLABLE_VEHICLE_SPEED) {
            rightWheelSpeed = Settings.CONTROLLABLE_VEHICLE_SPEED;
        }
    }

    private void accelerateLeftWheel(float frameTime) {
        leftWheelSpeed += frameTime / (leftWheelSpeed + Settings.VEHICLE_ACCELERATION);
    }

    private void accelerateRightWheel(float frameTime) {
        rightWheelSpeed += frameTime / (rightWheelSpeed + Settings.VEHICLE_ACCELERATION);
    }

    private void decelerateLeftWheel(float frameTime) {
        leftWheelSpeed *= 1 - (frameTime * Settings.VEHICLE_DECELERATION);
    }

    private void decelerateRightWheel(float frameTime) {
        rightWheelSpeed *= 1 - (frameTime * Settings.VEHICLE_DECELERATION);
    }

    private void turnLeft(float frameTime) {
        accelerateRightWheel(frameTime);
        leftWheelSpeed *= 1 - ((frameTime * Settings.VEHICLE_DECELERATION) * 0.15f);
    }

    private void turnRight(float frameTime) {
        accelerateLeftWheel(frameTime);
        rightWheelSpeed *= 1 - ((frameTime * Settings.VEHICLE_DECELERATION) * 0.15f);
    }

}
