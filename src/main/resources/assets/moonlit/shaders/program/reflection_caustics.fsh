#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform mat4 InvProjMat;
uniform mat4 InvBobMat;
uniform mat4 CameraToWorldRotMat;
uniform vec3 CameraPos;

uniform float CausticTime;

in vec2 texCoord;

out vec4 fragColor;

const float TAU = 6.28318530718;


/* ------------------------------------------------------------------------- */
/* Position reconstruction                                                   */
/* ------------------------------------------------------------------------- */

vec3 reconstructViewPosition(
    vec2 uv,
    float depth
) {
    vec4 clipPosition = vec4(
    uv * 2.0 - 1.0,
    depth * 2.0 - 1.0,
    1.0
    );

    vec4 viewPosition =
    InvProjMat * clipPosition;

    return viewPosition.xyz / viewPosition.w;
}

vec3 reconstructWorldPosition(
    vec2 uv,
    float depth
) {
    vec3 bobbedViewPosition =
    reconstructViewPosition(uv, depth);

/*
     * Undo the exact transform applied by GameRenderer#bobView.
     *
     * Use w = 1 because the bob transform includes both rotation and
     * translation.
     */
    vec3 unbobbedViewPosition =
    (
    InvBobMat
    * vec4(bobbedViewPosition, 1.0)
    ).xyz;

/*
     * Convert the unbobbed camera-relative vector into world orientation.
     *
     * CameraToWorldRotMat is rotation-only, so use w = 0.
     */
    vec3 cameraRelativeWorldPosition =
    (
    CameraToWorldRotMat
    * vec4(unbobbedViewPosition, 0.0)
    ).xyz;

    return CameraPos + cameraRelativeWorldPosition;
}


/* ------------------------------------------------------------------------- */
/* Surface normal reconstruction                                             */
/* ------------------------------------------------------------------------- */

vec3 reconstructSurfaceNormal(
    vec3 worldPosition
) {
    vec3 positionDx = dFdx(worldPosition);
    vec3 positionDy = dFdy(worldPosition);

    vec3 normal = normalize(
        cross(positionDx, positionDy)
    );

    vec3 directionToCamera =
    normalize(CameraPos - worldPosition);

    if (dot(normal, directionToCamera) < 0.0) {
        normal = -normal;
    }

    return normal;
}


/* ------------------------------------------------------------------------- */
/* Hash functions                                                            */
/* ------------------------------------------------------------------------- */

float hash21(vec2 point) {
    point = fract(
        point * vec2(123.34, 456.21)
    );

    point += dot(
        point,
        point + 45.32
    );

    return fract(point.x * point.y);
}


vec2 hash22(vec2 point) {
    float firstValue = hash21(point);

    return vec2(
    firstValue,
    hash21(point + firstValue + 19.19)
    );
}


/* ------------------------------------------------------------------------- */
/* Pixel-grid coordinates                                                    */
/* ------------------------------------------------------------------------- */

vec2 snapToWorldPixelGrid(
    vec2 position,
    float pixelsPerBlock
) {
    return (
    floor(position * pixelsPerBlock)
    + 0.5
    ) / pixelsPerBlock;
}


/* ------------------------------------------------------------------------- */
/* Rounded caustic generation                                                */
/* ------------------------------------------------------------------------- */

