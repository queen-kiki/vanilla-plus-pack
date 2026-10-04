#!/usr/bin/env python
"""Builds install/VanillaPlus.zip: a Prism Launcher instance that's ready to go.
It has Minecraft and NeoForge at the pack's versions, the pinned packwiz jars,
the pre-launch command and 6 GB of memory, so players only import it from a URL.

    python tools/prism-instance/build.py

Rerun it after bumping NeoForge in pack.toml or the pinned packwiz-installer.
The zip is deterministic, so rebuilding without changes leaves git clean.
"""
import hashlib
import json
import re
import urllib.request
import zipfile
from pathlib import Path

PACK_URL = "https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml"
# Same pins as server/start.sh
JARS = {
    "packwiz-installer-bootstrap.jar": (
        "https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar",
        "a8fbb24dc604278e97f4688e82d3d91a318b98efc08d5dbfcbcbcab6443d116c",
    ),
    "packwiz-installer.jar": (
        "https://github.com/packwiz/packwiz-installer/releases/download/v0.5.14/packwiz-installer.jar",
        "c9f646908d340d84773948a9a7d98bc1dae250d35e1016dc6e2b8459760b5598",
    ),
}

REPO = Path(__file__).resolve().parents[2]
OUTPUT = REPO / "install" / "VanillaPlus.zip"


def download(url, sha256):
    data = urllib.request.urlopen(url).read()
    if hashlib.sha256(data).hexdigest() != sha256:
        raise SystemExit(f"Hash mismatch for {url}")
    return data


def main():
    pack = (REPO / "pack.toml").read_text()
    minecraft = re.search(r'^minecraft = "(.+)"', pack, re.M)[1]
    neoforge = re.search(r'^neoforge = "(.+)"', pack, re.M)[1]

    instance_cfg = "\n".join([
        "[General]",
        "ConfigVersion=1.3",
        "InstanceType=OneSix",
        "name=Vanilla Plus",
        "OverrideMemory=true",
        "MinMemAlloc=512",
        "MaxMemAlloc=6144",
        # Generational ZGC: short pauses, so fewer lag spikes than the default G1
        "OverrideJavaArgs=true",
        "JvmArgs=-XX:+UseZGC -XX:+IgnoreUnrecognizedVMOptions -XX:+ZGenerational",
        "OverrideCommands=true",
        # Prism runs this in the instance's minecraft folder, next to the jars
        f'PreLaunchCommand=\\"$INST_JAVA\\" -jar packwiz-installer-bootstrap.jar --bootstrap-no-update {PACK_URL}',
        "",
    ])
    mmc_pack = json.dumps({
        "components": [
            {"uid": "net.minecraft", "version": minecraft, "important": True},
            {"uid": "net.neoforged", "version": neoforge},
        ],
        "formatVersion": 1,
    }, indent=4) + "\n"

    files = {"instance.cfg": instance_cfg.encode(), "mmc-pack.json": mmc_pack.encode()}
    for name, (url, sha256) in JARS.items():
        files[f"minecraft/{name}"] = download(url, sha256)

    OUTPUT.parent.mkdir(exist_ok=True)
    with zipfile.ZipFile(OUTPUT, "w", zipfile.ZIP_DEFLATED) as z:
        for name, data in sorted(files.items()):
            info = zipfile.ZipInfo(name, date_time=(1980, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, data)
    print(f"Wrote {OUTPUT.relative_to(REPO)} (Minecraft {minecraft}, NeoForge {neoforge})")


if __name__ == "__main__":
    main()
