# Vanilla Plus

Our NeoForge 1.21.1 pack: vanilla+ with bigger terrain, cozy stuff, and Create Aeronautics. Managed with [packwiz](https://packwiz.infra.link/).

## Install (Prism Launcher)

You need [Prism Launcher](https://prismlauncher.org/download/) (free).

1. In Prism, click Add Instance → Import, paste this link and click OK:

   ```
   https://github.com/queen-kiki/vanilla-plus-pack/raw/master/install/VanillaPlus.zip
   ```

2. Launch it. Everything downloads, and it updates itself every time you start the game.

The instance comes set up with NeoForge, 6 GB of memory, a low-lag garbage collector and the packwiz installer, pinned to a fixed version so it can't update itself behind your back.

<details>
<summary>Setting it up by hand, or updating an older install</summary>

1. Download [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar) and [packwiz-installer.jar](https://github.com/packwiz/packwiz-installer/releases/download/v0.5.14/packwiz-installer.jar).
2. In Prism, add an instance for Minecraft 1.21.1, then go to Edit → Version → Install Loader and pick NeoForge 21.1.252.
3. Open the instance folder and put both jars into the `minecraft` folder (create it if it's not there).
4. Edit → Settings → Custom commands: tick Custom Commands and use this as the pre-launch command:

   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar --bootstrap-no-update https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml
   ```

5. Edit → Settings → Java: set maximum memory to 6144 MB.
6. In the same tab, tick Java arguments and enter `-XX:+UseZGC -XX:+IgnoreUnrecognizedVMOptions -XX:+ZGenerational`. That fixes most lag spikes from garbage collection.

If you set the pack up before the installer was pinned (pack 2.3.1), add `--bootstrap-no-update` to your pre-launch command and drop `packwiz-installer.jar` next to the bootstrap. That stops it from updating itself.

</details>

Your own graphics, voice chat and zoom settings won't get overwritten by updates.
If you just missed an update, restart in a few minutes. GitHub takes a moment to catch up.

## Server

The `server` folder has start scripts that set up and update a server by themselves. You need Java 21.

1. Copy `start.sh` (Linux) or `start.bat` (Windows) and `server.properties.defaults` into an empty folder.
2. Run the script once. It creates `eula.txt`; set `eula=true` in it.
3. Run the script again. It installs NeoForge, downloads the server-side mods and configs with packwiz, and starts the server.

Every time the server stops or crashes, it restarts after 10 seconds and pulls the latest pack first, so `/stop` is all it takes to update. If the pack bumps NeoForge, the script installs the new version too. To quit for real, press Ctrl+C (or close the window) during the countdown.

It uses 6 GB of RAM by default. Set the `MEMORY` environment variable to change that, e.g. `MEMORY=8G ./start.sh`.

Everything the scripts download is checked against a SHA-256 hash, and they refuse anything that doesn't match. packwiz-installer is pinned to a fixed version, NeoForge is checked against `server/neoforge.sha256`, and the mods are checked against the hashes in the pack index.

## Resource packs

The default resource-pack selection and order live in `config/defaultoptions-common.toml`, including mod-provided packs. Default Options applies this selection once on a fresh install, so it won't overwrite your choices on later launches.

Several packs change the same plants or entities, so pack priority affects which changes are visible. Allure 3D Plants is designed to pair with Allure. Fresh Animations is selected by default; the Minecraft 1.21.1-compatible Better Animations release is available if you prefer it instead. Entity Model Features and Entity Texture Features are included for animation-pack support. Wild Vanilla is not included because its only release uses the newer 1.21.4 resource-pack format.

Leaves use Motschen's Better Leaves, which covers vanilla, Vinery and most Regions Unexplored trees in one style. The small `vanillaplus-better-leaves-ru` pack fills in the Regions Unexplored leaves it misses (wisteria, apple oak, flowering and alpha leaves). It is built with Better Leaves' own generator by `tools/better-leaves-ru/build.py`. Rerun that script after updating Regions Unexplored or Better Leaves.

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

When you bump NeoForge in `pack.toml`, add the new installer's hash to `server/neoforge.sha256` too, or servers will refuse to install it. Then rebuild the Prism instance with `python tools/prism-instance/build.py`, which picks up the new NeoForge version:

```sh
v=21.1.300; echo "$(curl -fsSL https://maven.neoforged.net/releases/net/neoforged/neoforge/$v/neoforge-$v-installer.jar | sha256sum | cut -d' ' -f1)  neoforge-$v-installer.jar" >> server/neoforge.sha256
```

`tweaks/` is the source of our own mod, `mods/vanillaplus-tweaks.jar` (oxygen up high, alpine biomes above the treeline, seasonal airship lift, the altitude readout, autumn harvests, the advancement tab and so on). Rebuild it with `gradlew install` inside `tweaks/` (needs a Java 21+ JDK; Prism's bundled Java works). The pack version shown in-game comes from `pack.toml`, so rebuild after changing it.