float calculateRoundedCaustics(
    vec2 projectedWorldPosition,
    float time
) {
    const float cellsPerBlock = 0.70;

    vec2 position =
    projectedWorldPosition * cellsPerBlock;

    vec2 baseCell =
    floor(position);

    vec2 localPosition =
    fract(position);

    float nearestDistance = 1000.0;
    float secondNearestDistance = 1000.0;

    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 neighbor =
            vec2(float(x), float(y));

            vec2 cellId =
            baseCell + neighbor;

            vec2 randomPoint =
            hash22(cellId);

            float phase =
            hash21(
                cellId + vec2(7.13, 3.71)
            ) * TAU;

            vec2 movement = vec2(
            sin(time * 0.18 + phase),
            cos(time * 0.14 + phase * 1.31)
            ) * 0.18;

            vec2 deformation = vec2(
            sin(time * 0.11 + phase * 0.73),
            sin(time * 0.16 + phase * 1.57)
            ) * 0.07;

            vec2 cellPoint =
            neighbor
            + randomPoint
            + movement
            + deformation;

            float distanceToPoint =
            length(
                cellPoint - localPosition
            );

            if (
            distanceToPoint
            < nearestDistance
            ) {
                secondNearestDistance =
                nearestDistance;

                nearestDistance =
                distanceToPoint;
            } else if (
            distanceToPoint
            < secondNearestDistance
            ) {
                secondNearestDistance =
                distanceToPoint;
            }
        }
    }

    float edgeDistance =
    secondNearestDistance
    - nearestDistance;

    float roundedEdge =
    1.0
    - smoothstep(
        0.025,
        0.115,
        edgeDistance
    );

    float pulse =
    0.5
    + 0.5 * sin(
        time * 0.21
        + nearestDistance * 8.0
        + hash21(baseCell) * TAU
    );

    float innerGlow =
    1.0
    - smoothstep(
        0.07 + pulse * 0.025,
        0.42 + pulse * 0.035,
        nearestDistance
    );

    float caustics =
    roundedEdge * 0.88
    + innerGlow * 0.14;

    return pow(
        clamp(caustics, 0.0, 1.0),
        1.45
    );
}


/* ------------------------------------------------------------------------- */
/* Directional world projection                                              */
/* ------------------------------------------------------------------------- */

vec2 calculateProjectedCoordinates(
    vec3 worldPosition,
    vec3 projectionDirection
) {
    vec3 direction =
    normalize(projectionDirection);

    vec3 referenceAxis =
    vec3(0.0, 0.0, 1.0);

    if (
    abs(dot(referenceAxis, direction))
    > 0.98
    ) {
        referenceAxis =
        vec3(1.0, 0.0, 0.0);
    }

    vec3 projectionX = normalize(
        cross(referenceAxis, direction)
    );

    vec3 projectionY = normalize(
        cross(direction, projectionX)
    );

    return vec2(
    dot(worldPosition, projectionX),
    dot(worldPosition, projectionY)
    );
}


/* ------------------------------------------------------------------------- */
/* Color grading helpers                                                     */
/* ------------------------------------------------------------------------- */

vec3 applyMirrorColorGrading(
    vec3 color,
    float luminance,
    float distanceToCamera
) {
    /*
     * Distance-based desaturation.
     */
    float distanceFade = smoothstep(
        16.0,
        56.0,
        distanceToCamera
    );

    vec3 grayscale = vec3(
    dot(
        color,
        vec3(0.2126, 0.7152, 0.0722)
    )
    );

    color = mix(
        color,
        grayscale,
        distanceFade * 0.18
    );

    /*
     * Cooler shadows.
     */
    float shadowFactor =
    1.0 - smoothstep(
        0.08,
        0.55,
        luminance
    );

    vec3 shadowTint = vec3(
    0.82,
    0.92,
    1.06
    );

    color *= mix(
        vec3(1.0),
        shadowTint,
        shadowFactor * 0.14
    );

    /*
     * Slight silvery highlight tint.
     */
    float highlightFactor =
    smoothstep(
        0.55,
        1.0,
        luminance
    );

    vec3 highlightTint = vec3(
    1.02,
    1.04,
    1.06
    );

    color *= mix(
        vec3(1.0),
        highlightTint,
        highlightFactor * 0.10
    );

/*
     * Gentle overall cool tint with distance.
     */
    vec3 distanceTint = vec3(
    0.92,
    0.98,
    1.05
    );

    color *= mix(
        vec3(1.0),
        distanceTint,
        distanceFade * 0.10
    );

    return color;
}


vec3 applyMirrorFog(
    vec3 color,
    float distanceToCamera
) {

    float fogAmount = smoothstep(
        0.0,
        20.0,
        distanceToCamera
    );

    fogAmount *= 0.5;

    vec3 fogColor = vec3(
    0.08,
    0.13,
    0.18
    );

    return mix(
        color,
        fogColor,
        fogAmount
    );
}

/* ------------------------------------------------------------------------- */
/* Edge shimmer                                                              */
/* ------------------------------------------------------------------------- */

