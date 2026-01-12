plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "me.daoge.chainmining"
description = "A chain mining plugin for AllayMC that automatically breaks adjacent blocks of the same type"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allay {
    api = "0.21.0"

    plugin {
        entrance = ".ChainMining"
        authors += "daoge_cmd"
        website = "https://github.com/smartcmd/ChainMining"
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    implementation(group = "eu.okaeri", name = "okaeri-configs-yaml-snakeyaml", version = "5.0.13")
}
