"""Rebuild mods/vanillaplus-tweaks.jar from this folder. Run: python tweaks-src/build.py"""
import os, zipfile
root = os.path.dirname(os.path.abspath(__file__))
out = os.path.join(root, '..', 'mods', 'vanillaplus-tweaks.jar')
with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as z:
    for d, _, files in os.walk(root):
        for f in files:
            if f == 'build.py': continue
            p = os.path.join(d, f)
            info = zipfile.ZipInfo(os.path.relpath(p, root).replace(os.sep, '/'), (1980, 1, 1, 0, 0, 0))
            z.writestr(info, open(p, 'rb').read(), zipfile.ZIP_DEFLATED)
print('built', os.path.normpath(out))
