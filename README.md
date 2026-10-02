# Vanilla Plus

Our NeoForge 1.21.1 pack: vanilla+ with bigger terrain, cozy stuff, and Create Aeronautics. Managed with [packwiz](https://packwiz.infra.link/).

## Install (Prism Launcher)

1. Download [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar).
2. In Prism, add an instance for Minecraft 1.21.1, then go to Edit → Version → Install Loader and pick NeoForge 21.1.252.
3. Open the instance folder and put the jar into the `minecraft` folder (create it if it's not there).
4. Edit → Settings → Custom commands: tick Custom Commands and use this as the pre-launch command:

   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml
   ```

5. Edit → Settings → Java: set maximum memory to 6144 MB.
6. Launch. Everything downloads, and it updates itself every time you start the game.

Your own graphics, voice chat and zoom settings won't get overwritten by updates.
If you just missed an update, restart in a few minutes. GitHub takes a moment to catch up.

## Memory

6 GB is plenty. 4 GB works if you turn off Distant Horizons. Giving it a lot more doesn't help and can make lag spikes worse.

## If it runs badly

Some of the heavier client mods are optional. You get a checklist the first time you install, and you can change it later with the "Optional mods..." button in the window that pops up when you launch (it waits 10 seconds).

What helps most:

- Shaders are off by default. Press K to try them and press it again if your FPS tanks. Untick Iris to get rid of them completely.
- Untick Distant Horizons (and SSRD with it). That saves a lot of CPU, RAM and disk space.
- Untick Sound Physics Remastered and AmbientSounds.
- Particular, Subtle Effects, 3D Skin Layers and LambDynamicLights each save a little.
- Lower render distance and simulation distance in Video Settings. With Distant Horizons on, 8 to 10 chunks is enough.

## Keybinds

These are the pack defaults. You can change any of them under Options → Controls → Key Binds, which has a search bar and shows conflicts.

| Key | Action |
|---|---|
| K | Toggle shaders |
| O | Pick shader pack |
| Caps Lock | Voice chat: push to talk |
| V | Voice chat menu |
| N | Voice chat on/off |
| J | Voice chat groups |
| C | Zoom (scroll to adjust) |
| M | Open map (Map Atlases) |
| B | Place map pin |
| Numpad + / − | Minimap zoom |
| I | Curios slots |
| Y | Quiver (Supplementaries) |
| G / H | Jetpack on/off / hover |
| Z | Dash (Estrogen) |
| Tab | Rotate mode for Aeronautics tools |
| R / U | Recipes / uses of the item under your cursor (EMI) |
| Hold W | Ponder: animated Create tutorial for the hovered item |
| Hold Left Alt | Create tool menu and toolbelt |
| Numpad 0 | Jade settings |
| F10 | Footstep sound settings |

## Working on the pack

```sh
packwiz modrinth add <mod-slug>   # add a mod
packwiz update <mod>              # update a mod
packwiz remove <mod>              # remove a mod
packwiz refresh                   # after editing config files by hand
git commit -am "..." && git push
```

The "Vanilla Plus (dev)" instance in Prism installs straight from this folder through `dev-sync.cmd`, so you can try changes before pushing them.

`tweaks-src/` is the source of our own little mod, `mods/vanillaplus-tweaks.jar`. Rebuild it with `python tweaks-src/build.py`.