float getDepthEdge(
    vec2 uv,
    float centerDepth
) {
    vec2 pixelSize = 1.0 / textureSize(
        DepthSampler,
        0
    );

    float depthLeft = texture(
        DepthSampler,
        uv - vec2(pixelSize.x, 0.0)
    ).r;

    float depthRight = texture(
        DepthSampler,
        uv + vec2(pixelSize.x, 0.0)
    ).r;

    float depthDown = texture(
        DepthSampler,
        uv - vec2(0.0, pixelSize.y)
    ).r;

    float depthUp = texture(
        DepthSampler,
        uv + vec2(0.0, pixelSize.y)
    ).r;

/*
     * Perspective depth is nonlinear, so multiply differences by a large
     * value before thresholding.
     */
    float horizontalDifference =
    abs(centerDepth - depthLeft)
    + abs(centerDepth - depthRight);

    float verticalDifference =
    abs(centerDepth - depthDown)
    + abs(centerDepth - depthUp);

    float edgeDifference =
    max(
        horizontalDifference,
        verticalDifference
    );

    return smoothstep(
        0.000015,
        0.00045,
        edgeDifference
    );
}


vec2 getDepthEdgeDirection(
    vec2 uv
) {
    vec2 pixelSize = 1.0 / textureSize(
        DepthSampler,
        0
    );

    float leftDepth = texture(
        DepthSampler,
        uv - vec2(pixelSize.x, 0.0)
    ).r;

    float rightDepth = texture(
        DepthSampler,
        uv + vec2(pixelSize.x, 0.0)
    ).r;

    float downDepth = texture(
        DepthSampler,
        uv - vec2(0.0, pixelSize.y)
    ).r;

    float upDepth = texture(
        DepthSampler,
        uv + vec2(0.0, pixelSize.y)
    ).r;

    vec2 gradient = vec2(
    rightDepth - leftDepth,
    upDepth - downDepth
    );

    float gradientLength = length(gradient);

    if (gradientLength < 0.0000001) {
        return vec2(0.0);
    }

    return gradient / gradientLength;
}

float animatedOrbNoise(
    vec2 position,
    float time
) {
    vec2 baseCell = floor(position);
    vec2 localPosition = fract(position);

    float field = 0.0;

    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 neighbor = vec2(
            float(x),
            float(y)
            );

            vec2 cellId =
            baseCell + neighbor;

            vec2 randomPoint =
            hash22(cellId);

            float phase =
            hash21(
                cellId + vec2(4.17, 9.23)
            ) * TAU;

        /*
             * Each orb moves smoothly within its cell.
             */
            vec2 movement = vec2(
            sin(time * 0.34 + phase),
            cos(time * 0.27 + phase * 1.37)
            ) * 0.24;

            vec2 orbCenter =
            neighbor
            + randomPoint
            + movement;

            float distanceToOrb =
            length(
                orbCenter - localPosition
            );

        /*
             * Rounded influence from this orb.
             */
            float orb =
            1.0
            - smoothstep(
                0.12,
                0.62,
                distanceToOrb
            );

        /*
             * Slightly vary the strength of each orb.
             */
            float orbStrength = mix(
                0.65,
                1.0,
                hash21(cellId + vec2(11.7, 2.4))
            );

            field += orb * orbStrength;
        }
    }

    return clamp(field, 0.0, 1.0);
}

vec2 animatedOrbNoiseGradient(
    vec2 position,
    float time
) {
    const float sampleDistance = 0.035;

    float left = animatedOrbNoise(
        position - vec2(sampleDistance, 0.0),
        time
    );

    float right = animatedOrbNoise(
        position + vec2(sampleDistance, 0.0),
        time
    );

    float down = animatedOrbNoise(
        position - vec2(0.0, sampleDistance),
        time
    );

    float up = animatedOrbNoise(
        position + vec2(0.0, sampleDistance),
        time
    );

    return vec2(
    right - left,
    up - down
    );
}


