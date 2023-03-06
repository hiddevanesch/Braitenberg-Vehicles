package nl.group5b.models;

public class Model {

    public static final int POSITION_ATTR = 0;
    public static final int NORMAL_ATTR = 2;

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
