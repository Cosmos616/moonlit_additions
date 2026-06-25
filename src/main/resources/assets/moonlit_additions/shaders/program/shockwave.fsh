#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InvProjMat;

uniform vec3 ShockwaveCenterView;
uniform float ShockwaveRadius;
uniform float ShockwaveAlpha;

in vec2 texCoord;

out vec4 fragColor;

vec3 reconstructViewPosition(vec2 uv, float depth) {
    vec2 ndc = uv * 2.0 - 1.0;
    float clipZ = depth * 2.0 - 1.0;

    vec4 clipPos = vec4(ndc, clipZ, 1.0);
    vec4 viewPos = InvProjMat * clipPos;

    viewPos /= viewPos.w;

    return viewPos.xyz;
}

bool raySphere(
    vec3 rayOrigin,
    vec3 rayDir,
    vec3 sphereCenter,
    float radius,
out float tFront,
out float tBack
) {
    vec3 oc = rayOrigin - sphereCenter;

    float b = dot(oc, rayDir);
    float c = dot(oc, oc) - radius * radius;

    float h = b * b - c;

    if (h < 0.0) {
        return false;
    }

    h = sqrt(h);

    tFront = -b - h;
    tBack = -b + h;

    return tBack > 0.0;
}

void main() {
    vec4 scene = texture(DiffuseSampler, texCoord);

    float depth = texture(DepthSampler, texCoord).r;
    bool hasTerrain = depth < 0.9999;

    vec2 ndc = texCoord * 2.0 - 1.0;

    vec4 farPoint = InvProjMat * vec4(ndc, 1.0, 1.0);
    farPoint /= farPoint.w;

    vec3 rayOrigin = vec3(0.0);
    vec3 rayDir = normalize(farPoint.xyz);

    vec3 shockwaveColor = vec3(0.1, 0.65, 1.0);

    float sphereEffect = 0.0;
    float intersectionEffect = 0.0;

    // How far away the real terrain is along this view ray.
    // If this pixel is sky, terrain is infinitely far away.
    float sceneT = 1000000.0;
    vec3 terrainViewPos = vec3(0.0);

    if (hasTerrain) {
        terrainViewPos = reconstructViewPosition(texCoord, depth);
        sceneT = length(terrainViewPos);
    }

    // -----------------------------
    // 1. Render the visible hollow sphere shell
    // -----------------------------
    float tFront;
    float tBack;

    if (raySphere(rayOrigin, rayDir, ShockwaveCenterView, ShockwaveRadius, tFront, tBack)) {
        float tHit = tFront;

        // If the camera is inside the sphere, use the back surface.
        if (tHit < 0.0) {
            tHit = tBack;
        }

        // Terrain blocks the sphere if terrain is closer than the sphere surface.
        bool blockedByTerrain = hasTerrain && sceneT < tHit;

        if (!blockedByTerrain && tHit > 0.0) {
            vec3 hitPos = rayOrigin + rayDir * tHit;
            vec3 normal = normalize(hitPos - ShockwaveCenterView);

            // Rim is strongest at grazing angles.
            float facing = max(dot(normal, -rayDir), 0.0);
            float rim = pow(1.0 - facing, 2.25);

            // This keeps it mostly hollow instead of a solid filled ball.
            float softInterior = pow(1.0 - facing, 5.0) * 0.25;

            sphereEffect = rim + softInterior;
            sphereEffect *= ShockwaveAlpha;
        }
    }

    // -----------------------------
    // 2. Draw the terrain intersection band
    // -----------------------------
    if (hasTerrain) {
        float distToCenter = distance(terrainViewPos, ShockwaveCenterView);

        float shellDistance = abs(distToCenter - ShockwaveRadius);

        float thickness = 0.25;
        float softness = 0.35;

        intersectionEffect = 1.0 - smoothstep(
            thickness,
            thickness + softness,
            shellDistance
        );

        intersectionEffect *= ShockwaveAlpha;
    }

    // -----------------------------
    // 3. Combine
    // -----------------------------

    // The sphere shell itself, visible in air/sky but blocked by terrain.
    scene.rgb += shockwaveColor * sphereEffect * 0.85;
    scene.rgb = mix(scene.rgb, shockwaveColor, sphereEffect * 0.25);

    // The ground contact line where the shell intersects terrain.
    scene.rgb += shockwaveColor * intersectionEffect * 1.15;
    scene.rgb = mix(scene.rgb, shockwaveColor, intersectionEffect * 0.35);

    fragColor = vec4(scene.rgb, 1.0);
}