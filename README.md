# Groovy Engine for NeoForge

A powerful and flexible modding engine for Minecraft, built on NeoForge. Groovy Engine provides a robust framework for creating dynamic and maintainable Minecraft mods using Groovy scripting language.

## Features

- **Modular Architecture**: Built with a modular design for easy extension and maintenance
- **Groovy Scripting**: Write mod logic in Groovy for rapid development
- **Event System**: Comprehensive event handling system for game interactions
- **Resource Management**: Built-in resource pack generation and management

## Getting Started

### Prerequisites

- Java 21 or later
- Gradle 8.0+
- Minecraft 1.21.1
- NeoForge

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/LuckyMcDev/groovyengine-neoforge.git
   cd groovyengine-neoforge
   ```
2. Build the project:
   ```bash
   ./gradlew build
   ```

## Project Structure

```
groovyengine-neoforge/
├── src/
│   ├── main/
│   │   ├── java/io/github/luckymcdev/groovyengine/
│   │   │   ├── core/         # Core engine components
│   │   │   ├── construct/    # Game object construction system
│   │   │   ├── lens/         # Data transformation and mapping
│   │   │   ├── threads/      # Thread management
│   │   │   └── util/         # Utility classes
│   │   └── resources/        # Resource files
├── run/                      # Runtime files and configurations
└── libs/                     # External library dependencies
```

## Documentation

See the documentation starting in [Home Page](wiki/HomePage.md)

## License

This project is licensed under the ![GitHub License](https://img.shields.io/github/license/perxenic-studios/groovyengine-neoforge).

## Contact

For questions or support, please open an issue on our [GitHub repository](https://github.com/LuckyMcDev/groovyengine-neoforge/issues),
Or visit the Discord: [Discord](https://discord.gg/dUefmxFvWr)

## Stats

![GitHub release (latest by date)](https://img.shields.io/github/v/release/perxenic-studios/groovyengine-neoforge)
![GitHub](https://img.shields.io/github/license/perxenic-studios/groovyengine-neoforge)
![GitHub last commit](https://img.shields.io/github/last-commit/perxenic-studios/groovyengine-neoforge)

---

Made with Love by LuckyMcDev