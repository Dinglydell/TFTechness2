# TFTechness2
This is a rewrite of my awful [TFTechness](https://github.com/Dinglydell/TFTechness/) mod.

This version has a significantly scaled down scope - I'm focusing on creating and maintaining a playable (and balanced) version instead of frantically adding every idea that pops into my head leaving the whole thing a mess of half-working features. Also, it helps that the new version is not using any team CoFH mods as they're far too magic-boxy and really don't fit with TFC. (instead, it currently uses [ImmersiveEngineering](https://github.com/BluSunrize/ImmersiveEngineering))

Things it does so far: (not an exhaustive list)
* Major recipe rebalance for tech gating
* Rebalance includes: (see image below)
	* IE wire coils require rolling machine, such that players have to go through Railcraft steam power to reach IE electrical power
	* Railcraft coke oven and blast furnace unobtainable; use IE instead
	* IE hammer requires steel, so coke oven and treated wood (and therefore pretty much the whole of IE) is gated behind steel
	* Wooden rails don't require creosote so that they are available much earlier
	* Low pressure boiler requires steel; high pressure requires black steel
* Added bauxite as a TFC-style ore that can be found in basalt, gneiss and shale.
* Added heatable TFC style ingots and sheets for the IE metals (constantan, electrum, aluminium)
* IE metal blocks can be heated in a forge and melted into 9 ingots (crafting recipe still exists for now)
* Nuggets are crafted with molds, ratio is 1:10
* Added fluids for molten metals - when placed in the world they will cool down and solidify into a block
* 1 mB of molten metal = 1 TFC unit of metal
* Removed recipe for crude blast furnace (you have TFC for this)
* Added wooden creosote buckets
* Added coke oven recipe for bitumious coal -> coal coke
* Added coke oven recipe for TFC logs -> charcoal
* Food weight now shown in KG as well as this mythical unit referred to as "oz"
* Added crusher recipe for bauxite -> crushed bauxite
* Heating crushed bauxite produces alumina
* Added the electrolyser - a multiblock machine for processing alumina into aluminium (with a "lovely" placeholder texture)

Ugly but readable tech tree: (drawn by a program I wrote, hence the poor presentation)
![it no work](https://github.com/Dinglydell/TFTechness2/raw/master/TechTree-2017-10-29.png "The Ugly Tree")


List of dependencies:
* [TerraFirmaCraft](https://github.com/Deadrik/TFCraft)
* [ImmersiveEngineering](https://github.com/BluSunrize/ImmersiveEngineering)
* [Railcraft](https://github.com/Railcraft/Railcraft)

Recommended complimentary mods: (eventually, I plan to make a formalised modpack which will probably include these)
* [NotEnoughItems](https://github.com/Chicken-Bones/NotEnoughItems)
* [TFC NEI Plugin](https://github.com/dries007/TFCraft-NEIplugin)
* [NEI Integration](https://github.com/Tonius/NEI-Integration/)
* [Waila](https://bitbucket.org/ProfMobius/waila/)


Planned dependencies:
* [BigReactors](https://github.com/erogenousbeef/BigReactors) (maybe)
* [AdvancedRocketry](https://github.com/zmaster587/AdvancedRocketry)

Planned progression/gating:
* There will be several "eras" in which a single mod is the main focus (most things you do will be from this mod)
* TerraFirmaCraft (stone age, bronze age, iron age) -> Railcraft (steam age) -> ImmersiveEngineering (electrical age) -> AdvancedRocketry (space age)
