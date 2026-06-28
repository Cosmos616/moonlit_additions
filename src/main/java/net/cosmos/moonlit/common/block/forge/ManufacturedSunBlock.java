package net.cosmos.moonlit.common.block.forge;

import net.cosmos.moonlit.common.block_entity.forge.ManufacturedSunBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

import java.util.Locale;

public class ManufacturedSunBlock extends LodestoneEntityBlock<ManufacturedSunBlockEntity> {

    public static final EnumProperty<Stage> STAGE = EnumProperty.create("stage", Stage.class);

    public ManufacturedSunBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, Stage.DORMANT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(STAGE)) {
            case FAILED -> Block.box(4, 4, 4, 12, 12, 12);
            case DORMANT -> Block.box(3, 3, 3, 13, 13, 13);
            case BLOOMING, BLOOMED -> Block.box(0, 0, 0, 16, 16, 16);
            case OVERLOADED -> Block.box(-1, -1, -1, 17, 17, 17);
        };
    }

    public enum Stage implements StringRepresentable {
        FAILED,
        DORMANT,
        BLOOMING,
        BLOOMED,
        OVERLOADED;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
