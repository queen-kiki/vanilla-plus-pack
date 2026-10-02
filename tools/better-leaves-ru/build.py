#!/usr/bin/env python
"""Builds resourcepacks/vanillaplus-better-leaves-ru: Better Leaves models for the
Regions Unexplored leaves that Motschen's Better Leaves doesn't cover yet.

Runs Better Leaves Lite's own generator on just those textures, so the result
matches the main pack. Needs git and: pip install pillow tqdm requests setuptools

    python tools/better-leaves-ru/build.py path/to/regions-unexplored-*.jar
"""
import json
import os
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path

BETTER_LEAVES_REPO = "https://github.com/TeamMidnightDust/BetterLeavesLite"
BETTER_LEAVES_COMMIT = "2d283056560fb6d1d9121c7090e575f2389a768f"  # 9.6

REPO = Path(__file__).resolve().parents[2]
OUTPUT = REPO / "resourcepacks" / "vanillaplus-better-leaves-ru"
RU = "regions_unexplored"

# Source textures, and the blockstate each one drives when it isn't the texture's own name.
TEXTURES = {
    "wisteria_leaves": None,
    "alpha_oak_leaves": None,
    "flowering_leaves_flower": None,
    "apple_oak_leaves_stage_0": "age=0",
    "apple_oak_leaves_stage_1": "age=2",
    "apple_oak_leaves_stage_2": "age=4",
}
OVERRIDES = {
    "noTint": [
        f"{RU}:alpha_leaves",
        f"{RU}:apple_oak_leaves_stage_0",
        f"{RU}:apple_oak_leaves_stage_1",
        f"{RU}:apple_oak_leaves_stage_2",
    ],
    "leavesWithCarpet": {},
    "blockTextures": {},
    "overlayTextures": {},
    # Flowering leaves are oak leaves with a flower layer on top.
    "overlayVariants": {f"{RU}:flowering_leaves": "minecraft:block/oak_leaves"},
    "blockIds": {
        f"{RU}:alpha_oak_leaves": f"{RU}:alpha_leaves",
        f"{RU}:flowering_leaves_flower": f"{RU}:flowering_leaves",
    },
    "dynamicTreesNamespaces": {},
    "generateItemModels": [],
    # The coloured wisterias share one grey texture and are tinted in code.
    "blockStateCopies": {
        f"{RU}:wisteria_leaves": [
            f"{RU}:lavender_wisteria_leaves",
            f"{RU}:salmon_wisteria_leaves",
            f"{RU}:sky_wisteria_leaves",
        ]
    },
    "compileOnly": [],
}
# Generated textures that must not ship: Better Leaves already has an identical
# alpha_oak_leaves, and the flower overlay has to stay at Regions Unexplored's 16px.
DROP_TEXTURES = ["alpha_oak_leaves", "flowering_leaves_flower"]

# The generator splits paths on "/", so normalise them on Windows.
RUNNER = """
import os, sys
_walk, _join = os.walk, os.path.join
os.walk = lambda top, *a, **k: ((r.replace("\\\\", "/"), d, f) for r, d, f in _walk(top, *a, **k))
os.path.join = lambda *p: _join(*p).replace("\\\\", "/")
sys.argv = ["gen_pack.py", "9.6", "-m"]
exec(open("gen_pack.py", encoding="utf-8").read())
"""


def main(ru_jar):
    with tempfile.TemporaryDirectory() as tmp:
        gen = Path(tmp) / "bll"
        subprocess.run(["git", "clone", "-q", BETTER_LEAVES_REPO, str(gen)], check=True)
        subprocess.run(["git", "-C", str(gen), "checkout", "-q", BETTER_LEAVES_COMMIT], check=True)

        # Replace the generator's input with only our textures.
        textures = gen / "input" / "assets"
        shutil.rmtree(textures)
        block_dir = textures / RU / "textures" / "block"
        block_dir.mkdir(parents=True)
        with zipfile.ZipFile(ru_jar) as jar:
            for name, state in TEXTURES.items():
                (block_dir / f"{name}.png").write_bytes(jar.read(f"assets/{RU}/textures/block/{name}.png"))
                if state:
                    data = {"blockStateData": {"block": f"{RU}:apple_oak_leaves", "state": state}}
                    (block_dir / f"{name}.betterleaves.json").write_text(json.dumps(data))
        (gen / "input" / "overrides.json").write_text(json.dumps(OVERRIDES))
        (gen / "run.py").write_text(RUNNER)

        # Zipping the full pack fails without its icon; we only want the assets anyway.
        subprocess.run([sys.executable, "run.py"], cwd=gen)
        generated = gen / "assets" / RU
        if not (generated / "blockstates" / "wisteria_leaves.json").exists():
            sys.exit("Better Leaves generator produced no output")

        for name in DROP_TEXTURES:
            (generated / "textures" / "block" / f"{name}.png").unlink()

        # Apple oak leaves have five ages but only three textures.
        apple = generated / "blockstates" / "apple_oak_leaves.json"
        state = json.loads(apple.read_text())
        state["variants"]["age=1"] = state["variants"]["age=0"]
        state["variants"]["age=3"] = state["variants"]["age=2"]
        apple.write_text(json.dumps(state, separators=(",", ":")))

        if OUTPUT.exists():
            shutil.rmtree(OUTPUT)
        shutil.copytree(generated, OUTPUT / "assets" / RU)

    (OUTPUT / "pack.mcmeta").write_text(json.dumps({"pack": {
        "pack_format": 34,
        "description": "Better Leaves for the Regions Unexplored leaves it misses",
    }}, indent=2) + "\n")
    print(f"Wrote {OUTPUT}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    main(sys.argv[1])
