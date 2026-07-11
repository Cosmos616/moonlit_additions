package net.cosmos.moonlit.client.rendering;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.cosmos.moonlit.client.ClientHelper;
import net.cosmos.moonlit.client.manfactured_sun.ManufacturedSunClientData;
import net.cosmos.moonlit.client.manfactured_sun.SunGlowPostProcessor;
import net.cosmos.moonlit.client.shaders.fx.SunGlowFx;
import net.cosmos.moonlit.client.shaders.processor.SunShaderProcessor;
import net.cosmos.moonlit.common.block_entity.forge.ManufacturedSunBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.FastColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class ManufacturedSunRenderer implements BlockEntityRenderer<ManufacturedSunBlockEntity> {

    private record SunLayerSettings(
            int color,
            int layers,
            float innerRadius,
            float outerRadius,
            float pulseAmount,
            float rotationSpeed
    ) {
    }

    static SunGlowFx sunGlowFx;

    public ManufacturedSunRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(
            ManufacturedSunBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {

        float gameTime = 0.0F;

        if (blockEntity.getLevel() != null) {
            gameTime = blockEntity.getLevel().getGameTime() + partialTick;
        }

        SunLayerSettings settings = new SunLayerSettings(
                0x80FFE8A8, // warm pale-gold ARGB
                10,          // number of cube layers
                1F,      // smallest cube radius
                2F,      // largest cube radius
                0.035F,     // pulse amount
                0.45F       // rotation speed
        );

        Vec3 center = Vec3.atLowerCornerOf(blockEntity.getBlockPos()).add(0.5D, 0.5D, 0.5D);

        //ManufacturedSunClientData.submit(
        //        center,
        //        new Vector3f(1.0F, 0.75F, 0.28F),
        //        8.0F,
        //        1.25F,
        //        blockEntity.getLevel().getGameTime()
        //);

        if(sunGlowFx == null) {
            sunGlowFx = new SunGlowFx(center.toVector3f(), new Vector3f(1.0F, 0.75F, 0.28F), 8.0F, 1.25F);
            SunShaderProcessor.INSTANCE.addFxInstance(sunGlowFx);
        }

        poseStack.pushPose();

        // Center render inside the block.
        poseStack.translate(0.5D, 0.5D, 0.5D);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(ClientHelper.LIGHT_BEAM);
        Matrix4f pose = poseStack.last().pose();

        renderSunCubeLayers(
                vertexConsumer,
                poseStack,
                pose,
                gameTime,
                settings
        );

        poseStack.popPose();
    }

    private static void renderSunCubeLayers(
            VertexConsumer vertexConsumer,
            PoseStack poseStack,
            Matrix4f pose,
            float gameTime,
            SunLayerSettings settings
    ) {
        SunColor baseColor = SunColor.fromArgb(settings.color());

        int layers = Math.max(settings.layers(), 1);

        for (int layer = 0; layer < layers; layer++) {
            float t = layers == 1 ? 0.0F : layer / (float) (layers - 1);

            float radius = lerp(settings.innerRadius(), settings.outerRadius(), t);

            // Larger outer layers are softer.
            float alphaMultiplier = lerp(0.2F, 0.05F, t);

            // Slight pulse offset per layer so they do not breathe exactly together.
            float pulse = (float) Math.sin(gameTime * 0.08F + layer * 0.85F) * settings.pulseAmount();
            float finalRadius = radius + pulse;

            SunColor color = baseColor.withAlpha(baseColor.alpha() * alphaMultiplier);

            poseStack.pushPose();

            float rotation = gameTime * settings.rotationSpeed() * (layer % 2 == 0 ? 1.0F : -1.0F);

            poseStack.mulPose(Axis.YP.rotationDegrees(rotation + layer * 17.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation * 0.45F + layer * 11.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation * 0.25F + layer * 7.0F));

            Matrix4f layerPose = poseStack.last().pose();

            renderCube(
                    vertexConsumer,
                    layerPose,
                    finalRadius,
                    color
            );

            poseStack.popPose();
        }
    }

    private static void renderCube(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float radius,
            SunColor color
    ) {
        float min = -radius;
        float max = radius;

        // Front face, z+
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                min, min, max,
                max, min, max,
                max, max, max,
                min, max, max,
                color
        );

        // Back face, z-
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                max, min, min,
                min, min, min,
                min, max, min,
                max, max, min,
                color
        );

        // Left face, x-
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                min, min, min,
                min, min, max,
                min, max, max,
                min, max, min,
                color
        );

        // Right face, x+
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                max, min, max,
                max, min, min,
                max, max, min,
                max, max, max,
                color
        );

        // Top face, y+
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                min, max, max,
                max, max, max,
                max, max, min,
                min, max, min,
                color
        );

        // Bottom face, y-
        addDoubleSidedQuad(
                vertexConsumer,
                pose,
                min, min, min,
                max, min, min,
                max, min, max,
                min, min, max,
                color
        );
    }

    private static void addDoubleSidedQuad(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            SunColor color
    ) {
        addQuad(
                vertexConsumer,
                pose,
                x1, y1, z1,
                x2, y2, z2,
                x3, y3, z3,
                x4, y4, z4,
                color
        );

        addQuad(
                vertexConsumer,
                pose,
                x4, y4, z4,
                x3, y3, z3,
                x2, y2, z2,
                x1, y1, z1,
                color
        );
    }

    private static void addQuad(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            SunColor color
    ) {
        addVertex(vertexConsumer, pose, x1, y1, z1, color);
        addVertex(vertexConsumer, pose, x2, y2, z2, color);
        addVertex(vertexConsumer, pose, x3, y3, z3, color);
        addVertex(vertexConsumer, pose, x4, y4, z4, color);
    }

    private static void addVertex(
            VertexConsumer vertexConsumer,
            Matrix4f pose,
            float x,
            float y,
            float z,
            SunColor color
    ) {
        vertexConsumer.addVertex(pose, x, y, z)
                .setColor(color.red(), color.green(), color.blue(), color.alpha());
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private record SunColor(
            float red,
            float green,
            float blue,
            float alpha
    ) {
        private static SunColor fromArgb(int color) {
            return new SunColor(
                    FastColor.ARGB32.red(color) / 255.0F,
                    FastColor.ARGB32.green(color) / 255.0F,
                    FastColor.ARGB32.blue(color) / 255.0F,
                    FastColor.ARGB32.alpha(color) / 255.0F
            );
        }

        private SunColor withAlpha(float alpha) {
            return new SunColor(
                    red,
                    green,
                    blue,
                    Math.max(0.0F, Math.min(1.0F, alpha))
            );
        }
    }

    @Override
    public boolean shouldRenderOffScreen(ManufacturedSunBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    @Override
    public AABB getRenderBoundingBox(ManufacturedSunBlockEntity blockEntity) {
        return AABB.INFINITE;
    }
}
