#!/usr/bin/env bash
# Vanilla Plus server: syncs the pack with packwiz, keeps NeoForge on the
# pack's version, then runs the server. Restarts after a stop or crash, and
# every restart pulls the latest pack. Press Ctrl+C during the countdown to quit.
set -euo pipefail
cd "$(dirname "$0")"

PACK_URL="${PACK_URL:-https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml}"
MEMORY="${MEMORY:-6G}"
BOOTSTRAP_URL="https://github.com/packwiz/packwiz-installer-bootstrap/releases/latest/download/packwiz-installer-bootstrap.jar"

if ! grep -qs '^eula=true' eula.txt; then
  echo "eula=false" > eula.txt
  echo "Read https://aka.ms/MinecraftEULA, then set eula=true in eula.txt and run this again."
  exit 1
fi

[ -f server.properties ] || cp server.properties.defaults server.properties
[ -f packwiz-installer-bootstrap.jar ] || curl -fsSL -o packwiz-installer-bootstrap.jar "$BOOTSTRAP_URL"

while true; do
  # NeoForge version comes from the pack, so a loader bump updates the server too
  NEOFORGE="$(curl -fsSL "$PACK_URL" | sed -n 's/^neoforge = "\(.*\)"/\1/p')"
  if [ -z "$NEOFORGE" ]; then
    echo "Couldn't read the NeoForge version from $PACK_URL" >&2
    exit 1
  fi
  if [ ! -f "libraries/net/neoforged/neoforge/$NEOFORGE/unix_args.txt" ]; then
    echo "Installing NeoForge $NEOFORGE..."
    curl -fsSL -o neoforge-installer.jar \
      "https://maven.neoforged.net/releases/net/neoforged/neoforge/$NEOFORGE/neoforge-$NEOFORGE-installer.jar"
    java -jar neoforge-installer.jar --installServer
    rm -f neoforge-installer.jar neoforge-installer.jar.log run.sh run.bat
  fi

  java -jar packwiz-installer-bootstrap.jar -g -s server "$PACK_URL"

  java -Xms"$MEMORY" -Xmx"$MEMORY" @"libraries/net/neoforged/neoforge/$NEOFORGE/unix_args.txt" nogui "$@" || true

  echo "Server stopped. Restarting in 10 seconds (Ctrl+C to quit)..."
  sleep 10
done
