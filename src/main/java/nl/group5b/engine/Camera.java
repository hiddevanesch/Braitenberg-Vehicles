package nl.group5b.engine;


import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class Camera {

    private static final float SPEED = 0.05f;

    private Vector3f position;
    private Vector3f rotation;

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public void move(long window) {

    }

    public Vector3f getPostion() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
