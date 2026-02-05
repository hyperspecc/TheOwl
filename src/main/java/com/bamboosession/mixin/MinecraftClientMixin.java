package com.theowl.mixin;

import com.theowl.TheOwl;
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
    private UUID theowl$lastUuid = null;

    @Unique
    private String theowl$lastToken = null;

    @Unique
    private ProfileKeys theowl$cachedProfileKeys = null;

    @Inject(method = "getSession", at = @At("HEAD"), cancellable = true)
    private void theowl$onGetSession(CallbackInfoReturnable<Session> cir) {
        Session customSession = TheOwl.getCurrentSession();
        if (customSession != null) {
            cir.setReturnValue(customSession);
        }
    }

    @Inject(method = "getProfileKeys", at = @At("HEAD"), cancellable = true)
    private void theowl$onGetProfileKeys(CallbackInfoReturnable<ProfileKeys> cir) {
        if (!TheOwl.overrideActive) {
            return;
        }

        Session currentSession = TheOwl.currentSession;
        UUID currentUuid = currentSession.getUuidOrNull();
        String currentToken = currentSession.getAccessToken();

        if (theowl$lastUuid == null ||
                !theowl$lastUuid.equals(currentUuid) ||
                theowl$lastToken == null ||
                !theowl$lastToken.equals(currentToken)) {

            theowl$lastUuid = currentUuid;
            theowl$lastToken = currentToken;

            TheOwl.LOGGER.info("Creating ProfileKeys for: {}", currentSession.getUsername());

            try {
                UserApiService userApiService = authenticationService.createUserApiService(currentToken);
                Path profileKeysPath = runDirectory.toPath().resolve("profilekeys");
                theowl$cachedProfileKeys = Profile