vec3 applyEdgeShimmer(
    vec3 originalColor,
    vec2 uv,
    float depth,
    float time
) {
    float edge = getDepthEdge(
        uv,
        depth
    );

    if (edge <= 0.001) {
        return originalColor;
    }

    vec2 textureSizePixels = vec2(
    textureSize(DiffuseSampler, 0)
    );

    vec2 pixelSize =
    1.0 / textureSizePixels;

/*
     * Correct for screen aspect ratio so the noise cells remain rounded
     * rather than becoming stretched ovals.
     */
    float aspect =
    textureSizePixels.x
    / max(textureSizePixels.y, 1.0);

    vec2 noisePosition = uv;
    noisePosition.x *= aspect;

/*
     * Number of orb cells across the screen.
     *
     * Lower values produce larger distortion bubbles.
     */
    noisePosition *= 7.0;

/*
     * Use two differently scaled fields so the result does not look like
     * a simple repeating Voronoi grid.
     */
    float orbFieldA = animatedOrbNoise(
        noisePosition,
        time
    );

    float orbFieldB = animatedOrbNoise(
        noisePosition * 1.63
        + vec2(17.3, 8.7),
        time * 0.71
    );

    float orbField = clamp(
        orbFieldA * 0.68
        + orbFieldB * 0.32,
        0.0,
        1.0
    );

    vec2 gradientA =
    animatedOrbNoiseGradient(
        noisePosition,
        time
    );

    vec2 gradientB =
    animatedOrbNoiseGradient(
        noisePosition * 1.63
        + vec2(17.3, 8.7),
        time * 0.71
    );

    vec2 orbDirection =
    gradientA
    + gradientB * 0.45;

/*
     * Undo the aspect scaling for the direction vector.
     */
    orbDirection.x /= aspect;

    float directionLength =
    length(orbDirection);

    if (directionLength > 0.00001) {
        orbDirection /= directionLength;
    }

/*
     * Also obtain the silhouette tangent. Mixing it with the orb gradient
     * makes the bubbles flow around silhouettes rather than cutting
     * directly through them.
     */
    vec2 edgeDirection =
    getDepthEdgeDirection(uv);

    vec2 edgeTangent = vec2(
    -edgeDirection.y,
    edgeDirection.x
    );

    vec2 distortionDirection =
    orbDirection * 0.70
    + edgeTangent * 0.30;

    float distortionDirectionLength =
    length(distortionDirection);

    if (distortionDirectionLength > 0.00001) {
        distortionDirection /=
        distortionDirectionLength;
    }

/*
     * Center the field around zero so some orbs push one way while others
     * pull the other way.
     */
    float signedOrbField =
    orbField * 2.0 - 1.0;

    float distortionPixels = 1.75;

    vec2 distortionOffset =
    distortionDirection
    * pixelSize
    * distortionPixels
    * signedOrbField
    * edge;

    vec2 distortedUv = clamp(
        uv + distortionOffset,
        pixelSize,
        vec2(1.0) - pixelSize
    );

    vec3 distortedColor = texture(
        DiffuseSampler,
        distortedUv
    ).rgb;

/*
     * Rounded highlights appear near the brighter portions of the orb
     * field rather than as repeating stripes.
     */
    float orbHighlight = smoothstep(
        0.48,
        0.90,
        orbField
    );

    float glint =
    pow(edge, 1.5)
    * orbHighlight;

    vec3 shimmerColor = vec3(
    0.42,
    0.82,
    1.00
    );

    vec3 result = mix(
        originalColor,
        distortedColor,
        edge * 0.42
    );

    result +=
    shimmerColor
    * glint
    * 0.075;

    return result;
}


/* ------------------------------------------------------------------------- */
/* Broad moving light patches                                                */
/* ------------------------------------------------------------------------- */

float broadLightPatchField(
    vec3 worldPosition,
    float time
) {
/*
     * These frequencies produce patches several blocks across rather than
     * tens or hundreds of blocks across.
     */
    float waveA = sin(
        worldPosition.x * 0.34
        + worldPosition.z * 0.22
        + time * 0.075
    );

    float waveB = sin(
        worldPosition.z * 0.29
        - worldPosition.x * 0.17
        - time * 0.055
    );

    float waveC = sin(
        worldPosition.y * 0.24
        + worldPosition.x * 0.13
        + time * 0.042
    );

/*
     * Products create rounded islands of illumination instead of three
     * plainly visible bands.
     */
    float field =
    waveA * 0.25
    + waveB * 0.20
    + waveC * 0.15
    + waveA * waveB * 0.25
    + waveB * waveC * 0.15;

    field = field * 0.5 + 0.5;

    return smoothstep(
        0.35,
        0.72,
        field
    );
}


float broadLightSurfaceResponse(
    vec3 surfaceNormal,
    vec3 projectionDirection
) {
/*
     * Favor surfaces that generally face the incoming light,
     * but still allow walls and ceilings to receive some effect.
     */
    float facingProjection = max(
        dot(surfaceNormal, -projectionDirection),
        0.0
    );

    return mix(
        0.22,
        1.0,
        pow(facingProjection, 0.65)
    );
}


