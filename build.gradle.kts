plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.froobworld"
version = "1.3.0"

tasks {
    jar {
        enabled = false
    }

    processResources {
        filter { line ->
            line.replace("\${version}", project.version.toString())
        }
    }

    shadowJar {
        archiveFileName.set("FarmControl-${project.version}.jar")

        relocate("com.froobworld.nabconfiguration", "com.froobworld.farmcontrol.lib.nabconfiguration")
        relocate("org.joor", "com.froobworld.farmcontrol.lib.joor")
        relocate("org.bstats", "com.froobworld.farmcontrol.lib.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")

    implementation("org.jooq:joor-java-8:0.9.14")
    implementation("com.github.froobynooby:nab-configuration:master-SNAPSHOT")
    implementation("org.bstats:bstats-base:3.0.2")
}

artifacts {
    add("archives", tasks.shadowJar)
}