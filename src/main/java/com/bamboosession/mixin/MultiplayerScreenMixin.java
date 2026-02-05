package com.bamboosession.mixin;

import com.bamboosession.TheOwl;
import com.bamboosession.screens.TheOwlLoginScreen;
import com.bamboosession.screens.TheOwlEditScreen;
import com.bamboosession.utils.TheOwlAPI;
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

        if (theowl$isValid == null &&
