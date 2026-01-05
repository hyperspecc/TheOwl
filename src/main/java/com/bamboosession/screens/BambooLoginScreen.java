package com.bamboosession.screens;

import com.bamboosession.BambooSession;
import com.bamboosession.utils.BambooAPI;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BambooLoginScreen extends Screen {
    private TextFieldWidget sessionField;
    private ButtonWidget loginButton;
    private ButtonWidget restoreButton;
    private Text statusMessage;

    public BambooLoginScreen() {
        super(Text.literal("Bamboo Session Login"));
        this.statusMessage = Text.literal("(Bamboo) Enter your session ID").formatted(Formatting.GOLD);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Session input field
        sessionField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY,
                200,
                20,
                Text.literal("Session ID")
        );
        sessionField.setMaxLength(32767);
        sessionField.setFocused(true);
        this.addSelectableChild(sessionField);

        // Login button
        loginButton = ButtonWidget.builder(Text.literal("Login"), button -> {
            String sessionId = sessionField.getText().trim();

            if (sessionId.isEmpty()) {
                statusMessage = Text.literal("Session ID cannot be empty").formatted(Formatting.RED);
                return;
            }

            try {
                BambooAPI.ProfileInfo info = BambooAPI.getProfileInfo(sessionId);
                BambooSession.setSession(BambooSession.createSession(
                        info.username,
                        info.uuid,
                        sessionId
                ));

                statusMessage = Text.literal("Bamboo logged you in as: " + info.username)
                        .formatted(Formatting.GREEN);
                restoreButton.active = true;

            } catch (Exception e) {
                statusMessage = Text.literal("Invalid session ID").formatted(Formatting.RED);
                BambooSession.LOGGER.error("Login failed: {}", e.getMessage());
            }
        }).dimensions(centerX - 100, centerY + 25, 97, 20).build();
        this.addDrawableChild(loginButton);

        // Restore button
        restoreButton = ButtonWidget.builder(Text.literal("Restore"), button -> {
            BambooSession.restoreOriginalSession();
            statusMessage = Text.literal("Restored original session").formatted(Formatting.GREEN);
            restoreButton.active = false;
        }).dimensions(centerX + 3, centerY + 25, 97, 20).build();
        restoreButton.active = BambooSession.isUsingCustomSession();
        this.addDrawableChild(restoreButton);

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
        }).dimensions(centerX - 100, centerY + 50, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        sessionField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                statusMessage,
                this.width / 2,
                this.height / 2 - 30,
                0xFFFFFF
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return sessionField.keyPressed(keyCode, scanCode, modifiers) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return sessionField.charTyped(chr, modifiers) ||
                super.charTyped(chr, modifiers);
    }
}