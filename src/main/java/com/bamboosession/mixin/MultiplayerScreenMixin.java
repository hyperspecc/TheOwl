package com.theowl.mixin;

import com.theowl.TheOwl;
import com.theowl.screens.TheOwlLoginScreen;
import com.theowl.screens.TheOwlEditScreen;
import com.theowl.utils.TheOwlAPI;
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
    private Boolean theowl$isValid = null;

    @Unique
    private boolean theowl$validating = false;

    protected MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void theowl$onInit(CallbackInfo ci) {
        theowl$isValid = null;
        theowl$validating = false;

        int x = this.width - 90;
        int y = 5;

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Login"),
                btn -> MinecraftClient.getInstance().setScreen(new TheOwlLoginScreen())
        ).dimensions(x, y, 80, 20).build());

        this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Edit"),
                btn -> MinecraftClient.getInstance().setScreen(new TheOwlEditScreen())
        ).dimensions(x - 90, y, 80, 20).build());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void theowl$onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        String username = TheOwl.currentSession.getUsername();

        if (theowl$isValid == null && !theowl$validating) {
            theowl$validating = true;
            new Thread(() -> {
                try {
                    theowl$isValid = TheOwlAPI.validateSession(
                            TheOwl.currentSession.getAccessToken(),
                            TheOwl.currentSession.getUsername(),
                            TheOwl.currentSession.getUuidOrNull().toString()
                    );
                } catch (Exception e) {
                    theowl$isValid = false;
                }
            }, "TheOwlValidation").start();
        }

        Text status;
        if (theowl$isValid == null) {
            status = Text.literal("[...] Validating").formatted(Formatting.GRAY);
        } else if (theowl$isValid) {
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
