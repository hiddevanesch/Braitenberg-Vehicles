package nl.group5b.light;

import nl.group5b.model.BodyElement;
import nl.group5b.model.Entity;
import nl.group5b.model.Model;
import nl.group5b.shaders.shadow.ShadowShader;
import nl.group5b.util.Algebra;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.util.List;
import java.util.Map;

public class ShadowRenderer {

	private Matrix4f projectionViewMatrix;
	private ShadowShader shader;

	public ShadowRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
		this.shader = shader;
		this.projectionViewMatrix = projectionViewMatrix;
	}

	public void render(Map<Model, List<BodyElement>> renderMap) {
		// Iterate over all the different models
		for (Model model : renderMap.keySet()) {
			prepareModel(model);

			// Get all the BodyElements that use this model
			List<BodyElement> bodyElements = renderMap.get(model);

			for (BodyElement bodyElement : bodyElements) {
				prepareInstance(bodyElement);

				// Render the BodyElement by drawing the triangles
				GL46.glDrawElements(GL46.GL_TRIANGLES, model.getVertexCount(), GL46.GL_UNSIGNED_INT, 0);
			}
			unbindModel();
		}
		GL46.glDisableVertexAttribArray(0);
		GL46.glBindVertexArray(0);
	}

	private void prepareModel(Model model) {
		// Bind the VAO
		GL46.glBindVertexArray(model.getVaoID());
		GL46.glEnableVertexAttribArray(Model.POSITION_ATTR);
	}

	private void unbindModel () {
		// Unbind the VAO
		GL46.glDisableVertexAttribArray(Model.POSITION_ATTR);
		GL46.glBindVertexArray(0);
	}

	private void prepareInstance(BodyElement bodyElement) {
		Entity entity = bodyElement.getEntity();

		// Create model matrix
		Matrix4f modelMatrix = Algebra.createTransformationMatrix(entity.getPosition(),
				entity.getRotation(), entity.getScale());

		// Create MVP matrix
		Matrix4f mvpMatrix = new Matrix4f(projectionViewMatrix).mul(modelMatrix);

		// Load MVP matrix into shader
		shader.loadMvpMatrix(mvpMatrix);
	}

}
