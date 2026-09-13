# Super Mod (Fabric 1.20.1)

Adds: a 10x-faster furnace, a hoe-only "Farming Fortune" enchantment, two new
armor sets, a Wardrobe for quick armor-loadout swapping, five more
enchantments, and a Skull Bench for reskinning helmets.

## ⚠️ Important: this is source code, not a compiled jar

I wrote this offline, without internet access, so I could not run
`gradlew build` (which needs to download Minecraft + Fabric libraries) or
launch the game to test it. Everything here is real, complete Fabric mod
source following standard 1.20.1 patterns, but you'll need to do the actual
build yourself (takes about 2 minutes with an internet connection):

```bash
cd supermod
./gradlew build      # Linux/macOS
gradlew.bat build     # Windows
```

The finished jar shows up in `build/libs/supermod-1.0.0.jar` — drop that into
your `.minecraft/mods` folder (Fabric Loader 0.15+ and Fabric API for
1.20.1 also need to be installed).

If you don't have the Gradle wrapper jar (it's a binary file I couldn't
include), just open the project folder in IntelliJ IDEA or run `gradle wrapper`
once first — either will fetch it for you.

## What might need a small fix

I'm confident in the structure and ~90% of this code, but a few spots rely on
exact Minecraft/Yarn method signatures I couldn't verify without compiling.
If the build fails, these are the first places to check:

1. **`mixin/StemBlockMixin.java`** — hooks `StemBlock#randomTick` to detect
   when a melon/pumpkin grows from a stem (needed for the "only from stem"
   rule on Farming Fortune). If the method signature differs slightly in
   your mappings, adjust the `@Inject` target.
2. **`client/render/SkullHelmetArmorRenderer.java`** — the actual skull-skin
   rendering. This is the single trickiest part of the whole mod (custom
   armor rendering + player-skin texture fetching). It's wrapped in a
   try/catch so a texture-lookup failure just shows the plain helmet instead
   of crashing, but the visual alignment may need nudging.
3. Textures are all **placeholder recolors** I generated procedurally (no
   internet access to pull real Minecraft textures) — 16x16 pixel art that's
   readable but not polished. Swap in your own art via a resource pack or by
   replacing the PNGs under `src/main/resources/assets/supermod/textures/`.

Happy to help debug specific error messages if you hit any while building.

## Feature → file map

| # | Feature | Files |
|---|---|---|
| 1 | Super Smelter (10x smelting) | `block/SuperSmelterBlock(Entity).java`, `screen/SuperSmelterScreenHandler.java`, `client/screen/SuperSmelterScreen.java`, recipe `data/.../super_smelter.json` |
| 2 | Farming Fortune enchant (hoe-only, Librarian trade, melon/pumpkin-from-stem, faster hoe mining) | `enchantment/FarmingFortuneEnchantment.java`, `village/ModVillagerTrades.java`, `util/StemGrowthTracker.java`, `mixin/StemBlockMixin.java`, `mixin/HoeItemMixin.java`, `ModEvents.java` |
| 3 | Farming & Mining armor sets + their own enchantments | `item/ModArmorMaterials.java`, `item/ModItems.java`, `enchantment/GreenThumbEnchantment.java`, `enchantment/ProspectorSenseEnchantment.java`, recipes `data/.../farming_*.json` & `mining_*.json` |
| 4 | Wardrobe (armor loadout quick-swap) + inventory button | `block/WardrobeBlock(Entity).java`, `screen/WardrobeScreenHandler.java`, `client/screen/WardrobeScreen.java`, `mixin/InventoryScreenMixin.java`, `ModNetworking.java`, `util/LastWardrobeTracker.java` |
| 5 | Teletele, Smelting Touch, Night Goggles, Hard Miner | `enchantment/*.java`, all behavior in `ModEvents.java` |
| 6 | Skull Bench (skull skins on helmets) | `block/SkullBenchBlock.java`, `screen/SkullBenchScreenHandler.java`, `client/screen/SkullBenchScreen.java`, `client/render/SkullHelmetArmorRenderer.java` |

## Design notes / simplifications

- **Farming Fortune** is a "treasure" enchantment (`isAvailableForRandomSelection()
  = false`), so it can never appear at the enchanting table or in loot — the
  Librarian trade is genuinely the only source, exactly as requested.
- **Stem-grown tracking** for melon/pumpkin is kept in memory (not saved to
  the world file) for simplicity/reliability — a server restart forgets which
  melons/pumpkins were "natural", but everything grown after a restart is
  tracked normally again.
- **Hard Miner** breaks 2-3 neighboring blocks outright rather than
  re-triggering the full break-event pipeline, so those bonus blocks don't
  themselves proc Teletele/Smelting Touch/Farming Fortune (avoids recursive
  chains).
- **Wardrobe loadouts** are 4 slots (helmet/chest/legs/boots) × 4 saved sets.
  The in-inventory quick-swap button reopens whichever Wardrobe you last used
  (also in-memory only).
- Farming Fortune bonus formula: `random(0, level)` extra of the harvested
  crop/produce, same spirit as vanilla Fortune on ores.
