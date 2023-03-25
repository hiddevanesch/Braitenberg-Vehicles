package nl.group5b.util;

import nl.group5b.camera.Camera;
import nl.group5b.model.HitBox;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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

    // Function that takes two hitboxes and returns true if they overlap.
    // The y (height) axis is not taken into account.
    // The hitboxes can be rotated
    public static boolean hitboxOverlap(HitBox hitBoxSelf, HitBox hitBoxOther) {
        // Get the x and z coordinates of hitBoxSelf
        Vector3f[] coordinatesSelf = hitBoxSelf.getCoordinates();
        Vector3f selfFrontLeft = coordinatesSelf[0];
        Vector3f selfFrontRight = coordinatesSelf[1];
        Vector3f selfBackRight = coordinatesSelf[2];
        Vector3f selfBackLeft = coordinatesSelf[3];

        // Get the x and z coordinates of hitBoxOther
        Vector3f[] coordinatesOther = hitBoxOther.getCoordinates();
        Vector3f otherFrontLeft = coordinatesOther[0];
        Vector3f otherFrontRight = coordinatesOther[1];
        Vector3f otherBackRight = coordinatesOther[2];
        Vector3f otherBackLeft = coordinatesOther[3];

        // Check if the hitboxes overlap, taking into account the rotation of the hitboxes
        Boolean ans = isRectanglesIntersecting(new double[] {selfFrontLeft.x, selfFrontLeft.z, selfFrontRight.x, selfFrontRight.z, selfBackRight.x, selfBackRight.z, selfBackLeft.x, selfBackLeft.z},
                new double[] {otherFrontLeft.x, otherFrontLeft.z, otherFrontRight.x, otherFrontRight.z, otherBackRight.x, otherBackRight.z, otherBackLeft.x, otherBackLeft.z});
        return ans;
    }

    // Function that takes two rectangles in the form [x1z1, x2z2, x3z3, x4z4] and returns true if they overlap
    // Based on the separating axis theorem
    static boolean isRectanglesIntersecting(double[] rectA, double[] rectB)
    {
        for (int x=0; x<2; x++)
        {
            double[] rect = (x==0) ? rectA : rectB;
            for (int i1=0; i1<4; i1+=2)
            {
                int   i2 = (i1 + 2) % 4;
                double x1 = rect[i1];
                double z1 = rect[i1+1];
                double x2 = rect[i2];
                double z2 = rect[i2+1];

                double normalX = z2 - z1;
                double normalZ = x1 - x2;

                double minA = Double.POSITIVE_INFINITY;
                double maxA = Double.NEGATIVE_INFINITY;

                for (int j=0; j<8; j+=2)
                {
                    double projected = normalX * rectA[j] + normalZ * rectA[j+1];

                    if (projected < minA)
                        minA = projected;
                    if (projected > maxA)
                        maxA = projected;
                }

                double minB = Double.POSITIVE_INFINITY;
                double maxB = Double.NEGATIVE_INFINITY;

                for (int j=0; j<8; j+=2)
                {
                    double projected = normalX * rectB[j] + normalZ * rectB[j+1];

                    if (projected < minB)
                        minB = projected;
                    if (projected > maxB)
                        maxB = projected;
                }

                if (maxA < minB || maxB < minA)
                    return false;
            }
        }

        return true;
    }
}
