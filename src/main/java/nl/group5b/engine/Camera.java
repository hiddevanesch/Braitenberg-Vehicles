package nl.group5b.engine;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

    // These are preset values, such that setPosition and setRotation do not HAVE to be called per se
    // Though, when not using a BodyCamera, these functions should be called
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f rotation = new Vector3f(0, 0, 0);

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
}

