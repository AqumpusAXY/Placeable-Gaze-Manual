package github.aqumpusaxy.placeable_gaze_manual;

import com.strawberry.gaze.GazeMod;
import github.aqumpusaxy.placeable_gaze_manual.common.Constants;
import github.aqumpusaxy.placeable_gaze_manual.common.Registry;
import io.redspace.ironsspellbooks.render.SpellBookCurioRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@Mod(Constants.MOD_ID)
public class PlaceableGazeManual {
    public PlaceableGazeManual(IEventBus modEventBus, ModContainer modContainer) {
        Registry.register(modEventBus);
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        CuriosRendererRegistry.register(GazeMod.ENCYCLOPEDIA_UNVEILED.get(), SpellBookCurioRenderer::new);
    }
}
