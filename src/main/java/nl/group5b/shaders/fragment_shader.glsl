# version 460

in vec3 surfaceNormal;
in vec3 toLight;

out vec4 outColour;

uniform vec3 lightColour;
uniform float damping;
uniform float shininess;

void main(void) {
    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLight = normalize(toLight);

    float prod = dot(unitNormal, unitLight);
    float brightness = clamp(prod, 0.0, 1.0);

    vec3 diffuse = vec3(0.1, 0.05, 0.7) * lightColour * brightness;

    outColour = vec4(diffuse, 1.0);
}