package nl.group5b.util;

import nl.group5b.camera.Camera;
import nl.group5b.engine.DisplayBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.Vector;

public class Algebra {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.translate(translation);
        matrix.rotateXYZ(
                (float) Math.toRadians(rotation.x()),
                (float) Math.toRadians(rotation.y()),
                (float) Math.toRadians(rotation.z())
        );
        matrix.scale(new Vector3f(scale, scale, scale));
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotateXYZ(
                (float) Math.toRadians(camera.getRotation().x()),
                (float) Math.toRadians(camera.getRotation().y()),
                (float) Math.toRadians(camera.getRotation().z())
        );
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }

    public static Vector3f rotatePointAroundPivot(Vector3f point, Vector3f pivot, float angle) {
        return new Vector3f(point).rotateY((float) Math.toRadians(angle)).add(pivot.x, 0, pivot.z);
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
        wheelRotation.rotateY((float) Math.toRadians(angle));

        // Create a rotation matrix for the amount to rotate forward
        Matrix4f forwardRotation = new Matrix4f();
        forwardRotation.rotateX((float) Math.toRadians(amount));

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
        float aspectRatio = (float) width / (float) height;
        float fovRadians = (float) Math.toRadians(fov);
        return new Matrix4f().perspective(fovRadians, aspectRatio,
                Settings.VIEWPORT_NEAR_PLANE, Settings.VIEWPORT_FAR_PLANE);
    }
}
