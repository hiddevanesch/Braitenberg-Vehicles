package nl.group5b.shaders.shadow.point;

import nl.group5b.shaders.ShaderProgram;
import org.joml.Matrix4f;

public class PointShadowShader extends ShaderProgram {
    private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/shadow/point/vertex_shader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/shadow/point/fragment_shader.glsl";

    private int mvpMatrixLocations[];

    public PointShadowShader() {
        // lightCount can be set to 0 here, since this is not important for the shadow shader
        super(VERTEX_FILE, FRAGMENT_FILE, 0);
    }

    @Override
    protected void getAllUniformLocations() {
        mvpMatrixLocations = new int[6];

        for (int i = 0; i < 6; i++) {
            mvpMatrixLocations[i] = super.getUniformLocation("mvpMatrix[" + i + "]");
        }
    }

    public void loadMvpMatrix(int sideIndex, Matrix4f mvpMatrix) {
        super.loadMatrix(mvpMatrixLocations[sideIndex], mvpMatrix);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "inPosition");
    }
}
