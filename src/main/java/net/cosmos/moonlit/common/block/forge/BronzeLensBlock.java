package net.cosmos.moonlit.common.block.forge;

import net.cosmos.moonlit.common.block_entity.forge.light.BronzeLensBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import team.lodestar.lodestone.modules.toolkit.block.WaterLoggedEntityBlock;

public class BronzeLensBlock extends WaterLoggedEntityBlock<BronzeLensBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ROTATE_X = BooleanProperty.create("rotate_x");
    public static final BooleanProperty ROTATE_Y = BooleanProperty.create("rotate_y");
    public static final BooleanProperty ROTATE_Z = BooleanProperty.create("rotate_z");

    public BronzeLensBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ROTATE_X, false)
                .setValue(ROTATE_Y, false)
                .setValue(ROTATE_Z, false)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATE_X, ROTATE_Y, ROTATE_Z);
        super.createBlockStateDefinition(builder);
    }
}