/* ------------------------------------------------------------------------- */
/* Main                                                                      */
/* ------------------------------------------------------------------------- */

void main() {
    vec4 baseColor =
    texture(DiffuseSampler, texCoord);

    float depth =
    texture(DepthSampler, texCoord).r;

    if (depth >= 0.999999) {
        fragColor = vec4(
        baseColor.rgb,
        1.0
        );
        return;
    }

    vec3 worldPosition =
    reconstructWorldPosition(
        texCoord,
        depth
    );

    vec3 surfaceNormal =
    reconstructSurfaceNormal(
        worldPosition
    );

    vec3 projectionDirection = normalize(
        vec3(
        0.35,
        -1.0,
        0.45
        )
    );

    vec2 projectedPosition =
    calculateProjectedCoordinates(
        worldPosition,
        projectionDirection
    );

    vec2 pixelAlignedPosition =
    snapToWorldPixelGrid(
        projectedPosition,
        16.0
    );

    float caustics =
    calculateRoundedCaustics(
        pixelAlignedPosition,
        CausticTime
    );

    float facingProjection = max(
        dot(
            surfaceNormal,
            -projectionDirection
        ),
        0.0
    );

    float surfaceResponse = mix(
        0.12,
        1.0,
        pow(facingProjection, 0.80)
    );

    float luminance = dot(
        baseColor.rgb,
        vec3(
        0.2126,
        0.7152,
        0.0722
        )
    );

    float lightResponse = mix(
        0.10,
        1.0,
        smoothstep(
            0.02,
            0.45,
            luminance
        )
    );

    float strength =
    caustics
    * surfaceResponse
    * lightResponse
    * 0.38;

    vec3 causticColor = vec3(
    0.30,
    0.76,
    0.94
    );

    vec3 result =
    baseColor.rgb
    + causticColor * strength;


/* ------------------------------------------------------------------------- */
/* Ceiling caustics                                                          */
/* ------------------------------------------------------------------------- */

    vec3 ceilingProjectionDirection = normalize(
        vec3(
        -0.28,
        1.0,
        -0.18
        )
    );

    vec2 ceilingProjectedPosition =
    calculateProjectedCoordinates(
        worldPosition,
        ceilingProjectionDirection
    );

    vec2 ceilingPixelAlignedPosition =
    snapToWorldPixelGrid(
        ceilingProjectedPosition,
        16.0
    );

    float ceilingCaustics =
    calculateRoundedCaustics(
        ceilingPixelAlignedPosition
        + vec2(3.7, 8.2),
        CausticTime * 0.82
    );

    float ceilingResponse = max(
        dot(
            surfaceNormal,
            vec3(0.0, -1.0, 0.0)
        ),
        0.0
    );

    ceilingResponse = pow(
        ceilingResponse,
        1.6
    );

    float ceilingStrength =
    ceilingCaustics
    * ceilingResponse
    * lightResponse
    * 0.26;

    vec3 ceilingCausticColor = vec3(
    0.38,
    0.68,
    0.90
    );

    result +=
    ceilingCausticColor
    * ceilingStrength;

    float broadPatch =
    broadLightPatchField(
        worldPosition,
        CausticTime
    );

    float broadPatchResponse =
    broadLightSurfaceResponse(
        surfaceNormal,
        projectionDirection
    );

    float broadPatchMask =
    broadPatch
    * broadPatchResponse;

/*
 * Brighten the underlying scene so the effect reads as illumination,
 * rather than only adding a faint colored overlay.
 */
    result *= 1.0 + broadPatchMask * 0.16;

/*
 * Add a restrained cyan-silver tint to the illuminated regions.
 */
    vec3 broadPatchColor = vec3(
    0.22,
    0.48,
    0.62
    );

    result +=
    broadPatchColor
    * broadPatchMask
    * 0.10;

    float distanceToCamera =
    length(worldPosition - CameraPos);

    result = applyMirrorColorGrading(
        result,
        luminance,
        distanceToCamera
    );

    result = applyMirrorFog(
        result,
        distanceToCamera
    );

    result = applyEdgeShimmer(
        result,
        texCoord,
        depth,
        CausticTime
    );

    fragColor = vec4(
    result,
    1.0
    );
}