package net.cosmos.moonlit_additions.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.cosmos.moonlit_additions.common.world.meteor.Meteor;
import net.cosmos.moonlit_additions.init.ModAttachmentTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

public class MeteorCommand {
    public MeteorCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("meteor").requires((stack) -> stack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes((context) -> set(context, BlockPosArgument.getLoadedBlockPos(context, "pos"), 0))
                        ).then(Commands.argument("age", IntegerArgumentType.integer(0))
                                .executes((context) -> set(context, BlockPosArgument.getLoadedBlockPos(context, "pos"), IntegerArgumentType.getInteger(context, "age")))
                        ))
                .then(Commands.literal("clear")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes((context) -> clear(context, BlockPosArgument.getLoadedBlockPos(context, "pos")))
                        )));
    }

    private static int set(CommandContext<CommandSourceStack> context, BlockPos pos, int age) {
        ServerLevel overworld = context.getSource().getServer().overworld();
        LevelChunk chunk = overworld.getChunkAt(pos);
        chunk.setData(ModAttachmentTypes.METEOR, new Meteor(age));
        BlockPos center = chunk.getPos().getMiddleBlockPosition(0);
        context.getSource().sendSuccess(() -> Component.literal("Created a meteor at " + center.getX() + ", " + center.getZ()), true);
        return 1;
    }

    private static int clear(CommandContext<CommandSourceStack> context, BlockPos pos) {
        ServerLevel overworld = context.getSource().getServer().overworld();
        LevelChunk chunk = overworld.getChunkAt(pos);
        BlockPos center = chunk.getPos().getMiddleBlockPosition(0);
        if (!chunk.hasData(ModAttachmentTypes.METEOR)) {
            context.getSource().sendSuccess(() -> Component.literal("No meteor exists at " + center.getX() + ", " + center.getZ()), true);
            return 1;
        }
        chunk.removeData(ModAttachmentTypes.METEOR);
        context.getSource().sendSuccess(() -> Component.literal("Removed a meteor at " + center.getX() + ", " + center.getZ()), true);
        return 1;
    }
}
