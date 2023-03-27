package nl.group5b.model.interfaces;


import org.joml.Vector3f;

public interface PositionHandler {
    void setPosition(Vector3f position);

    void setRotation(Vector3f rotation);

    void movePosition(Vector3f position);

    void moveRotation(Vector3f rotation);

    Vector3f getPosition();

    Vector3f getRotation();
}
