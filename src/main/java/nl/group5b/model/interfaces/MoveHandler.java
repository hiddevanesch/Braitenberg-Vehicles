package nl.group5b.model.interfaces;

import org.lwjgl.util.vector.Vector3f;

public interface MoveHandler {
    public void setPosition(Vector3f position);

    public void setRotation(Vector3f rotation);

    public Vector3f getPosition();

    public Vector3f getRotation();
}
