# Vanilla Plus

Fabric 1.21.1 vanilla+ modpack, managed with [packwiz](https://packwiz.infra.link/).

## Installing (Prism Launcher)

1. Download [packwiz-installer-bootstrap.jar](https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar).
2. In Prism: **Add Instance** → Minecraft **1.21.1**, then **Edit → Version → Install Loader → Fabric 0.19.5**.
3. Open the instance folder and put `packwiz-installer-bootstrap.jar` in the `minecraft` folder (create it if missing).
4. **Edit → Settings → Custom commands** → tick *Custom Commands* and set **Pre-launch command** to:

   ```
   "$INST_JAVA" -jar packwiz-installer-bootstrap.jar PACK_URL
   ```

5. Launch. Mods, shaders and configs download automatically and update on every launch.

Your graphics/voice-chat/zoom settings are only set once and never overwritten.

## Maintaining (pack owner)

```sh
packwiz modrinth add <mod-slug>   # add a mod
packwiz update --all              # update everything
packwiz remove <mod>              # remove a mod
packwiz refresh                   # after editing configs by hand
git commit -am "..." && git push
```
