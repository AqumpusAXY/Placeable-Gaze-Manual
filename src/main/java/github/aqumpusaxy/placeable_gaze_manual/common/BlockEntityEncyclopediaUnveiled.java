package github.aqumpusaxy.placeable_gaze_manual.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class BlockEntityEncyclopediaUnveiled extends BlockEntity {
    private ItemStack itemStack = ItemStack.EMPTY;

    public BlockEntityEncyclopediaUnveiled(BlockPos pos, BlockState blockState) {
        super(Registry.BLOCK_ENTITY_TYPE_ENCYCLOPEDIA_UNVEILED.get(), pos, blockState);
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.itemStack = ItemStack.parseOptional(registries, tag.getCompound("book"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("book", this.itemStack.saveOptional(registries));
    }
}
