package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.ControlHandler;
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
        float frameTime = renderer.getFrameTimeSeconds();
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
                // Decrease right wheel speed
                if (rightWheelSpeed > SPEED / STEERING) {
                    decelerateRightWheel(frameTime);
                } else {
                    accelerateRightWheel(frameTime);
                }
                accelerateLeftWheel(frameTime);
            } else if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
                // Decrease left wheel speed
                if (leftWheelSpeed > SPEED / STEERING) {
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
            if (leftWheelSpeed > SPEED) {
                leftWheelSpeed = SPEED;
            }
            if (rightWheelSpeed > SPEED) {
                rightWheelSpeed = SPEED;
            }
        } else {
            // Slow down
            if (leftWheelSpeed < CLAMP) {
                leftWheelSpeed = 0;
            }
            else {
                decelerateLeftWheel(frameTime);
            }
            if (rightWheelSpeed < CLAMP) {
                rightWheelSpeed = 0;
            }
            else {
                decelerateRightWheel(frameTime);
            }
        }
    }

    private void accelerateLeftWheel(float frameTime) {
        leftWheelSpeed += frameTime / (leftWheelSpeed + ACCELERATION);
    }

    private void accelerateRightWheel(float frameTime) {
        rightWheelSpeed += frameTime / (rightWheelSpeed + ACCELERATION);
    }

    private void decelerateLeftWheel(float frameTime) {
        leftWheelSpeed *= 1 - (frameTime * DECELERATION);
    }

    private void decelerateRightWheel(float frameTime) {
        rightWheelSpeed *= 1 - (frameTime * DECELERATION);
    }
}
