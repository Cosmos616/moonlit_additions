#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform vec4 SphereColor;
uniform float GameTime;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec2 p = texCoord0 * 2.0 - 1.0;

    float dist = length(p);

    if (dist > 1.0) {
        discard;
    }

    // Fake sphere depth.
    float z = sqrt(1.0 - dist * dist);

    vec3 normal = normalize(vec3(p.x, p.y, z));

    // Fake light direction.
    vec3 lightDir = normalize(vec3(-0.4, 0.7, 0.8));

    float diffuse = max(dot(normal, lightDir), 0.0);

    // Rim glow gets stronger near sphere edge.
    float rim = pow(1.0 - z, 2.0);

    // Soft edge fade.
    float edgeFade = smoothstep(1.0, 0.75, dist);

    // Subtle animated shimmer.
    float shimmer = sin((p.y * 12.0) + GameTime * 250.0) * 0.5 + 0.5;

    vec3 color = SphereColor.rgb;
    color *= 0.25 + diffuse * 0.75;
    color += rim * vec3(0.2, 0.8, 1.0);
    color += shimmer * 0.08;

    float alpha = SphereColor.a * edgeFade;
    alpha += rim * 0.25;

    fragColor = vec4(color, alpha) * vertexColor * ColorModulator;
}