package nl.group5b.engine;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Camera {

    public Camera(Vector3f position, float pitch, float yaw, float roll) {
        this.position = position;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    private static final float SPEED = 0.05f;

    private Vector3f position;
    private float pitch;
    private float yaw;
    private float roll;

    public void move(long window) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.z += -SPEED;
        }
        if (glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.z += SPEED;
        }
        if (glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.x += -SPEED;
        }
        if (glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.x += SPEED;
        }
    }

    public Vector3f getPostion() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
