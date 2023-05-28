# FarmControl
[Hangar](https://hangar.papermc.io/froobynooby/FarmControl) | [Modrinth](https://modrinth.com/plugin/farmcontrol) | [Spigot](https://www.spigotmc.org/resources/86923/)

## About
FarmControl is a Bukkit plugin that allows you to control certain properties of farms on your server.

**Features**:
* Disable breeding in oversized animal farms and villager breeders.
* Reduce unnecessary random movement within mob farms.
* Disable the AI of mobs in farms.
* Limit the number of mobs allowed in a farm.
* Highly configurable - allowing you to tailor the plugin to your needs.
* Low impact - with the brunt of the plugin's processing performed asynchronously.

## Building
If you would like to build the plugin yourself you can follow these steps.

1\. Install dependency NabConfiguration to maven local
```bash
git clone https://github.com/froobynooby/nab-configuration
cd nab-configuration
./gradlew clean install
```
2\. Clone FarmControl and build
```bash
git clone https://github.com/froobynooby/FarmControl
cd FarmControl
./gradlew clean build
```

3\. Find jar in `FarmControl/build/libs`
