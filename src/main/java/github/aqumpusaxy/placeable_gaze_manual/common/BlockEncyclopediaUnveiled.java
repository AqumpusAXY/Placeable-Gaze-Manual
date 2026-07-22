package github.aqumpusaxy.placeable_gaze_manual.common;

import com.strawberry.gaze.GazeMod;
import com.strawberry.gaze.client.screens.codex.screens.GazeProgressionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockEncyclopediaUnveiled extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Properties BLOCK_PROPERTIES = Properties.ofFullCopy(Blocks.BLUE_WOOL);
    private static final VoxelShape NORTH_SHAPE = Block.box(4, 0, 3, 11, 4, 13);
    private static final VoxelShape EAST_SHAPE = Block.box(3, 0, 4, 13, 4, 11);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 0, 3, 12, 4, 13);
    private static final VoxelShape WEST_SHAPE = Block.box(3, 0, 5, 13, 4, 12);

    public BlockEncyclopediaUnveiled() {
        super(BLOCK_PROPERTIES);
        this.registerDefaultState(
                this.defaultBlockState().setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        player.swing(player.getUsedItemHand());
        if (!player.isSecondaryUseActive()) {
           return this.openBookGUI(level);
        } else {
            return this.pickUpBook(level, player, pos, state);
        }
    }

    private InteractionResult openBookGUI(Level level) {
        if (level.isClientSide) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getConnection() != null) {
                mc.getConnection().send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS));
            }

            GazeProgressionScreen.getScreen().openCodexViaTransition();
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.CONSUME;
        }
    }

    private InteractionResult pickUpBook(Level level, Player player, BlockPos pos, BlockState state) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (level.getBlockEntity(pos) instanceof BlockEntityEncyclopediaUnveiled book) itemStack = book.getItemStack();

        player.setItemInHand(InteractionHand.MAIN_HAND, itemStack);

        level.playSound(
                player,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        if (state.getValue(WATERLOGGED)) {
            level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
        } else {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> throw new IllegalStateException("Invalid direction");
        };
    }

    @Override
    protected BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        if (p_60541_.getValue(WATERLOGGED)) {
            p_60544_.scheduleTick(p_60545_, Fluids.WATER, Fluids.WATER.getTickDelay(p_60544_));
        }
        return super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof BlockEntityEncyclopediaUnveiled book) {
            return List.of(book.getItemStack());
        } else {
            return List.of(new ItemStack(GazeMod.ENCYCLOPEDIA_UNVEILED.get()));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return switch (context.getHorizontalDirection()) {
            case NORTH -> defaultBlockState().setValue(FACING, Direction.NORTH);
            case EAST -> defaultBlockState().setValue(FACING, Direction.EAST);
            case SOUTH -> defaultBlockState().setValue(FACING, Direction.SOUTH);
            case WEST -> defaultBlockState().setValue(FACING, Direction.WEST);
            default -> null;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public Item asItem() {
        return GazeMod.ENCYCLOPEDIA_UNVEILED.get();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlockEntityEncyclopediaUnveiled(pos, state);
    }
}
