package net.cosmos.moonlit_additions.client.shockwave;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.cosmos.moonlit_additions.MoonlitAdditions;
import net.cosmos.moonlit_additions.client.shockwave.ShockwaveClientData;
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

public class ShockwavePostProcessor {

    private static int lastWidth = -1;
    private static int lastHeight = -1;
    private static PostChain shockwavePostChain;

    public static void load() {
        Minecraft minecraft = Minecraft.getInstance();

        try {
            shockwavePostChain = new PostChain(
                    minecraft.getTextureManager(),
                    minecraft.getResourceManager(),
                    minecraft.getMainRenderTarget(),
                    ResourceLocation.fromNamespaceAndPath(
                            MoonlitAdditions.MOD_ID,
                            "shaders/post/shockwave.json"
                    )
            );

            shockwavePostChain.resize(
                    minecraft.getWindow().getWidth(),
                    minecraft.getWindow().getHeight()
            );
        } catch (Exception exception) {
            MoonlitAdditions.LOGGER.error("Failed to load shockwave post shader", exception);
            shockwavePostChain = null;
        }
    }

    public static void resize(int width, int height) {
        if (shockwavePostChain != null) {
            shockwavePostChain.resize(width, height);
        }
    }

    private static void resizeIfNeeded() {
        if (shockwavePostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        int width = minecraft.getWindow().getWidth();
        int height = minecraft.getWindow().getHeight();

        if (width != lastWidth || height != lastHeight) {
            shockwavePostChain.resize(width, height);

            lastWidth = width;
            lastHeight = height;

            MoonlitAdditions.LOGGER.info("Resized shockwave post shader to {}x{}", width, height);
        }
    }

    private static void updateUniforms() {
        if (shockwavePostChain == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        Matrix4f invProjMat = new Matrix4f(RenderSystem.getProjectionMatrix()).invert();

        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();

        Vector3f centerView = new Vector3f(
                ShockwaveClientData.center.x - (float) cameraPos.x,
                ShockwaveClientData.center.y - (float) cameraPos.y,
                ShockwaveClientData.center.z - (float) cameraPos.z
        );

        // Convert world-relative position into camera/view-relative orientation.
        Quaternionf inverseCameraRotation = new Quaternionf(camera.rotation()).conjugate();
        centerView.rotate(inverseCameraRotation);

        for (PostPass pass : shockwavePostChain.passes) {
            if (pass.getEffect() == null) {
                continue;
            }

            Uniform invProjUniform = pass.getEffect().getUniform("InvProjMat");
            if (invProjUniform != null) {
                invProjUniform.set(invProjMat);
            }

            Uniform centerUniform = pass.getEffect().getUniform("ShockwaveCenterView");
            if (centerUniform != null) {
                centerUniform.set(centerView.x, centerView.y, centerView.z);
            }

            Uniform radiusUniform = pass.getEffect().getUniform("ShockwaveRadius");
            if (radiusUniform != null) {
                radiusUniform.set(ShockwaveClientData.radius);
            }

            Uniform alphaUniform = pass.getEffect().getUniform("ShockwaveAlpha");
            if (alphaUniform != null) {
                alphaUniform.set(ShockwaveClientData.alpha);
            }
        }
    }

    public static void render(RenderLevelStageEvent event) {
        if (shockwavePostChain == null) {
            return;
        }

        if (!ShockwaveClientData.active) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();


        updateUniforms();

        resizeIfNeeded();

        // Make sure the main target is active.
        minecraft.getMainRenderTarget().bindWrite(false);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        shockwavePostChain.process(event.getPartialTick().getGameTimeDeltaPartialTick(false));

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();

        minecraft.getMainRenderTarget().bindWrite(false);
    }
}