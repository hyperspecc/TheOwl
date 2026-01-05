package com.bamboosession.mixin;

import com.bamboosession.BambooSession;
import com.bamboosession.screens.BambooLoginScreen;
import com.bamboosession.screens.BambooEditScreen;
import com.bamboosession.utils.BambooAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiplayerScreenMixin extends Screen {

    @Unique
    private Boolean bamboo$isValid = null;

    @Unique
    private boolean bamboo$validating = false;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void bamboo$onInit(CallbackInfo ci) {
        bamboo$isValid = null;
        bamboo$validating = false;

        int x = this.width - 90;
        int y = 5;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Login"),
                btn -> MinecraftClient.getInstance().setScreen(new BambooLoginScreen())
        ).dimensions(x, y, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Edit"),
                btn -> MinecraftClient.getInstance().setScreen(new BambooEditScreen())
        ).dimensions(x - 90, y, 80, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void bamboo$onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String username = BambooSession.currentSession.getUsername();

        if (bamboo$isValid == null && !bamboo$validating) {
            bamboo$validating = true;
            new Thread(() -> {
                try {
                    bamboo$isValid = BambooAPI.validateSession(
                            BambooSession.currentSession.getAccessToken(),
                            BambooSession.currentSession.getUsername(),
                            BambooSession.currentSession.getUuidOrNull().toString()
                    );
                } catch (Exception e) {
                    bamboo$isValid = false;
                }
            }, "BambooValidation").start();
        }

        Text status;
        if (bamboo$isValid == null) {
            status = Text.literal("[...] Validating").formatted(Formatting.GRAY);
        } else if (bamboo$isValid) {
            status = Text.literal("[✓] Valid").formatted(Formatting.GREEN);
        } else {
            status = Text.literal("[✗] Invalid").formatted(Formatting.RED);
        }

        Text display = Text.literal("User: ")
                .append(Text.literal(username).formatted(Formatting.WHITE))
                .append(Text.literal(" | ").formatted(Formatting.DARK_GRAY))
                .append(status);

        context.drawText(this.textRenderer, display, 5, 10, 0xFFFFFF, false);
    }
}