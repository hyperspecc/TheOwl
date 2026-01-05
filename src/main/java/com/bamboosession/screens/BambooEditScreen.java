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

public class BambooEditScreen extends Screen {
    private TextFieldWidget nameField;
    private TextFieldWidget skinField;
    private ButtonWidget nameButton;
    private ButtonWidget skinButton;
    private Text statusMessage;

    public BambooEditScreen() {
        super(Text.literal("Edit Account"));
        this.statusMessage = Text.literal("(Bamboo) Edit Account").formatted(Formatting.AQUA);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Username field
        nameField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY - 40,
                200,
                20,
                Text.literal("(Bamboo) New Username")
        );
        nameField.setMaxLength(16);
        nameField.setFocused(true);
        this.addSelectableChild(nameField);

        // Skin URL field
        skinField = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY,
                200,
                20,
                Text.literal("(Bamboo) Skin URL")
        );
        skinField.setMaxLength(2048);
        this.addSelectableChild(skinField);

        // Change name button
        nameButton = ButtonWidget.builder(Text.literal("(Bamboo) Change Name"), button -> {
            String newName = nameField.getText().trim();

            if (newName.isEmpty()) {
                statusMessage = Text.literal("(Bamboo) Please enter a name").formatted(Formatting.RED);
                return;
            }

            if (!newName.matches("^[a-zA-Z0-9_]{3,16}$")) {
                statusMessage = Text.literal("(Bamboo) Invalid name format").formatted(Formatting.RED);
                return;
            }

            int code = BambooAPI.changeName(newName, BambooSession.currentSession.getAccessToken());
            statusMessage = switch (code) {
                case 200 -> {
                    BambooSession.setSession(BambooSession.createSession(
                            newName,
                            BambooSession.currentSession.getUuidOrNull().toString(),
                            BambooSession.currentSession.getAccessToken()
                    ));
                    yield Text.literal("Name changed successfully").formatted(Formatting.GREEN);
                }
                case 400 -> Text.literal("Invalid name").formatted(Formatting.RED);
                case 401 -> Text.literal("Invalid token").formatted(Formatting.RED);
                case 403 -> Text.literal("Name unavailable or changed recently").formatted(Formatting.RED);
                case 429 -> Text.literal("Too many requests").formatted(Formatting.RED);
                default -> Text.literal("Unknown error").formatted(Formatting.RED);
            };
        }).dimensions(centerX - 100, centerY + 25, 97, 20).build();
        this.addDrawableChild(nameButton);

        // Change skin button
        skinButton = ButtonWidget.builder(Text.literal("Change Skin"), button -> {
            String skinUrl = skinField.getText().trim();

            if (skinUrl.isEmpty()) {
                statusMessage = Text.literal("Please enter a URL").formatted(Formatting.RED);
                return;
            }

            int code = BambooAPI.changeSkin(skinUrl, BambooSession.currentSession.getAccessToken());
            statusMessage = switch (code) {
                case 200 -> Text.literal("Skin changed successfully").formatted(Formatting.GREEN);
                case 401 -> Text.literal("Invalid token").formatted(Formatting.RED);
                case 429 -> Text.literal("Too many requests").formatted(Formatting.RED);
                case -1 -> Text.literal("Connection error").formatted(Formatting.RED);
                default -> Text.literal("Invalid skin URL").formatted(Formatting.RED);
            };
        }).dimensions(centerX + 3, centerY + 25, 97, 20).build();
        this.addDrawableChild(skinButton);

        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"), button -> {
            assert this.client != null;
            this.client.setScreen(new MultiplayerScreen(new TitleScreen()));
        }).dimensions(centerX - 100, centerY + 50, 200, 20).build());

        // Disable if using original session
        if (!BambooSession.isUsingCustomSession()) {
            nameButton.active = false;
            skinButton.active = false;
            statusMessage = Text.literal("(Bamboo) Cannot modify original session").formatted(Formatting.YELLOW);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Username:"),
                this.width / 2 - 100, this.height / 2 - 52, 0xA0A0A0);
        nameField.render(context, mouseX, mouseY, delta);

        context.drawTextWithShadow(this.textRenderer, Text.literal("Skin URL:"),
                this.width / 2 - 100, this.height / 2 - 10, 0xA0A0A0);
        skinField.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, statusMessage,
                this.width / 2, this.height / 2 - 75, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return nameField.keyPressed(keyCode, scanCode, modifiers) ||
                skinField.keyPressed(keyCode, scanCode, modifiers) ||
                super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return nameField.charTyped(chr, modifiers) ||
                skinField.charTyped(chr, modifiers) ||
                super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean nameFocus = nameField.mouseClicked(mouseX, mouseY, button);
        boolean skinFocus = skinField.mouseClicked(mouseX, mouseY, button);

        nameField.setFocused(nameFocus);
        skinField.setFocused(skinFocus);

        return nameFocus || skinFocus || super.mouseClicked(mouseX, mouseY, button);
    }
}