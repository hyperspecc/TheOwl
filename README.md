# Bamboo Session ID Login

A Minecraft Fabric mod for version 1.21.5 that allows you to log in to multiplayer servers using your session ID instead of your Microsoft account.

## ⚠️ Disclaimer

This mod was created as a clean, safe alternative to existing session login mods that were found to contain malicious code. Use at your own risk and only on servers where you have permission to use alternative authentication methods.

## Features

- Login to multiplayer servers using session ID
- Client-side only - no server-side changes needed
- Lightweight and simple
- Clean, open-source code with no malicious components

## Installation

1. Make sure you have [Fabric Loader](https://fabricmc.net/use/) installed for Minecraft 1.21.5
2. Download [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release of Bamboo Session Login
4. Place both the Fabric API and Bamboo Session Login `.jar` files in your `.minecraft/mods` folder
5. Launch Minecraft with the Fabric profile

## Usage

1. Obtain your session ID (typically from your Minecraft launcher logs or authentication flow)
2. Use the mod's interface to input your session ID when connecting to servers
3. Connect to multiplayer servers as normal

## Building from Source

```bash
git clone https://github.com/Star2likesgirls/bamboo-session-id-login.git
cd bamboo-session-id-login
./gradlew build
```

The compiled `.jar` file will be in `build/libs/`

## Requirements

- Minecraft 1.21.5
- Fabric Loader 0.18.4 or higher
- Fabric API
- Java 21 or higher

## Why This Exists

The original open-source session login mod was compromised with malicious code (RAT) in both the mod itself and the Gradle wrapper. This is a clean reimplementation to provide the same functionality without security risks.

## Security

This mod is completely open source. You're encouraged to review the code yourself. If you find any security concerns, please open an issue immediately.

## License

This project is licensed under CC0-1.0 - feel free to use, modify, and distribute as you see fit.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## Support

If you encounter any issues, please open an issue on the GitHub repository with:
- Your Minecraft version
- Your Fabric Loader version
- The full crash report or error log
- Steps to reproduce the problem

---

**Note:** This mod is for educational and convenience purposes. Always ensure you have permission to use alternative authentication methods on any server you connect to.
