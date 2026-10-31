package com.vyn.alwaysday;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Client-side only: forces noon and clear weather on YOUR screen.
 * The server is not changed, other players still see their normal time/weather.
 * Type /alwaysday to turn it off or on.
 */
public class AlwaysDayClient implements ClientModInitializer {

    private static final long NOON = 6000L;
    private static boolean enabled = true;

    @Override
    public void onInitializeClient() {
        System.out.println("[AlwaysDay] loaded");

        // Runs every tick, after the server's time/weather packets were applied,
        // so what gets drawn is always noon with no rain, snow or thunder.
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!enabled) return;
            world.setTime(world.getTime(), NOON, false); // keep game time, freeze day time at noon
            world.setRainGradient(0.0f);
            world.setThunderGradient(0.0f);
        });

        // /alwaysday toggles it (handled on the client, never sent to the server)
        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {
            if (!command.trim().equalsIgnoreCase("alwaysday")) return true;
            enabled = !enabled;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("AlwaysDay is now ").formatted(Formatting.GRAY)
                        .append(Text.literal(enabled ? "ON" : "OFF")
                                .formatted(enabled ? Formatting.GREEN : Formatting.RED)), false);
            }
            return false;
        });
    }
}
