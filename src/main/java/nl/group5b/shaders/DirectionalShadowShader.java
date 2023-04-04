package nl.group5b.shaders;


import nl.group5b.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class DirectionalShadowShader extends ShaderProgram {

	private static final String VERTEX_FILE = "shadow_vertex_shader.glsl";
	private static final String FRAGMENT_FILE = "shadow_fragment_shader.glsl";
	
	private int mvpMatrixLocation;

	public DirectionalShadowShader() {
		// lightCount can be set to 0 here, since this is not important for the shadow shader
		super(VERTEX_FILE, FRAGMENT_FILE, 0);
	}

	@Override
	protected void getAllUniformLocations() {
		mvpMatrixLocation = super.getUniformLocation("mvpMatrix");
	}
	
	public void loadMvpMatrix(Matrix4f mvpMatrix){
		super.loadMatrix(mvpMatrixLocation, mvpMatrix);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "inPosition");
	}

}
