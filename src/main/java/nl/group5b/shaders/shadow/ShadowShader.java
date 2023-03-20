package nl.group5b.shaders.shadow;


import nl.group5b.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class ShadowShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/shadow/vertex_shader.glsl";
	private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/shadow/fragment_shader.glsl";
	
	private int mvpMatrixLocation;

	protected ShadowShader() {
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
