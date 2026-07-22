package github.aqumpusaxy.placeable_gaze_manual.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Registry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    public static final Supplier<BlockEncyclopediaUnveiled> BLOCK_ENCYCLOPEDIA_UNVEILED =
            BLOCKS.register("encyclopedia_unveiled", BlockEncyclopediaUnveiled::new);

    @SuppressWarnings("DataFlowIssue")
    public static final Supplier<BlockEntityType<BlockEntityEncyclopediaUnveiled>> BLOCK_ENTITY_TYPE_ENCYCLOPEDIA_UNVEILED =
            BLOCK_ENTITY_TYPES.register(
                    "encyclopedia_unveiled",
                    () -> BlockEntityType.Builder.of(
                            BlockEntityEncyclopediaUnveiled::new,
                            BLOCK_ENCYCLOPEDIA_UNVEILED.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
