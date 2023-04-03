package nl.group5b.model;

import org.joml.Vector3f;


public class HitBox {
    private final Vector3f frontLeftOffset;
    private final Vector3f frontRightOffset;
    private final Vector3f backLeftOffset;
    private final Vector3f backRightOffset;
    private final Entity entity;

    public HitBox(Vector3f frontLeftOffset, Vector3f frontRightOffset, Vector3f backLeftOffset, Vector3f backRightOffset, Entity entity) {
        this.frontLeftOffset = frontLeftOffset;
        this.frontRightOffset = frontRightOffset;
        this.backLeftOffset = backLeftOffset;
        this.backRightOffset = backRightOffset;
        this.entity = entity;
    }

    // Function that returns the coordinates based on the position, rotation and offsets
    public Vector3f[] getCoordinates() {
        Vector3f[] coordinates = new Vector3f[4];

        // Get the position and rotation of the entity
        Vector3f position = entity.getPosition();
        Vector3f rotation = entity.getRotation();
        float rotationY = (float) Math.toRadians(rotation.y);

        // Get position.x and inverse it
        // This is because the x-axis is inverted in the game
        float positionX = -position.x;

        // Compute the coordinates based on rotation and position and the offsets
        // Position of the front left corner
        coordinates[0] = new Vector3f(
                (float) (positionX + (frontLeftOffset.x * Math.cos(rotationY) - frontLeftOffset.z * Math.sin(rotationY))),
                position.y + frontLeftOffset.y,
                (float) (position.z + (frontLeftOffset.x * Math.sin(rotationY) + frontLeftOffset.z * Math.cos(rotationY)))
        );
        // Position of the front right corner
        coordinates[1] = new Vector3f(
                (float) (positionX + (frontRightOffset.x * Math.cos(rotationY) - frontRightOffset.z * Math.sin(rotationY))),
                position.y + frontRightOffset.y,
                (float) (position.z + (frontRightOffset.x * Math.sin(rotationY) + frontRightOffset.z * Math.cos(rotationY)))
        );
        // Position of the back left corner
        coordinates[2] = new Vector3f(
                (float) (positionX + (backLeftOffset.x * Math.cos(rotationY) - backLeftOffset.z * Math.sin(rotationY))),
                position.y + backLeftOffset.y,
                (float) (position.z + (backLeftOffset.x * Math.sin(rotationY) + backLeftOffset.z * Math.cos(rotationY)))
        );
        // Position of the back right corner
        coordinates[3] = new Vector3f(
                (float) (positionX + (backRightOffset.x * Math.cos(rotationY) - backRightOffset.z * Math.sin(rotationY))),
                position.y + backRightOffset.y,
                (float) (position.z + (backRightOffset.x * Math.sin(rotationY) + backRightOffset.z * Math.cos(rotationY)))
        );
        return coordinates;
    }
}
