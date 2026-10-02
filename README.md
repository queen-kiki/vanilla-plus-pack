# Vanilla Plus

NeoForge 1.21.1 vanilla+ modpack (with Create Aeronautics), managed with [packwiz](https://packwiz.infra.link/).

## Installing (Prism Launcher)

1. Download [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar).
2. In Prism: **Add Instance** → Minecraft **1.21.1**, then **Edit → Version → Install Loader → NeoForge 21.1.252**.
3. Open the instance folder and put `packwiz-installer-bootstrap.jar` in the `minecraft` folder (create it if missing).
4. **Edit → Settings → Custom commands** → tick *Custom Commands* and set **Pre-launch command** to:

   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml
   ```

5. **Edit → Settings → Java**: set **Maximum memory** (see below).
6. Launch. Mods, shaders and configs download automatically and update on every launch.

Your graphics, voice chat and zoom settings are only set once and never overwritten.
If an update was just published and you don't get it, relaunch after ~5 minutes (GitHub caches files briefly).

## Memory

| Setup | Maximum memory |
|---|---|
| Recommended | **6 GB** (6144 MB) |
| Minimum (Distant Horizons off) | 4 GB (4096 MB) |

More isn't better: giving Java far more than it needs only makes garbage-collection pauses longer.

## Weaker PCs

The heavy client-side mods are **optional**. On first install the installer shows a checklist; to change it later,
click **Optional mods...** in the installer window that appears when you launch (it waits 10 seconds for you).

Biggest wins, in order:

1. **Shaders** are off by default. Press **K** in-game to try them; turn them off again if FPS drops. Untick *Iris* to remove them entirely.
2. **Distant Horizons**: untick it (and *SSRD*) to save a lot of CPU, RAM and disk. Or lower its quality in its settings.
3. **Sound Physics Remastered** and **AmbientSounds**: untick to save CPU.
4. **Particular**, **Subtle Effects**, **3D Skin Layers**, **LambDynamicLights**: small savings each.
5. In **Video Settings**, lower *Render Distance* (8–10 is plenty with Distant Horizons) and *Simulation Distance*.

## Keybinds

Pack defaults. Anything can be rebound in **Options → Controls → Key Binds** (with search and conflict highlighting).

| Key | Action |
|---|---|
| **K** | Toggle shaders |
| **O** | Shader pack selection |
| **Caps Lock** | Voice chat: push to talk |
| **V** | Voice chat menu |
| **N** | Voice chat on/off |
| **J** | Voice chat groups |
| **C** | Zoom (scroll to adjust) |
| **M** | Map Atlases: open map |
| **B** | Map Atlases: place pin |
| **Numpad + / −** | Minimap zoom |
| **I** | Curios (accessory slots) |
| **Y** | Supplementaries quiver |
| **G** / **H** | Create Jetpack: on/off / hover |
| **Z** | Estrogen dash |
| **Tab** | Aeronautics: rotate mode (while using its tools) |
| **R** / **U** | EMI: recipes / uses of the hovered item (in inventories) |
| **hold W** | Create Ponder: animated tutorial for the hovered item |
| **hold Left Alt** | Create tool menu / toolbelt |
| **Numpad 0** | Jade settings |
| **F10** | Presence Footsteps settings |

## Maintaining (pack owner)

```sh
packwiz modrinth add <mod-slug>   # add a mod
packwiz update <mod>              # update one mod (see "Updating safely")
packwiz remove <mod>              # remove a mod
packwiz refresh                   # after editing configs by hand
git commit -am "..." && git push
```

The **Vanilla Plus (dev)** Prism instance installs straight from this folder via `dev-sync.cmd`,
so changes can be tested before pushing.

`tweaks-src/` holds the source of `mods/vanillaplus-tweaks.jar` (data-only glue between mods).
Rebuild it with `python tweaks-src/build.py`.

### Updating safely

Every launch installs whatever is in this repo on every player's PC, so:

- Keep **two-factor authentication** on the GitHub account that can push here.
- Update mods deliberately, not with a blind `packwiz update --all`. Prefer versions that have been out for a few days,
  and skim `git diff` before pushing.
- Only add mods from Modrinth (packwiz pins each file to a SHA-512 hash).
- Test updates in the dev instance first.
