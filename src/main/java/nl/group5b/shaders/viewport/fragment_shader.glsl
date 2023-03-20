# version 460

#define LIGHT_COUNT 1

in vec3 surfaceNormal;
in vec3 toLight[LIGHT_COUNT];
in vec3 toCamera;

out vec4 outColour;

uniform vec3 lightColour[LIGHT_COUNT];
uniform vec3 lightAttenuation[LIGHT_COUNT];
uniform vec3 colour;
uniform float damping;
uniform float shininess;

void main(void) {
    // Normalise the vectors
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitCamera = normalize(toCamera);

    vec3 totalDiffuse = vec3(0.0, 0.0, 0.0);
    vec3 totalSpecular = vec3(0.0, 0.0, 0.0);

    for (int i = 0; i < LIGHT_COUNT; i++) {
        float distanceToLight = length(toLight[i]);
        float attenuationFactor = lightAttenuation[i].x + lightAttenuation[i].y * distanceToLight
            + lightAttenuation[i].z * distanceToLight * distanceToLight;

        vec3 unitLight = normalize(toLight[i]);

        // Calculate the diffuse component
        float prod = dot(unitNormal, unitLight);
        float brightness = clamp(prod, 0.0, 1.0);
        totalDiffuse += (colour * lightColour[i] * brightness) / attenuationFactor;

        // Calculate the specular component
        vec3 lightDirection = -unitLight;
        vec3 reflectedDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(unitCamera, reflectedDirection);
        specularFactor = clamp(specularFactor, 0.0, 1.0);
        float dampedFactor = pow(specularFactor, damping);
        totalSpecular += (dampedFactor * shininess * lightColour[i]) / attenuationFactor;
    }

    // Add ambient light
    totalDiffuse = max(totalDiffuse, 0.15 * colour);

    // Compute out colour
    outColour = vec4(totalDiffuse, 1.0) + vec4(totalSpecular, 1.0);
}