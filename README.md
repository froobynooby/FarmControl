# FarmControl

**Plugin page**: [https://www.spigotmc.org/resources/86923/](https://www.spigotmc.org/resources/86923/)

## About
FarmControl is a Bukkit plugin that allows you to control certain properties of farms on your server. Among other things, you can limit the size of mob farms, remove the ability of mobs in farms to collide and perform random movements, or completely disable the AI of mobs in farms.

## Building

1. Install dependency NabConfiguration to maven local
```bash
git clone https://github.com/froobynooby/nab-configuration
cd nab-configuration
./gradlew clean install
```
2. Clone FarmControl and build
```bash
git clone https://github.com/froobynooby/FarmControl
cd ViewDistanceTweaks
./gradlew clean build
```

3. Find jar in `FarmControl/build/libs`
