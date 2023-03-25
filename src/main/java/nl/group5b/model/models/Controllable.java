package nl.group5b.model.models;

import nl.group5b.engine.Renderer;
import nl.group5b.model.Body;
import nl.group5b.model.HitBox;
import nl.group5b.model.ModelLoader;
import nl.group5b.model.interfaces.CollisionHandler;
import nl.group5b.model.interfaces.ControlHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.io.FileNotFoundException;
import java.util.List;

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

        // Compute position based on wheel speeds
        float distance = (leftWheelSpeed + rightWheelSpeed) * frameTime;
        float dx = (float) (distance * Math.sin(Math.toRadians(getRotation().y)));
        float dz = (float) (distance * Math.cos(Math.toRadians(getRotation().y)));
        Vector3f deltaPosition = new Vector3f(dx, 0, dz);

        // Compute the next hitbox coordinates based on the rotation and position
        Vector3f[] nextCoordinates = hitBox.getNextCoordinates(deltaRotation, deltaPosition);

        // Check for collision
        if(isColliding(nextCoordinates, bodiesPotentialCollide)){
            // If collision is detected, set wheel speeds to 0
            leftWheelSpeed = 0;
            rightWheelSpeed = 0;
        } else {
            // update the rotation and position of the vehicle
            moveRotation(deltaRotation);
            movePosition(deltaPosition);
            rotateWheels(frameTime);
        }




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
