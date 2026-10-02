# Plan: airless End and habs

The End has no air. You visit in a diving helmet and backtank, and to stay you build sealed habs with oxygen vents. Goes into our mod in `tweaks/`, building on `Oxygen.java` and on Create: Deep Seas, which we're adding anyway.

## Adding Create: Deep Seas

Checked in `create_submarine-3.3.0.jar` (Modrinth `mva5q4qZ`, ARR, NeoForge 1.21.1):

- **Requires** Create, Sable 2.0.3+, Aeronautics 1.3.2+ (with Simulated) and Iris & Oculus Flywheel Compat (`iris-flw-compat`). We already have everything except the last one.
- **Bundles** Fusion and Lithostitched, which we already have.
- **Shaders**: the author advises against Iris because of Veil lighting. Iris & Oculus Flywheel Compat is required anyway, and Iris Veil Compat is suggested. Test with our shaders before shipping.
- **Deeper oceans**: an on-by-default option (`enableDeeperOceans`) changes the overworld terrain shape through Lithostitched. That can fight Tectonic. Look at a new world with it on, and turn it off if the terrain looks wrong.
- **Abyss dimension**: an extra dimension (`create_abyss:abyss`). Keep our air rules out of it unless testing shows a reason.
- **Fluid mixins**: it patches `FlowingFluid` and Sodium's fluid renderer. Watch for clashes with our other rendering mods.

## What Deep Seas gives us, and what it doesn't

- **Electrolyzer**: turns water into `create_submarine:oxygen` fluid. It runs on FE, or on rotation through a shaft fitted in its slot, so pure Create works. Oxygen buckets exist too.
- **Oxygen Diffuser**: uses that oxygen to fill a sealed submarine with air, and needs a redstone signal. It **only works on Sable physics structures**: its scanner (`CompartmentDetector.beginScan(SubLevelAccess)`) and tracker (`CompartmentTracker`, keyed by sub-level UUID) never look at normal world blocks. A base built normally in the End won't count.
- **Its "air" means no water**: Deep Seas' oxygen pushes water out of the hull. It doesn't decide whether a player can breathe, so we still make that call ourselves.
- **Useful hooks** (public static): `CompartmentDetector.isPermeable(BlockState)` tells us which blocks Deep Seas treats as sealing, and `CompartmentTracker` exposes the sealed compartments of each sub-level. Using these keeps our habs and their submarines consistent. Because the mod is ARR, we call it, never copy it, and it stays an optional dependency in code.

## Pieces to build

1. **Airless End**: generalise `Oxygen.isThinAir` from "overworld above Y" to a per-dimension rule. Overworld keeps the height rule; the End becomes airless everywhere. The Create helmet and backtank already supply air through `LivingBreatheEvent`, so they work there with no extra code. Use its own damage type, with a death message like "ran out of air in the End".
2. **Hab vent**: our block. It takes `create_submarine:oxygen` by pipe and needs a redstone signal, like the Diffuser.
   - On a change, it flood-fills the air from the vent outward, up to a cap (config, a few thousand blocks).
   - If the fill reaches the cap or open sky, the room leaks and the vent stops. If it's enclosed, the room is sealed, and the vent stores its block set.
   - It uses oxygen in proportion to the room's size, like the Diffuser. When it runs dry, the room loses air.
3. **Breathing check**: in `Oxygen.onBreathe`, a player whose head is in an active vent's sealed room can breathe. To keep it cheap: keep a per-level index from chunk to the rooms in that chunk, and look up the head position there.
4. **Keeping rooms correct**: on block place, break, piston or explosion events inside or next to a stored room, mark that vent for a rescan on its next tick. Closed doors and trapdoors seal and open ones leak (check `isPermeable` here), so an airlock is two doors.
5. **Goggles**: Create goggle info on the vent, for example "Sealed, 640 blocks, 12 mB/s" or "Leaking near x y z". Same idea as the Diffuser's tooltip.
6. **Air readout**: the client altitude readout (`ClientEvents`) also shows air status in the End: in a hab, on backtank, or out of air.
7. **Sable habs (later)**: a player inside a sealed `CompartmentTracker` compartment that has a working Diffuser can also breathe. That makes airships and the planned physics raft work as mobile habs in the End.
8. **Content (later)**: wrecked hab modules in End cities with loot (backtanks, vent parts), an advancement for your first sealed hab, and a welcome-book page.

## Decisions still open

1. Does the airless rule apply to players only, or to mobs too? Endermen, shulkers and the dragon obviously breathe fine. Players only is simplest.
2. Vent fuel: Deep Seas oxygen only, or also plain rotation as a cheaper, weaker option?
3. Arrival: does the obsidian platform get a short-lived starter air bubble, or does arriving without gear mean dying? A middle ground is a warning when you right-click an End portal without a helmet and a filled backtank.
4. Endermen pick up blocks. Should they be kept from taking hab walls (only blocks in the `enderman_holdable` tag are at risk, mostly natural ones), or is a surprise leak part of the fun?
5. Room size cap and oxygen cost per block.
6. Does the Nether get anything similar (smoke, heat)? Probably not now.

## Suggested order

1. Add Deep Seas and its Iris compat. Test shaders and new-world terrain.
2. Airless End using the existing helmet and backtank.
3. Hab vent with flood fill, then the breathing check and rescans, then goggles.
4. Air readout.
5. Sable habs.
6. Content.

## Risks

- **Flood-fill cost**: keep it capped, run it only when something changed, and spread big scans over several ticks (Deep Seas does the same with `stepScan`).
- **Deep Seas internals**: they may change between versions. We touch only the two public hooks above and guard them with a mod-loaded check, so the airless End and hab vents keep working if Deep Seas changes or is removed.
- **Dragon fight**: everyone needs a backtank. Check how long a backtank lasts against how long a typical fight takes before balancing the cost.
