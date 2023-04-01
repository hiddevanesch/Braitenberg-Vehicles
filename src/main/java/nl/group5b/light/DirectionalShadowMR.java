package nl.group5b.light;

import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.shaders.shadow.DirectionalShadowShader;
import nl.group5b.util.Settings;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class DirectionalShadowMR {

	private static final float SHADOW_BOX_MIN_X = -Settings.ARENA_RADIUS;
	private static final float SHADOW_BOX_MAX_X = Settings.ARENA_RADIUS;
	private static final float SHADOW_BOX_MIN_Y = -Settings.ARENA_RADIUS;
	private static final float SHADOW_BOX_MAX_Y = Settings.ARENA_RADIUS;
	private static final float SHADOW_BOX_MIN_Z = -Settings.ARENA_RADIUS;
	private static final float SHADOW_BOX_MAX_Z = Settings.ARENA_RADIUS;

	private final DirectionalShadowFBO shadowFbo;
	private final DirectionalShadowShader shader;
	private final Matrix4f projectionMatrix = new Matrix4f();
	private final Matrix4f lightViewMatrix = new Matrix4f();
	private final Matrix4f projectionViewMatrix = new Matrix4f();
	private final Matrix4f offset = createOffset();
    private final Vector3f lightDirection;

	private final DirectionalShadowRenderer shadowRenderer;

	public DirectionalShadowMR(Light sun) {
		shader = new DirectionalShadowShader();
		shadowFbo = new DirectionalShadowFBO(Settings.SHADOW_MAP_RESOLUTION, Settings.SHADOW_MAP_RESOLUTION);
		shadowRenderer = new DirectionalShadowRenderer(shader, projectionViewMatrix);

		// Compute the light direction
        Vector4f sunPosition = sun.getPosition();
        lightDirection = new Vector3f(-sunPosition.x, -sunPosition.y, -sunPosition.z);

		setProjectionMatrix();

		setLightViewMatrix(lightDirection, getCenter());

		// Set projection view matrix
		projectionMatrix.mul(lightViewMatrix, projectionViewMatrix);
	}

	public void render(Map<Model, List<BodyElement>> renderMap) {
		shadowFbo.bindFrameBuffer();
		GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);

		shader.start();
		shadowRenderer.render(renderMap);
		shader.stop();

		shadowFbo.unbindFrameBuffer();
	}

	public Matrix4f getToShadowMapSpaceMatrix() {
		return new Matrix4f(offset).mul(projectionViewMatrix);
	}

	public void cleanUp() {
		shader.cleanUp();
		shadowFbo.cleanUp();
	}

	public int getShadowMap() {
		return shadowFbo.getShadowMap();
	}

	protected Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}

	private void setLightViewMatrix(Vector3f direction, Vector3f center) {
		Vector3f newDirection = new Vector3f(direction);
		Vector3f newCenter = new Vector3f(center);
		newDirection.normalize();
		newCenter.negate();

		float pitch = (float) Math.acos(new Vector2f(newDirection.x, newDirection.z).length());
		float yaw = (float) Math.toDegrees(Math.atan2(newDirection.x, newDirection.z));
		yaw += 180f;
		if (yaw < 0) {
			yaw += 360f;
		}

        lightViewMatrix.identity();
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix.translate(newCenter);
	}

	private void setProjectionMatrix() {
		projectionMatrix.identity();
		projectionMatrix.m00(2f / (SHADOW_BOX_MAX_X - SHADOW_BOX_MIN_X));
		projectionMatrix.m11(2f / (SHADOW_BOX_MAX_Y - SHADOW_BOX_MIN_Y));
		projectionMatrix.m22(-2f / (SHADOW_BOX_MAX_Z - SHADOW_BOX_MIN_Z));
	}

	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}

	private Vector3f getCenter() {
		float x = (SHADOW_BOX_MIN_X + SHADOW_BOX_MAX_X) / 2f;
		float y = (SHADOW_BOX_MIN_Y + SHADOW_BOX_MAX_Y) / 2f;
		float z = (SHADOW_BOX_MIN_Z + SHADOW_BOX_MAX_Z) / 2f;
		Vector4f center = new Vector4f(x, y, z, 1);
		Matrix4f invertedLight = new Matrix4f();
		lightViewMatrix.invert(invertedLight);
		Vector4f newCenter = invertedLight.transform(center);
		return new Vector3f(newCenter.x, newCenter.y, newCenter.z);
	}
}
