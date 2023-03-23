#version 460

#define LIGHT_COUNT 1 // Will be overwritten upon compilation

in vec3 position;
in vec3 normal;

out vec3 surfaceNormal;
out vec3 toLight[LIGHT_COUNT];
out vec3 toCamera;
out vec4 shadowCoordinates;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform mat4 toShadowMapSpace;

uniform vec4 lightPosition[LIGHT_COUNT];

void main(void) {
    // Compute the position of the vertex in world space
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    shadowCoordinates = toShadowMapSpace * worldPosition;

    // Compute the vertex position in eye space
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    // Compute the vertex's normal in eye space
    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

    // Compute the vector from the vertex to the light in eye space
    for (int i = 0; i < LIGHT_COUNT; i++) {
        // Check if the light is directional or a point light
        if (lightPosition[i].w == 0.0) {
            toLight[i] = lightPosition[i].xyz;
        } else {
            toLight[i] = lightPosition[i].xyz - worldPosition.xyz;
        }
    }

    // Compute the vector from the vertex to the camera in eye space
    toCamera = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
}