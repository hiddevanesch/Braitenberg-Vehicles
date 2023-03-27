package nl.group5b.util;

import nl.group5b.camera.Camera;
import nl.group5b.engine.DisplayBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import nl.group5b.model.HitBox;


import java.nio.FloatBuffer;

public class Algebra {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.translate(translation);
        matrix.rotate((float) Math.toRadians(rotation.x()), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(rotation.y()), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(rotation.z()), new Vector3f(0, 0, 1));
        matrix.scale(new Vector3f(scale, scale, scale));
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().x()), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().y()), new Vector3f(0, 1, 0));
        viewMatrix.rotate((float) Math.toRadians(camera.getRotation().z()), new Vector3f(0, 0, 1));
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }

    public static Vector3f rotatePointAroundPivot(Vector3f point, Vector3f pivot, float angle) {
        float s = (float) Math.sin(Math.toRadians(-angle));
        float c = (float) Math.cos(Math.toRadians(-angle));

        // Rotate point
        float newX = point.x * c - point.z * s;
        float newZ = point.x * s + point.z * c;

        // Get result
        Vector3f result = new Vector3f();
        result.x = newX + pivot.x;
        result.y = pivot.y;
        result.z = newZ + pivot.z;
        return result;
    }

    public static void matrixToBuffer(Matrix4f matrix, FloatBuffer dest)
    {
        dest.put(0, matrix.m00());
        dest.put(1, matrix.m01());
        dest.put(2, matrix.m02());
        dest.put(3, matrix.m03());
        dest.put(4, matrix.m10());
        dest.put(5, matrix.m11());
        dest.put(6, matrix.m12());
        dest.put(7, matrix.m13());
        dest.put(8, matrix.m20());
        dest.put(9, matrix.m21());
        dest.put(10, matrix.m22());
        dest.put(11, matrix.m23());
        dest.put(12, matrix.m30());
        dest.put(13, matrix.m31());
        dest.put(14, matrix.m32());
        dest.put(15, matrix.m33());
    }

    public static Vector3f rotateWheelGivenCurrentAngle(float angle, float amount) {
        // Create a rotation matrix for the wheel
        Matrix4f wheelRotation = new Matrix4f();
        wheelRotation.rotate((float) Math.toRadians(angle), new Vector3f(0, 1, 0));

        // Create a rotation matrix for the amount to rotate forward
        Matrix4f forwardRotation = new Matrix4f();
        forwardRotation.rotate((float) Math.toRadians(amount), new Vector3f(1, 0, 0));

        // Combine the two rotations by multiplying them together
        Matrix4f combinedRotation = new Matrix4f();
        combinedRotation.mul(wheelRotation).mul(forwardRotation);

        // Get the euler angles from the combined rotation
        Vector3f result = new Vector3f();
        combinedRotation.getEulerAnglesXYZ(result);

        // Convert the result to degrees
        return new Vector3f((float) Math.toDegrees(result.x), (float) Math.toDegrees(result.y), (float) Math.toDegrees(result.z));
    }

    public static Matrix4f createProjectionMatrix(int width, int height, float fov) {
        Matrix4f projectionMatrix = new Matrix4f();
        float aspectRatio = (float) width / (float) height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = Settings.VIEWPORT_FAR_PLANE - Settings.VIEWPORT_NEAR_PLANE;

        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((Settings.VIEWPORT_FAR_PLANE + Settings.VIEWPORT_NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * Settings.VIEWPORT_NEAR_PLANE * Settings.VIEWPORT_FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);

        return projectionMatrix;
    }

    // Function that takes two hitboxes and returns true if they overlap.
    // The y (height) axis is not taken into account.
    // The hitboxes can be rotated
    public static boolean hitboxOverlap(Vector3f[] Coordinates, HitBox hitBoxOther) {
        // Get the x and z coordinates of hitBoxSelf
        Vector3f selfFrontLeft = Coordinates[0];
        Vector3f selfFrontRight = Coordinates[1];
        Vector3f selfRearRight = Coordinates[2];
        Vector3f selfRearLeft = Coordinates[3];

        // Get the x and z coordinates of hitBoxOther
        Vector3f[] otherCoordinates = hitBoxOther.getCoordinates();
        Vector3f otherFrontLeft = otherCoordinates[0];
        Vector3f otherFrontRight = otherCoordinates[1];
        Vector3f otherRearRight = otherCoordinates[2];
        Vector3f otherRearLeft = otherCoordinates[3];

        // Calculate the axes to test for overlap
        Vector3f[] axes = new Vector3f[4];

        // For all axes create new vectors
        for (int i = 0; i < axes.length; i++) {
            axes[i] = new Vector3f();
        }

        selfFrontRight.sub(selfFrontLeft, axes[0]);
        selfFrontRight.sub(selfRearRight, axes[1]);
        otherFrontLeft.sub(otherRearLeft, axes[2]);
        otherFrontLeft.sub(otherFrontRight, axes[3]);


        // Project each rectangle onto the axes and check for overlap
        for (Vector3f axis : axes) {
            axis.normalize();
            float selfMin = Float.MAX_VALUE;
            float selfMax = -Float.MAX_VALUE;
            float otherMin = Float.MAX_VALUE;
            float otherMax = -Float.MAX_VALUE;

            // Project self onto the axis
            for (Vector3f point : Coordinates) {
                float projection = point.dot(axis);
                selfMin = Math.min(selfMin, projection);
                selfMax = Math.max(selfMax, projection);
            }

            // Project other onto the axis
            for (Vector3f point : otherCoordinates) {
                float projection = point.dot(axis);
                otherMin = Math.min(otherMin, projection);
                otherMax = Math.max(otherMax, projection);
            }

            // Check for overlap
            if (selfMax < otherMin || otherMax < selfMin) {
                // No overlap on this axis, so the rectangles do not collide
                return false;
            }
        }

        // All axes have overlap, so the rectangles collide
        return true;



//        // Check for all self corners if they are inside the other hitbox using the isPointInsideRectangle function
//        if (isPointInsideRectangle(otherCoordinates, selfFrontLeft.x, selfFrontLeft.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(otherCoordinates, selfFrontRight.x, selfFrontRight.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(otherCoordinates, selfRearRight.x, selfRearRight.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(otherCoordinates, selfRearLeft.x, selfRearLeft.z)) {
//            return true;
//        }
//
//        // Check if the other hitbox is inside the self hitbox
//        if (isPointInsideRectangle(Coordinates, otherFrontLeft.x, otherFrontLeft.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(Coordinates, otherFrontRight.x, otherFrontRight.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(Coordinates, otherRearRight.x, otherRearRight.z)) {
//            return true;
//        }
//        if (isPointInsideRectangle(Coordinates, otherRearLeft.x, otherRearLeft.z)) {
//            return true;
//        }
//        // If none of the above is true, the hitboxes do not overlap
//        return false;
    }

    public static boolean isPointInsideRectangle(Vector3f[] coordinates, double pointX, double pointY) {

        // Get the x and z coordinates of the rectangle
        double frontLeftX = coordinates[0].x;
        double frontLeftZ = coordinates[0].z;
        double frontRightX = coordinates[1].x;
        double frontRightZ = coordinates[1].z;
        double backRightX = coordinates[2].x;
        double backRightZ = coordinates[2].z;
        double backLeftX = coordinates[3].x;
        double backLeftZ = coordinates[3].z;


        // Determine the minimum and maximum x and y values of the rectangle
        double minX = Math.min(Math.min(frontLeftX, frontRightX), Math.min(backLeftX, backRightX));
        double maxX = Math.max(Math.max(frontLeftX, frontRightX), Math.max(backLeftX, backRightX));
        double minY = Math.min(Math.min(frontLeftZ, frontRightZ), Math.min(backLeftZ, backRightZ));
        double maxY = Math.max(Math.max(frontLeftZ, frontRightZ), Math.max(backLeftZ, backRightZ));

        // Check if the point is inside the rectangle
        if (pointX >= minX && pointX <= maxX && pointY >= minY && pointY <= maxY) {

            // Use cross product to determine if the point is on the "right" or "left" side of each line
            double ABx = frontRightX - frontLeftX;
            double ABy = frontRightZ - frontLeftZ;
            double APx = pointX - frontLeftX;
            double APy = pointY - frontLeftZ;
            double crossProductABAP = ABx * APy - ABy * APx;

            double BCx = backRightX - frontRightX;
            double BCy = backRightZ - frontRightZ;
            double BPx = pointX - frontRightX;
            double BPy = pointY - frontRightZ;
            double crossProductBCBP = BCx * BPy - BCy * BPx;

            double CDx = backLeftX - backRightX;
            double CDy = backLeftZ - backRightZ;
            double CPx = pointX - backRightX;
            double CPy = pointY - backRightZ;
            double crossProductCDCP = CDx * CPy - CDy * CPx;

            double DEx = frontLeftX - backLeftX;
            double DEy = frontLeftZ - backLeftZ;
            double DExP = pointX - backLeftX;
            double DEyP = pointY - backLeftZ;
            double crossProductDEDExP = DEx * DEyP - DEy * DExP;

            // If the point is on the same side of all lines, it is inside the rectangle
            if ((crossProductABAP <= 0 && crossProductBCBP >= 0 && crossProductCDCP >= 0 && crossProductDEDExP <= 0)
                    || (crossProductABAP >= 0 && crossProductBCBP <= 0 && crossProductCDCP <= 0 && crossProductDEDExP >= 0)) {
                return true;
            }
        }

        // If the point is outside the rectangle or not on the same side of all lines, it is not inside the rectangle
        return false;
    }

}
