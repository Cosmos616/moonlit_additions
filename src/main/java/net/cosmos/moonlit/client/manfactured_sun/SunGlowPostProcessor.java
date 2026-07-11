package net.cosmos.moonlit.client.manfactured_sun;

import com.mojang.blaze3d.platform.GlStateManager;
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
import org.joml.Vector3f;

public final class SunGlowPostProcessor {
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    private static PostChain sunGlowPostChain;

    private SunGlowPostProcessor() {}

    public static void load() {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            sunGlowPostChain = new PostChain(
                    minecraft.getTextureManager(),
                    minecraft.getResourceManager(),
                    minecraft.getMainRenderTarget(),
                    ResourceLocation.fromNamespaceAndPath(
                            Moonlit.MOD_ID,
                            "shaders/post/sun_glow.json"
                    )
            );

            sunGlowPostChain.resize(
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight()
            );

            lastWidth = minecraft.getWindow().getWidth();
            lastHeight = minecraft.getWindow().getHeight();

            Moonlit.LOGGER.info("Loaded manufactured sun glow post shader.");
        } catch (Exception exception) {
            Moonlit.LOGGER.error("Failed to load manufactured sun glow post shader", exception);
            sunGlowPostChain = null;
        }
    }

    public static void resize(int width, int height) {
        if (sunGlowPostChain != null) {
            sunGlowPostChain.resize(width, height);
            lastWidth = width;
            lastHeight = height;
        }
    }

    private static void resizeIfNeeded() {
        if (sunGlowPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (width != lastWidth || height != lastHeight) {
            sunGlowPostChain.resize(width, height);

            lastWidth = width;
            lastHeight = height;

            Moonlit.LOGGER.info("Resized manufactured sun glow post shader to {}x{}", width, height);
        }
    }

    private static void updateUniforms() {
        if (sunGlowPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        Matrix4f invProjMat = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        Vector3f centerView = new Vector3f(
                (float) (ManufacturedSunClientData.center.x - cameraPos.x),
                (float) (ManufacturedSunClientData.center.y - cameraPos.y),
                (float) (ManufacturedSunClientData.center.z - cameraPos.z)
        );

        Quaternionf inverseCameraRotation = new Quaternionf(camera.rotation()).conjugate();
        centerView.rotate(inverseCameraRotation);

        for (PostPass pass : sunGlowPostChain.passes) {
            if (pass.getEffect() == null) {
                continue;
            }

            Uniform invProjUniform = pass.getEffect().getUniform("InvProjMat");
            if (invProjUniform != null) {
                invProjUniform.set(invProjMat);
            }

            Uniform centerUniform = pass.getEffect().getUniform("SunCenterView");
            if (centerUniform != null) {
                centerUniform.set(centerView.x, centerView.y, centerView.z);
            }

            Uniform radiusUniform = pass.getEffect().getUniform("SunRadius");
            if (radiusUniform != null) {
                radiusUniform.set(ManufacturedSunClientData.radius);
            }

            Uniform colorUniform = pass.getEffect().getUniform("SunColor");
            if (colorUniform != null) {
                Vector3f color = ManufacturedSunClientData.color;
                colorUniform.set(color.x, color.y, color.z);
            }

            Uniform intensityUniform = pass.getEffect().getUniform("SunIntensity");
            if (intensityUniform != null) {
                intensityUniform.set(ManufacturedSunClientData.intensity);
            }
        }
    }

    public static void render(RenderLevelStageEvent event) {
        if (sunGlowPostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        ManufacturedSunClientData.clearIfStale(minecraft.level.getGameTime());

        if (!ManufacturedSunClientData.active) {
            return;
        }

        updateUniforms();
        resizeIfNeeded();

        minecraft.getMainRenderTarget().bindWrite(false);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(
//                GlStateManager.SourceFactor.SRC_ALPHA,
//                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
//        );



        minecraft.getMainRenderTarget().bindWrite(false);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        sunGlowPostChain.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        minecraft.getMainRenderTarget().bindWrite(false);
    }
}