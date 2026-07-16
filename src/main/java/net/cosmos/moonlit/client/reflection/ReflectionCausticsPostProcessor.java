package net.cosmos.moonlit.client.reflection;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.cosmos.moonlit.Moonlit;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public final class ReflectionCausticsPostProcessor {

    private static final ResourceLocation EFFECT_LOCATION =
            Moonlit.moonlitPath("shaders/post/reflection.json");

    private static PostChain causticsPostChain;

    private static int lastWidth = -1;
    private static int lastHeight = -1;

    private static boolean active;
    private static long startTimeNanos = System.nanoTime();

    private ReflectionCausticsPostProcessor() {
    }

    public static void load() {
        Minecraft minecraft = Minecraft.getInstance();

        close();

        try {
            causticsPostChain = new PostChain(
                    minecraft.getTextureManager(),
                    minecraft.getResourceManager(),
                    minecraft.getMainRenderTarget(),
                    EFFECT_LOCATION
            );

            resize(
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight()
            );

            startTimeNanos = System.nanoTime();

            Moonlit.LOGGER.info(
                    "Loaded reflection caustics post shader"
            );
        } catch (Exception exception) {
            Moonlit.LOGGER.error(
                    "Failed to load reflection caustics post shader",
                    exception
            );

            causticsPostChain = null;
        }
    }

    public static void setActive(boolean shouldBeActive) {
        active = shouldBeActive;

        if (active && causticsPostChain == null) {
            load();
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static void resize(int width, int height) {
        if (causticsPostChain == null) {
            return;
        }

        causticsPostChain.resize(width, height);

        lastWidth = width;
        lastHeight = height;
    }

    private static void resizeIfNeeded() {
        if (causticsPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (width == lastWidth && height == lastHeight) {
            return;
        }

        resize(width, height);
    }

    private static void updateUniforms() {
        if (causticsPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();

        float elapsedSeconds =
                (System.nanoTime() - startTimeNanos)
                        / 1_000_000_000.0F;

        Matrix4f inverseProjection =
                new Matrix4f(
                        RenderSystem.getProjectionMatrix()
                ).invert();

        Matrix4f cameraToWorldRotation =
                new Matrix4f().rotation(
                        new Quaternionf(camera.rotation())
                );

        Matrix4f inverseBob =
                ViewBobbingCapture.getInverseBob(
                        new Matrix4f()
                );

        Vec3 cameraPosition = camera.getPosition();

        for (PostPass pass : causticsPostChain.passes) {
            if (pass.getEffect() == null) {
                continue;
            }

            Uniform timeUniform =
                    pass.getEffect().getUniform("CausticTime");

            if (timeUniform != null) {
                timeUniform.set(elapsedSeconds);
            }

            Uniform inverseProjectionUniform =
                    pass.getEffect().getUniform("InvProjMat");

            if (inverseProjectionUniform != null) {
                inverseProjectionUniform.set(inverseProjection);
            }

            Uniform rotationUniform =
                    pass.getEffect().getUniform(
                            "CameraToWorldRotMat"
                    );

            if (rotationUniform != null) {
                rotationUniform.set(cameraToWorldRotation);
            }

            Uniform inverseBobUniform =
                    pass.getEffect().getUniform("InvBobMat");

            if (inverseBobUniform != null) {
                inverseBobUniform.set(inverseBob);
            }

            Uniform cameraPositionUniform =
                    pass.getEffect().getUniform("CameraPos");

            if (cameraPositionUniform != null) {
                cameraPositionUniform.set(
                        (float) cameraPosition.x,
                        (float) cameraPosition.y,
                        (float) cameraPosition.z
                );
            }
        }
    }

    public static void render(RenderLevelStageEvent event) {
        if (!active || causticsPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        resizeIfNeeded();
        updateUniforms();

        /*
         * The main target contains both the rendered scene color and the
         * depth attachment used by DepthSampler.
         */
        minecraft.getMainRenderTarget().bindWrite(false);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        causticsPostChain.process(
                event.getPartialTick()
                        .getGameTimeDeltaPartialTick(false)
        );

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        minecraft.getMainRenderTarget().bindWrite(false);
    }

    public static void close() {
        if (causticsPostChain != null) {
            causticsPostChain.close();
            causticsPostChain = null;
        }

        lastWidth = -1;
        lastHeight = -1;
    }
}