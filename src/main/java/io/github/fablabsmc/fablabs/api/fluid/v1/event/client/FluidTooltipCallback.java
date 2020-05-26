package io.github.fablabsmc.fablabs.api.fluid.v1.event.client;

import java.util.List;

import io.github.fablabsmc.fablabs.api.fluid.v1.volume.api.FluidVolume;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface FluidTooltipCallback {
	/**
	 * Fired before fluid properties have been appended to the list.
	 */
	Event<FluidTooltipCallback> EVENT = EventFactory.createArrayBacked(FluidTooltipCallback.class, (listeners) -> (volume, tooltipContext, lines) -> {
		for (FluidTooltipCallback callback : listeners) {
			callback.getTooltip(volume, tooltipContext, lines);
		}
	});

	/**
	 * Called when a fluid volume's tooltip is rendered. Text added to {@code lines} will be
	 * rendered with the tooltip.
	 *
	 * @param lines the list containing the lines of text displayed on the volume's tooltip
	 */
	void getTooltip(FluidVolume volume, TooltipContext tooltipContext, List<Text> lines);
}
