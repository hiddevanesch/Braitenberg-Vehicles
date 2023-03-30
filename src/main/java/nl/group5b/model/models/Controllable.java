package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
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
        float leftSensorBrightness = getLeftSensor().calculateSensorBrightness();
        float rightSensorBrightness = getRightSensor().calculateSensorBrightness();

        // multiply brightness by 15, clamp between 0 and 1
        leftSensorBrightness = Math.min(1, leftSensorBrightness * 1);
        rightSensorBrightness = Math.min(1, rightSensorBrightness * 1);

        // print sensor brightness
        System.out.println("Left sensor brightness: " + leftSensorBrightness);
        System.out.println("Right sensor brightness: " + rightSensorBrightness);


        // 'checkInput' sets the wheel speeds based on keyboard input
        checkInput(window, renderer);

        // Move the vehicle based on the new wheel speeds
        super.updatePosition(renderer);
    }

    private void checkInput(long window, Renderer renderer) {
        float frameTime = renderer.getFrameTimeSeconds();
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                // Decrease right wheel speed
                if (rightWheelSpeed > Settings.VEHICLE_SPEED / Settings.VEHICLE_STEERING_FACTOR) {
                    decelerateRightWheel(frameTime);
                } else {
                    accelerateRightWheel(frameTime);
                }
                accelerateLeftWheel(frameTime);
            } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                // Decrease left wheel speed
                if (leftWheelSpeed > Settings.VEHICLE_SPEED / Settings.VEHICLE_STEERING_FACTOR) {
                    decelerateLeftWheel(frameTime);
                } else {
                    accelerateLeftWheel(frameTime);
                }
                accelerateRightWheel(frameTime);
            } else {
                // Accelerate both wheels
                accelerateLeftWheel(frameTime);
                accelerateRightWheel(frameTime);
            }
            // Bound the wheel speeds
            if (leftWheelSpeed > Settings.VEHICLE_SPEED) {
                leftWheelSpeed = Settings.VEHICLE_SPEED;
            }
            if (rightWheelSpeed > Settings.VEHICLE_SPEED) {
                rightWheelSpeed = Settings.VEHICLE_SPEED;
            }
        } else {
            // Slow down
            if (leftWheelSpeed < Settings.VEHICLE_CLAMP_SPEED) {
                leftWheelSpeed = 0;
            }
            else {
                decelerateLeftWheel(frameTime);
            }
            if (rightWheelSpeed < Settings.VEHICLE_CLAMP_SPEED) {
                rightWheelSpeed = 0;
            }
            else {
                decelerateRightWheel(frameTime);
            }
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

}
