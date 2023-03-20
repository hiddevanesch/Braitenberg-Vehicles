package nl.group5b.light;

import nl.group5b.camera.Camera;
import nl.group5b.engine.MasterRenderer;
import nl.group5b.model.BodyElement;
import nl.group5b.model.Model;
import nl.group5b.shaders.shadow.ShadowShader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class ShadowMasterRenderer {

	private static final int SHADOW_MAP_SIZE = 1024;

	private ShadowFrameBuffer shadowFbo;
	private ShadowShader shader;
	private ShadowBox shadowBox;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f lightViewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();
	private Matrix4f offset = createOffset();
    private Vector3f lightDirection;

	private ShadowRenderer shadowRenderer;

	public ShadowMasterRenderer(Light sun) {
		shader = new ShadowShader();
		shadowBox = new ShadowBox(lightViewMatrix);
		shadowFbo = new ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
		shadowRenderer = new ShadowRenderer(shader, projectionViewMatrix);

        Vector4f sunPosition = sun.getPosition();
        lightDirection = new Vector3f(-sunPosition.x, -sunPosition.y, -sunPosition.z);
	}

	public void render(Map<Model, List<BodyElement>> renderMap) {
		shadowBox.update();
		prepare(lightDirection, shadowBox);
		shadowRenderer.render(renderMap);
		finish();
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

	private void prepare(Vector3f lightDirection, ShadowBox box) {
		updateOrthoProjectionMatrix(box.getWidth(), box.getHeight(), box.getLength());
		updateLightViewMatrix(lightDirection, box.getCenter());
        projectionMatrix.mul(lightViewMatrix, projectionViewMatrix);
		shadowFbo.bindFrameBuffer();
		GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT);
		shader.start();
	}

	private void finish() {
		shader.stop();
		shadowFbo.unbindFrameBuffer();
	}

	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		Vector3f newDirection = new Vector3f(direction);
		Vector3f newCenter = new Vector3f(center);
		newDirection.normalize();
		newCenter.negate();

		float pitch = (float) Math.acos(new Vector2f(newDirection.x, newDirection.z).length());
        float yaw = (float) Math.toDegrees(((float) Math.atan(newDirection.x / newDirection.z)));
        yaw = newDirection.z > 0 ? yaw - 180 : yaw;

        lightViewMatrix.identity();
        lightViewMatrix.rotate(pitch, new Vector3f(1, 0, 0));
        lightViewMatrix.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0));
        lightViewMatrix.translate(newCenter);
	}

	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		projectionMatrix.identity();
		projectionMatrix.m00(2f / width);
		projectionMatrix.m11(2f / height);
		projectionMatrix.m22(-2f / length);
	}

	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
}
