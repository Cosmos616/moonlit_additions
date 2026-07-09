plugins {
    // https://projects.neoforged.net/neoforged/ModDevGradle
    id("net.neoforged.moddev") version "2.0.141"
    idea
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

version = mod["version"]
group = mod["group"]
base.archivesName = mod["id"]

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neo["version"]

    validateAccessTransformers = true

    parchment {
        mappingsVersion = project.parchment["mappingsVersion"]
        minecraftVersion = project.parchment["minecraftVersion"]
    }

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }

        create("client") {
            client()
            devLogin = true
            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        }

        create("server") {
            server()
            programArgument("--nogui")
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod", mod["id"],
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
                "--existing-mod", "nomansland"
            )
        }
    }

    mods {
        create(mod["id"]) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

val localRuntime: Configuration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://maven.wispforest.io/releases/")
    maven("https://maven.su5ed.dev/releases")
    maven("https://maven.blamejared.com/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.theillusivec4.top/")
    maven("https://maven.ryanhcode.dev/releases")
}

dependencies {
    accessTransformers(("team.lodestar.lodestone:lodestone:${mc["version"]}-${deps["lodestone"]}"))
    jarJar(api("dev.ryanhcode.sable-companion:sable-companion-common-${mc["version"]}:${deps["sable_companion"]}") {
        version {
            prefer(deps["sable_companion"])
        }
    })

    compileOnlyApi("dev.emi:emi-neoforge:${deps["emi"]}+${mc["version"]}:api")
    localRuntime("dev.emi:emi-neoforge:${deps["emi"]}+${mc["version"]}")

    compileOnlyApi(("team.lodestar.lodestone:lodestone:${mc["version"]}-${deps["lodestone"]}"))
    runtimeOnly(("team.lodestar.lodestone:lodestone:${mc["version"]}-${deps["lodestone"]}"))
    compileOnlyApi(("top.theillusivec4.curios:curios-neoforge:${deps["curios"]}+${mc["version"]}"))
    runtimeOnly(("top.theillusivec4.curios:curios-neoforge:${deps["curios"]}+${mc["version"]}"))
    implementation("maven.modrinth:no-mans-land:${deps["nomansland"]}")

    runtimeOnly("maven.modrinth:emi-loot:${deps["emiLoot"]}+1.21+neoforge")
    runtimeOnly("maven.modrinth:fzzy-config:${deps["fzzyConfig"]}+1.21+neoforge")
    runtimeOnly("maven.modrinth:kotlin-for-forge:${deps["kotlin"]}")

    compileOnly("io.wispforest:accessories-neoforge:${deps["accessories"]}+${mc["version"]}")
    runtimeOnly("io.wispforest:accessories-neoforge:${deps["accessories"]}+${mc["version"]}")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to mc["version"],
        "minecraft_version_range" to mc["versionRange"],
        "neo_version" to neo["version"],
        "neo_version_range" to neo["versionRange"],
        "loader_version_range" to neo["loaderVersionRange"],
        "mod_id" to mod["id"],
        "mod_name" to mod["name"],
        "mod_license" to mod["license"],
        "mod_version" to mod["version"],
        "mod_authors" to mod["authors"],
        "mod_description" to mod["description"]
    )

    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}
