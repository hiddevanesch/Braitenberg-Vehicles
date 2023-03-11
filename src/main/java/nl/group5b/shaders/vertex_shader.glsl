#version 460

#define LIGHT_COUNT

in vec3 position;
in vec3 normal;

out vec3 surfaceNormal;
out vec3 toLight;
out vec3 toCamera;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition;

void main(void) {
    // Compute the position of the vertex in world space
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    // Compute the vertex position in eye space
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    // Compute the vertex's normal in eye space
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

    // Compute the vector from the vertex to the light in eye space
    toLight = lightPosition - worldPosition.xyz;

    // Compute the vector from the vertex to the camera in eye space
    toCamera = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}