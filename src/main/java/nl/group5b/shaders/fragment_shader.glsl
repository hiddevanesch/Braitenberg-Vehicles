# version 460

in vec3 surfaceNormal;
in vec3 toLight;
in vec3 toCamera;

out vec4 outColour;

uniform vec3 lightColour;
uniform float damping;
uniform float shininess;

void main(void) {
    // Normalise the vectors
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLight = normalize(toLight);
    vec3 unitCamera = normalize(toCamera);

    // Calculate the diffuse component
    float prod = dot(unitNormal, unitLight);
    float brightness = clamp(prod, 0.0, 1.0);
    vec3 diffuse = vec3(0.1, 0.05, 0.7) * lightColour * brightness;

    // Calculate the specular component
    vec3 lightDirection = -unitLight;
    vec3 reflectedDirection = reflect(lightDirection, unitNormal);
    float specularFactor = dot(unitCamera, reflectedDirection);
    specularFactor = clamp(specularFactor, 0.0, 1.0);
    float dampedFactor = pow(specularFactor, damping);
    vec3 specular = dampedFactor * shininess * lightColour;

    // Compute out colour
    outColour = vec4(diffuse, 1.0) + vec4(specular, 1.0);
}