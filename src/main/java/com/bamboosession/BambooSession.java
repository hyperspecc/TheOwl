package com.bamboosession;

import net.minecraft.client.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class BambooSession {
    public static final Logger LOGGER = LoggerFactory.getLogger("bamboo-session");

    public static Session originalSession;
    public static Session currentSession;
    public static boolean overrideActive = false;

    public static void initialize(Session initialSession) {
        originalSession = initialSession;
        currentSession = initialSession;
        overrideActive = true;
        LOGGER.info("Bamboo Session initialized");
    }

    public static Session createSession(String username, String uuidString, String accessToken) {
        // Format UUID if needed (remove dashes if present, then add them back)
        String formattedUuid = uuidString.replaceAll("-", "");

        if (formattedUuid.length() == 32) {
            formattedUuid = formattedUuid.substring(0, 8) + "-" +
                    formattedUuid.substring(8, 12) + "-" +
                    formattedUuid.substring(12, 16) + "-" +
                    formattedUuid.substring(16, 20) + "-" +
                    formattedUuid.substring(20);
        }

        return new Session(
                username,
                UUID.fromString(formattedUuid),
                accessToken,
                Optional.empty(),
                Optional.empty(),
                Session.AccountType.MSA
        );
    }

    public static void setSession(Session session) {
        currentSession = session;
        LOGGER.info("(bamboo) Session changed to: {}", session.getUsername());
    }

    public static void restoreOriginalSession() {
        currentSession = originalSession;
        LOGGER.info("Restored original session: {}", originalSession.getUsername());
    }

    public static Session getCurrentSession() {
        return overrideActive ? currentSession : null;
    }

    public static boolean isUsingCustomSession() {
        return !currentSession.equals(originalSession);
    }
}