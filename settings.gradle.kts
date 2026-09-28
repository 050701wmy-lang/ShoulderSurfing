@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            content {
                includeGroupAndSubgroups("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            content {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
        maven("https://maven.neoforged.net/releases/") {
            content {
                includeGroupAndSubgroups("net.neoforged")
                includeGroup("codechicken")
                includeGroup("net.covers1624")
            }
        }
        maven("https://maven.minecraftforge.net/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

rootProject.name = "ShoulderSurfing"

include(
    ":api",
    ":common",
    ":compat",
    ":forge",
    ":neoforge",
    ":fabric"
)
