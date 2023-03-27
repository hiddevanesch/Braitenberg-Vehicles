package nl.group5b.model;

import org.joml.Vector3f;


public class HitBox {
    private Vector3f frontLeftOffset;
    private Vector3f frontRightOffset;
    private Vector3f backLeftOffset;
    private Vector3f backRightOffset;
    private Entity entity;

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

        // get the position and rotation of the entity
        Vector3f position = entity.getPosition();
        Vector3f rotation = entity.getRotation();
        float rotationY = (float) Math.toRadians(rotation.y);

        // Compute the coordinates based on rotation and position and the offsets
        // Position of the front left corner
        coordinates[0] = new Vector3f(
                (float) (position.x + (frontLeftOffset.x * Math.cos(rotationY) - frontLeftOffset.z * Math.sin(rotationY))),
                (float) (position.y + frontLeftOffset.y),
                (float) (position.z + (frontLeftOffset.x * Math.sin(rotationY) + frontLeftOffset.z * Math.cos(rotationY)))
        );
        // Position of the front right corner
        coordinates[1] = new Vector3f(
                (float) (position.x + (frontRightOffset.x * Math.cos(rotationY) - frontRightOffset.z * Math.sin(rotationY))),
                (float) (position.y + frontRightOffset.y),
                (float) (position.z + (frontRightOffset.x * Math.sin(rotationY) + frontRightOffset.z * Math.cos(rotationY)))
        );
        // Position of the back left corner
        coordinates[2] = new Vector3f(
                (float) (position.x + (backLeftOffset.x * Math.cos(rotationY) - backLeftOffset.z * Math.sin(rotationY))),
                (float) (position.y + backLeftOffset.y),
                (float) (position.z + (backLeftOffset.x * Math.sin(rotationY) + backLeftOffset.z * Math.cos(rotationY)))
        );
        // Position of the back right corner
        coordinates[3] = new Vector3f(
                (float) (position.x + (backRightOffset.x * Math.cos(rotationY) - backRightOffset.z * Math.sin(rotationY))),
                (float) (position.y + backRightOffset.y),
                (float) (position.z + (backRightOffset.x * Math.sin(rotationY) + backRightOffset.z * Math.cos(rotationY)))
        );
        return coordinates;
    }

    // Function that returns the next coordinates based on the position, rotation and offsets
//    public Vector3f[] getNextCoordinates(Vector3f deltaPosition, Vector3f deltaRotation) {
//        // Get rotation of entity and add deltaRotation
//        Vector3f rotation = entity.getRotation();
//        rotation.add(deltaRotation);
//        // Get position of entity and add deltaPosition
//        Vector3f position = entity.getPosition();
//        position.add(deltaPosition);
//
//        // Compute the coordinates based on rotation and position and the offsets
//        Vector3f[] coordinates = new Vector3f[4];
//
//        // Position of the front left corner
//        coordinates[0] = new Vector3f(
//                (float) (position.x + (frontLeftOffset.x * Math.cos(rotation.y) - frontLeftOffset.z * Math.sin(rotation.y))),
//                (float) (position.y + frontLeftOffset.y),
//                (float) (position.z + (frontLeftOffset.x * Math.sin(rotation.y) + frontLeftOffset.z * Math.cos(rotation.y)))
//        );
//        // Position of the front right corner
//        coordinates[1] = new Vector3f(
//                (float) (position.x + (frontRightOffset.x * Math.cos(rotation.y) - frontRightOffset.z * Math.sin(rotation.y))),
//                (float) (position.y + frontRightOffset.y),
//                (float) (position.z + (frontRightOffset.x * Math.sin(rotation.y) + frontRightOffset.z * Math.cos(rotation.y)))
//        );
//        // Position of the back left corner
//        coordinates[2] = new Vector3f(
//                (float) (position.x + (backLeftOffset.x * Math.cos(rotation.y) - backLeftOffset.z * Math.sin(rotation.y))),
//                (float) (position.y + backLeftOffset.y),
//                (float) (position.z + (backLeftOffset.x * Math.sin(rotation.y) + backLeftOffset.z * Math.cos(rotation.y)))
//        );
//        // Position of the back right corner
//        coordinates[3] = new Vector3f(
//                (float) (position.x + (backRightOffset.x * Math.cos(rotation.y) - backRightOffset.z * Math.sin(rotation.y))),
//                (float) (position.y + backRightOffset.y),
//                (float) (position.z + (backRightOffset.x * Math.sin(rotation.y) + backRightOffset.z * Math.cos(rotation.y)))
//        );
//        return coordinates;
//
//    }
}
