# Plan: tgirl zombies and the forcefem gun

Goes into our mod in `tweaks/`. Builds on Estrogen (effect, attributes, sounds, particles) so it looks and feels like the rest of the pack.

## What Estrogen gives us, and what it doesn't

Checked in the Estrogen 6.0.10 jar:

- **Girl Power effect** (`estrogen:estrogen`). Works on any mob.
- **Attributes** `estrogen:boob_initial_size`, `estrogen:boob_growing_start_time`, `estrogen:show_boobs`, `estrogen:dash_level`. Registered for players only (`EstrogenAttributeEvents`), but we can attach them to zombies with `EntityAttributeModificationEvent`.
- **Body rendering** (`BoobFeatureLayer`). Player-only: typed to `Player` and uses the player skin. Can't reuse for zombies.
- **Dash**. Player-only: a client keybind (`ClientDash`) that sends `DashPacket`. Mobs need their own version.
- **Particles**: `estrogen:colored_cloud` takes a colour (good for the gun), `estrogen:dash`.
- **Sounds**: the dash has its own sound (`subtitles.estrogen.dash`).
- **Thigh highs** are a Curios item in the `thighs` slot; Curios doesn't render on mobs.

Mob effects aren't synced to other clients (they only see particle colours), so we sync our own marker.

## Pieces to build

1. **Girl Power marker on mobs**: a synced data attachment on zombies: `active` + `size`. Set while the mob has Girl Power, cleared when it ends (unless permanent, see decisions).
2. **Render layer for zombies**: our own `RenderLayer` on the zombie renderer: two small boxes on the chest using the zombie's own skin, scaled by `size`, with a little bounce. Matches Estrogen's proportions as closely as we can. Shown whenever the marker is set.
3. **Tgirl zombie spawns**: in `FinalizeSpawnEvent`, a share of natural zombie spawns get permanent Girl Power and a random starting size. Saved with the mob, so it doesn't reroll on reload. Optional: size grows slowly over time, like Estrogen's growth attribute.
4. **Zombie dash**: an AI goal. When the target is 5–12 blocks away, on the ground, and the cooldown is up (a few seconds): burst toward the target, play Estrogen's dash sound, puff of pink cloud particles.
5. **Forcefem gun**: an item. Hold right-click to spray a cone of pink `colored_cloud` particles a few blocks long. Everything in the cone gets Girl Power, refreshed while you keep spraying. Players get the real Estrogen effect (dash + body). Zombies get the marker and turn into tgirl zombies. Needs a texture, a recipe, and some cost (idea: runs on Estrogen's liquid, refilled with a Create spout, like the backtank).
6. **Armed tgirl zombies**: rarely, a tgirl zombie spawns holding the gun and sprays players in range instead of only hitting. Small chance to drop the gun.
7. **Loot**: tgirl zombies sometimes drop Estrogen items.

## Decisions still open

1. Spawn rate. Suggestion: about 15% of zombies, and about 1 in 50 of those with the gun.
2. Does a sprayed zombie stay a tgirl zombie, or turn back when the effect ends?
3. Zombies only, or also zombie villagers, husks and drowned (same model family, little extra work)?
4. Thigh highs on tgirl zombies (trans flag pair)? Would have to be drawn in our layer.
5. Gun recipe and fuel.

## Suggested order

Marker + render layer first (spawn a few with a command to see them), then natural spawns, then the dash, then the gun, then armed zombies and loot.

## Before starting

- The tweaks 2.0.0 work and the trans flag splash are committed but not pushed. Test them in the dev instance first, then push.
- Still on the list: FancyMenu title screen.
- Later idea: make the spawn raft a real Create Aeronautics raft (a Sable physics sub-level that floats and drifts) instead of the static 3x3 platform. Needs Sable's API for spawning sub-levels; Sable ships an example schematic (`data/sable/schematics/vinalilime.nbt`).
