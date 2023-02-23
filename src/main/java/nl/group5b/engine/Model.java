package nl.group5b.engine;

public class Model {

    public static final int POSITION_ATTR = 0;

    private int vaoID;
    private int vertexCount;

    public Model(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
