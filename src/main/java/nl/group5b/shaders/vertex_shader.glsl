# version 460

in vec3 position;
in vec3 normal;

out vec3 surfaceNormal;
out vec3 toLight;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition;

void main(void) {
    // Compute the position of the vertex in world space
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    // Compute the vertex position in eye space
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    toLight = lightPosition - worldPosition.xyz;
}