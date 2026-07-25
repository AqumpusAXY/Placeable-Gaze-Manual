package github.aqumpusaxy.placeable_gaze_manual;

import com.strawberry.gaze.GazeMod;
import github.aqumpusaxy.placeable_gaze_manual.common.Constants;
import github.aqumpusaxy.placeable_gaze_manual.common.Registry;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(Constants.MOD_ID)
public class PlaceableGazeManual {
    public PlaceableGazeManual(IEventBus modEventBus, ModContainer modContainer) {
        Registry.register(modEventBus);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::packSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(GazeMod.ENCYCLOPEDIA_UNVEILED.get(), SpellBookCurioRenderer::new);
    }

    private void packSetup(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "builtin_packs/new_gaze_manual"),
                PackType.CLIENT_RESOURCES,
                Component.translatable("pack.placeable_gaze_manual.new_gaze_manual"),
                PackSource.BUILT_IN,
                false,
                Pack.Position.BOTTOM
        );
    }
}
