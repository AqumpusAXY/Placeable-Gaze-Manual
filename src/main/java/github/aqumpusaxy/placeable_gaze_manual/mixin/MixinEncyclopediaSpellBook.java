package github.aqumpusaxy.placeable_gaze_manual.mixin;

import com.strawberry.gaze.compat.irons_spellbooks.EncyclopediaSpellBook;
import github.aqumpusaxy.placeable_gaze_manual.common.BlockEncyclopediaUnveiled;
import github.aqumpusaxy.placeable_gaze_manual.common.BlockEntityEncyclopediaUnveiled;
import github.aqumpusaxy.placeable_gaze_manual.common.Registry;
import io.redspace.ironsspellbooks.api.registry.SpellDataRegistryHolder;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;

@Mixin(value = EncyclopediaSpellBook.class, remap = false)
public abstract class MixinEncyclopediaSpellBook extends UniqueSpellBook implements ParticleEmitterHandler.ItemParticleSupplier {
    private MixinEncyclopediaSpellBook(SpellDataRegistryHolder[] spellDataRegistryHolders, Properties properties) {
        super(spellDataRegistryHolders, properties);
    }

    @Unique
    @Override
    public @NotNull InteractionResult useOn(UseOnContext useOnContext) {
        if (!useOnContext.isSecondaryUseActive()) return InteractionResult.PASS;

        var ctx = new BlockPlaceContext(useOnContext);
        if (!ctx.canPlace()) return InteractionResult.FAIL;

        var level = ctx.getLevel();
        var blockPos = ctx.getClickedPos();
        var player = ctx.getPlayer();
        var itemStack = ctx.getItemInHand();

        var blockState = Registry.BLOCK_ENCYCLOPEDIA_UNVEILED.get().getStateForPlacement(ctx);
        if (blockState == null) return InteractionResult.FAIL;
        if (level.getBlockState(blockPos).getBlock() == Blocks.WATER) {
            blockState = blockState.setValue(BlockEncyclopediaUnveiled.WATERLOGGED, true);
        }

        var collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        if (!level.isUnobstructed(blockState, blockPos, collisionContext)) return InteractionResult.FAIL;

        ctx.getLevel().setBlock(blockPos, blockState, 11);
        if (level.getBlockEntity(blockPos) instanceof BlockEntityEncyclopediaUnveiled blockEntity) {
            blockEntity.setItemStack(itemStack.copy());
            blockEntity.getPersistentData().put("book", itemStack.save(level.registryAccess()));
            blockEntity.setChanged();
        }

        var soundType = blockState.getSoundType(level, blockPos, player);
        level.playSound(
                player,
                blockPos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, blockState));

        itemStack.consume(1, player);

        return InteractionResult.sidedSuccess(ctx.getLevel().isClientSide);
    }
}
