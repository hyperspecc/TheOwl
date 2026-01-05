package com.bamboosession.mixin;

import com.bamboosession.BambooSession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.ProfileKeys;
import net.minecraft.client.session.Session;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Final
    private YggdrasilAuthenticationService authenticationService;

    @Shadow
    @Final
    public File runDirectory;

    @Unique
    private UUID bamboo$lastUuid = null;

    @Unique
    private String bamboo$lastToken = null;

    @Unique
    private ProfileKeys bamboo$cachedProfileKeys = null;

    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    private void bamboo$onGetSession(CallbackInfoReturnable<Session> cir) {
        Session customSession = BambooSession.getCurrentSession();
        if (customSession != null) {
            cir.setReturnValue(customSession);
        }
    }

    @Inject(method = "getProfileKeys", at = @At("HEAD"), cancellable = true)
    private void bamboo$onGetProfileKeys(CallbackInfoReturnable<ProfileKeys> cir) {
        if (!BambooSession.overrideActive) {
            return;
        }

        Session currentSession = BambooSession.currentSession;
        UUID currentUuid = currentSession.getUuidOrNull();
        String currentToken = currentSession.getAccessToken();

        // Check if session changed
        if (bamboo$lastUuid == null ||
                !bamboo$lastUuid.equals(currentUuid) ||
                bamboo$lastToken == null ||
                !bamboo$lastToken.equals(currentToken)) {

            bamboo$lastUuid = currentUuid;
            bamboo$lastToken = currentToken;

            BambooSession.LOGGER.info("Creating ProfileKeys for: {}", currentSession.getUsername());

            try {
                UserApiService userApiService = authenticationService.createUserApiService(currentToken);
                Path profileKeysPath = runDirectory.toPath().resolve("profilekeys");
                bamboo$cachedProfileKeys = ProfileKeys.create(userApiService, currentSession, profileKeysPath);

                BambooSession.LOGGER.info("Successfully created ProfileKeys");
            } catch (Exception e) {
                BambooSession.LOGGER.error("Failed to create ProfileKeys: {}", e.getMessage());
                bamboo$cachedProfileKeys = null;
            }
        }

        if (bamboo$cachedProfileKeys != null) {
            cir.setReturnValue(bamboo$cachedProfileKeys);
        }
    }
}