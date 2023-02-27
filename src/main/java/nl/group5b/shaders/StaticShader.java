package nl.group5b.shaders;

import nl.group5b.models.Model;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/java/nl/group5b/shaders/vertex_shader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/nl/group5b/shaders/fragment_shader.glsl";

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Model.POSITION_ATTR, "position");
    }
}