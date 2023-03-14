package nl.group5b.model;

import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OBJLoader {

    public static Model loadOBJ(String fileName, ModelLoader modelLoader) throws FileNotFoundException {
        FileReader fileReader = null;
        try {
             fileReader = new FileReader(new File("res/" + fileName + ".obj"));
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName + ".obj");
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // Initialize variables
        String line;
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        float[] verticesArray = null;
        float[] normalsArray = null;
        int[] indicesArray = null;

        // Parse the file
        try {
            while (true) {
                line = bufferedReader.readLine();
                String[] splitLine = line.split(" ");
                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]),
                            Float.parseFloat(splitLine[3]));
                    vertices.add(vertex);
                }
                else if (line.startsWith("vn ")) {
                    Vector3f normal = new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]),
                            Float.parseFloat(splitLine[3]));
                    normals.add(normal);
                }
                else if (line.startsWith("f ")) {
                    normalsArray = new float[vertices.size() * 3];
                    break;
                }
            }

            while (line != null) {
                if (!line.startsWith("f ")) {
                    line = bufferedReader.readLine();
                    continue;
                }
                String[] splitLine = line.split(" ");
                String[] vertex1 = splitLine[1].split("/");
                String[] vertex2 = splitLine[2].split("/");
                String[] vertex3 = splitLine[3].split("/");
                processVertex(vertex1, indices, normals, normalsArray);
                processVertex(vertex2, indices, normals, normalsArray);
                processVertex(vertex3, indices, normals, normalsArray);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize arrays
        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

        // Convert vertices to array
        int vertexPointer = 0;
        for (Vector3f vertex : vertices) {
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        // Convert indices to array
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }

        // Return the model
        return modelLoader.loadToVAO(verticesArray, normalsArray, indicesArray);
    }

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector3f> normals,
                                      float[] normalsArray) {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.add(currentVertexPointer);
        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2]) - 1);
        normalsArray[currentVertexPointer * 3] = currentNormal.x;
        normalsArray[currentVertexPointer * 3 + 1] = currentNormal.y;
        normalsArray[currentVertexPointer * 3 + 2] = currentNormal.z;
    }
}
