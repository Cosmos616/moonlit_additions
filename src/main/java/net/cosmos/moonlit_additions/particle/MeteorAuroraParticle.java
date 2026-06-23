package net.cosmos.moonlit_additions.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MeteorAuroraParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    private final float startHue;
    private final float hueShiftSpeed;
    private final float maxAlpha;

    private static final ParticleRenderType PARTICLE_SHEET_ADDITIVE = new ParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();

            RenderSystem.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE
            );

            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "moonlit_additions:particle_sheet_additive";
        }
    };


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();

        float x = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float y = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float z = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        // Direction from particle to camera, but flattened onto the X/Z plane.
        float toCameraX = (float) (cameraPos.x() - Mth.lerp(partialTick, this.xo, this.x));
        float toCameraZ = (float) (cameraPos.z() - Mth.lerp(partialTick, this.zo, this.z));

        float length = Mth.sqrt(toCameraX * toCameraX + toCameraZ * toCameraZ);

        if (length < 0.0001F) {
            return;
        }

        toCameraX /= length;
        toCameraZ /= length;

        // Horizontal right vector, perpendicular to the flattened camera direction.
        // This keeps the particle upright.
        float rightX = toCameraZ;
        float rightY = 0.0F;
        float rightZ = -toCameraX;

        // World up. No camera pitch tilt.
        float upX = 0.0F;
        float upY = 1.0F;
        float upZ = 0.0F;

        float size = this.getQuadSize(partialTick);

        float halfWidth = size;
        float halfHeight = size;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();

        int light = this.getLightColor(partialTick);

        vertexConsumer.addVertex(
                x - rightX * halfWidth - upX * halfHeight,
                y - rightY * halfWidth - upY * halfHeight,
                z - rightZ * halfWidth - upZ * halfHeight
        ).setUv(u1, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

// Bottom-right
        vertexConsumer.addVertex(
                x + rightX * halfWidth - upX * halfHeight,
                y + rightY * halfWidth - upY * halfHeight,
                z + rightZ * halfWidth - upZ * halfHeight
        ).setUv(u0, v0).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

// Top-right
        vertexConsumer.addVertex(
                x + rightX * halfWidth + upX * halfHeight,
                y + rightY * halfWidth + upY * halfHeight,
                z + rightZ * halfWidth + upZ * halfHeight
        ).setUv(u0, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);

// Top-left
        vertexConsumer.addVertex(
                x - rightX * halfWidth + upX * halfHeight,
                y - rightY * halfWidth + upY * halfHeight,
                z - rightZ * halfWidth + upZ * halfHeight
        ).setUv(u1, v1).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
    }

    protected MeteorAuroraParticle(
            ClientLevel level,
            double x,
            double y,
            double z,
            double xSpeed,
            double ySpeed,
            double zSpeed,
            SpriteSet sprites
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.sprites = sprites;

        this.lifetime = 120 + this.random.nextInt(50);

        this.gravity = 0.0F;
        this.friction = 0.96F;
        this.hasPhysics = false;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.quadSize = 4F + this.random.nextFloat() * 3F;

        // Hue range:
        // blue/cyan/green/purple-ish.
        this.startHue = switch (this.random.nextInt(4)) {
            case 0 -> 0.50F; // cyan
            case 1 -> 0.58F; // blue
            case 2 -> 0.78F; // purple
            default -> 0.38F; // green
        };

        this.hueShiftSpeed = 0.015F + this.random.nextFloat() * 0.02F;
        this.maxAlpha = 0.25F + this.random.nextFloat() * 0.35F;

        this.alpha = 0.0F;

        this.pickSprite(sprites);
        this.updateColor();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);

        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;


        float lifeProgress = (float) this.age / (float) this.lifetime;

        // Fade in, then fade out.
        float fadeIn = Mth.clamp(lifeProgress / 0.25F, 0.0F, 1.0F);
        float fadeOut = Mth.clamp((1.0F - lifeProgress) / 0.45F, 0.0F, 1.0F);

        this.alpha = this.maxAlpha * fadeIn * fadeOut;

        // Slightly grow over time.
        this.quadSize *= 1.006F;

        this.updateColor();
        this.setSpriteFromAge(this.sprites);
    }

    private void updateColor() {
        float hue = this.startHue + this.age * this.hueShiftSpeed;
        hue = hue - Mth.floor(hue);

        // Limit the random cycling back into aurora-like colors.
        // This remaps full hue cycling into green/blue/purple-ish colors.
        float shiftedHue;
        float wave = (Mth.sin(hue * Mth.TWO_PI) + 1.0F) * 0.5F;

        if (wave < 0.33F) {
            shiftedHue = 0.38F; // green
        } else if (wave < 0.66F) {
            shiftedHue = 0.55F; // cyan/blue
        } else {
            shiftedHue = 0.78F; // purple
        }

        int rgb = java.awt.Color.HSBtoRGB(shiftedHue, 0.75F, 1.0F);

        float r = ((rgb >> 16) & 255) / 255.0F;
        float g = ((rgb >> 8) & 255) / 255.0F;

        this.setColor(wave, 1.0f-wave, 1.0f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        // Start with this. Easier and safer.
        return PARTICLE_SHEET_ADDITIVE;
    }

    public static class Provider implements net.minecraft.client.particle.ParticleProvider<net.minecraft.core.particles.SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public net.minecraft.client.particle.Particle createParticle(
                net.minecraft.core.particles.SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            return new MeteorAuroraParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }


}