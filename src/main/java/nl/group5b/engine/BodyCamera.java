package nl.group5b.engine;

import nl.group5b.model.interfaces.PositionHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.util.vector.Vector3f;


import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;


public class BodyCamera extends Camera {
    private PositionHandler body;

    private static final float SENSITIVITY = 0.2f;

    private float heightOffset;

    private float distance = 4;
    private float bodyPitch;
    private float angle;

    private float previousMouseX;
    private float previousMouseY;
    private float mouseDX;
    private float mouseDY;

    // require Body that implements MoveHandler
    public BodyCamera(PositionHandler body, float heightOffset) {
        this.body = body;
        this.heightOffset = heightOffset; // TODO change to body height / 2
    }

    public void changeBody(PositionHandler body) {
        this.body = body;
    }

    public void move(long window) {
        computePitch(window);
        computeAngle(window);
        float horizontalDistance = computeHorizontalDistance();
        float verticalDistance = computeVerticalDistance();
        computeCameraPosition(horizontalDistance, verticalDistance);
    }

    private void computePitch(long window) {
        if (glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS) {
            float pitchChange = mouseDY * SENSITIVITY;
            bodyPitch += pitchChange;
            mouseDY = 0;
        }
    }

    private void computeAngle(long window) {
        if (glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS) {
            float angleChange = mouseDX * SENSITIVITY;
            angle -= angleChange;
            mouseDX = 0;
        }
    }

    private void computeCameraPosition(float horizontalDistance, float verticalDistance) {
        Vector3f bodyPosition = body.getPosition();
        Vector3f bodyRotation = body.getRotation();
        float theta = bodyRotation.y + angle;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = bodyPosition.x - offsetX;
        position.y = bodyPosition.y + heightOffset + verticalDistance;
        position.z = bodyPosition.z - offsetZ;
        rotation.x = bodyPitch;
        rotation.y = 180 - (bodyRotation.y + angle);
    }

    private float computeHorizontalDistance() {
        return (float) (distance * Math.cos(Math.toRadians(bodyPitch)));
    }

    private float computeVerticalDistance() {
        return (float) (distance * Math.sin(Math.toRadians(bodyPitch)));
    }

    public void enableZoom(long window) {
        GLFW.glfwSetScrollCallback(window, new GLFWScrollCallback() {
            @Override
            public void invoke(long win, double dx, double dy) {
                float zoomLevel = (float) dy * distance * SENSITIVITY;
                distance -= zoomLevel;
            }
        });
    }

    public void enableMouseTracking(long window) {
        GLFW.glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long win, double newX, double newY) {
                mouseDX = ((float) newX - previousMouseX);
                mouseDY = ((float) newY - previousMouseY);
                previousMouseX = (float) newX;
                previousMouseY = (float) newY;
            }
        });
    }
}
