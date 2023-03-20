package nl.group5b.light;

import nl.group5b.camera.Camera;
import nl.group5b.engine.DisplayBuilder;
import nl.group5b.engine.MasterRenderer;
import nl.group5b.engine.Renderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class ShadowBox {

	private static final float OFFSET = 10;
	private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
	private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
	private static final float SHADOW_RANGE = 100;

	private float minX, maxX;
	private float minY, maxY;
	private float minZ, maxZ;
	private Matrix4f lightViewMatrix;

	private Camera camera;

	private float farHeight, farWidth, nearHeight, nearWidth;

	protected ShadowBox(Matrix4f lightViewMatrix, Camera camera) {
		this.lightViewMatrix = lightViewMatrix;
		this.camera = camera;
	}

	protected void update() {
		updateWidthsAndHeights();

		Matrix4f rotation = calculateCameraRotationMatrix();
		Vector4f newRotation = new Vector4f();
		rotation.transform(FORWARD, newRotation);
		Vector3f forwardVector = new Vector3f(newRotation.x, newRotation.y, newRotation.z);

		Vector3f toFar = new Vector3f(forwardVector);
		toFar.mul(SHADOW_RANGE);
		Vector3f toNear = new Vector3f(forwardVector);
		toNear.mul(Renderer.getNearPlane());
        Vector3f cameraPosition = camera.getPosition();
		Vector3f centerNear = new Vector3f();
		toNear.add(cameraPosition, centerNear);
		Vector3f centerFar = new Vector3f();
		toFar.add(cameraPosition, centerFar);

		Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear,
				centerFar);

		boolean first = true;
		for (Vector4f point : points) {
			if (first) {
				minX = point.x;
				maxX = point.x;
				minY = point.y;
				maxY = point.y;
				minZ = point.z;
				maxZ = point.z;
				first = false;
				continue;
			}
			if (point.x > maxX) {
				maxX = point.x;
			} else if (point.x < minX) {
				minX = point.x;
			}
			if (point.y > maxY) {
				maxY = point.y;
			} else if (point.y < minY) {
				minY = point.y;
			}
			if (point.z > maxZ) {
				maxZ = point.z;
			} else if (point.z < minZ) {
				minZ = point.z;
			}
		}
		maxZ += OFFSET;

	}

	protected Vector3f getCenter() {
		float x = (minX + maxX) / 2f;
		float y = (minY + maxY) / 2f;
		float z = (minZ + maxZ) / 2f;
		Vector4f center = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
        lightViewMatrix.invert(invertedLight);
		Vector4f newCenter = invertedLight.transform(center);
		return new Vector3f(newCenter.x, newCenter.y, newCenter.z);
	}

	protected float getWidth() {
		return maxX - minX;
	}

	protected float getHeight() {
		return maxY - minY;
	}

	protected float getLength() {
		return maxZ - minZ;
	}

	private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
			Vector3f centerNear, Vector3f centerFar) {
		Vector4f newRotation = new Vector4f();
		rotation.transform(UP, newRotation);
		Vector3f upVector = new Vector3f(newRotation.x, newRotation.y, newRotation.z);
		Vector3f rightVector = new Vector3f();
		forwardVector.cross(upVector, rightVector);
		Vector3f downVector = new Vector3f(-upVector.x, -upVector.y, -upVector.z);
		Vector3f leftVector = new Vector3f(-rightVector.x, -rightVector.y, -rightVector.z);

        Vector3f farTop = new Vector3f();
        Vector3f farBottom = new Vector3f();
        centerFar.add(new Vector3f(upVector.x * farHeight,
				upVector.y * farHeight, upVector.z * farHeight), farTop);
		centerFar.add(new Vector3f(downVector.x * farHeight,
				downVector.y * farHeight, downVector.z * farHeight), farBottom);

        Vector3f nearTop = new Vector3f();
        Vector3f nearBottom = new Vector3f();
        centerNear.add(new Vector3f(upVector.x * nearHeight,
				upVector.y * nearHeight, upVector.z * nearHeight), nearTop);
        centerNear.add(new Vector3f(downVector.x * nearHeight,
				downVector.y * nearHeight, downVector.z * nearHeight), nearBottom);

        Vector4f[] points = new Vector4f[8];
		points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
		points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
		points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
		points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
		points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
		points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
		points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
		points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);

        return points;
	}

	private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
			float width) {
		Vector3f point = new Vector3f();
		startPoint.add(new Vector3f(direction.x * width, direction.y * width, direction.z * width), point);
		Vector4f point4f = new Vector4f(point.x, point.y, point.z, 1f);
        lightViewMatrix.transform(point4f, point4f);
		return point4f;
	}

	private Matrix4f calculateCameraRotationMatrix() {
		Matrix4f rotation = new Matrix4f();
		rotation.rotate((float) Math.toRadians(-camera.getRotation().y()), new Vector3f(0, 1, 0));
		rotation.rotate((float) Math.toRadians(-camera.getRotation().x()), new Vector3f(1, 0, 0));
		return rotation;
	}

	private void updateWidthsAndHeights() {
        float fovTan = (float) Math.tan(Math.toRadians(Renderer.getFOV()));
		farWidth = SHADOW_RANGE * fovTan;
		nearWidth = Renderer.getNearPlane() * fovTan;
		farHeight = farWidth / getAspectRatio();
		nearHeight = nearWidth / getAspectRatio();
	}

	private float getAspectRatio() {
		return (float) DisplayBuilder.getWidth() / (float) DisplayBuilder.getHeight();
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
}
