# Cycles - Lessons Learned

## Build System
- No Android SDK on dev machine — all builds happen via GitHub Actions CI
- System Gradle (4.4.1) is too old for Android; must use Gradle wrapper
- Gradle wrapper JAR can be downloaded from `https://raw.githubusercontent.com/gradle/gradle/v{VERSION}/gradle/wrapper/gradle-wrapper.jar`

## Architecture Decisions
- Using AGP 8.10.1 (not 9.0) to avoid breaking changes in initial scaffold
- Single :app module to start; modularize later when complexity warrants it
- Manual DI via singleton pattern; Hilt deferred until it adds clear value
- Type-safe navigation requires kotlinx-serialization plugin + dependency
