package nl.group5b.camera;

import nl.group5b.model.interfaces.PositionHandler;
import nl.group5b.util.Settings;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;


public class BodyCamera extends Camera {
    private PositionHandler body;

    private float heightOffset;

    private float distance = 4;
    private float bodyPitch = 15;
    private float angle;

    private float previousMouseX;
    private float previousMouseY;
    private float mouseDX;
    private float mouseDY;

    // require Body that implements MoveHandler
    public BodyCamera(PositionHandler body, float heightOffset) {
        super(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
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
            float pitchChange = mouseDY * Settings.CAMERA_MOUSE_SENSITIVITY;
            bodyPitch += pitchChange;
            mouseDY = 0;
        }
    }

    private void computeAngle(long window) {
        if (glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS) {
            float angleChange = mouseDX * Settings.CAMERA_MOUSE_SENSITIVITY;
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
                float zoomLevel = (float) dy * distance * Settings.CAMERA_MOUSE_SENSITIVITY;
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
