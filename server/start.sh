#!/usr/bin/env bash
# Vanilla Plus server: syncs the pack with packwiz, keeps NeoForge on the
# pack's version, then runs the server. Restarts after a stop or crash, and
# every restart pulls the latest pack. Press Ctrl+C during the countdown to quit.
set -euo pipefail
cd "$(dirname "$0")"

PACK_URL="${PACK_URL:-https://raw.githubusercontent.com/queen-kiki/vanilla-plus-pack/master/pack.toml}"
MEMORY="${MEMORY:-6G}"

# Pinned and hash-checked, so a compromised or replaced release can't slip in.
# packwiz-installer's own updater is turned off below (--bootstrap-no-update).
BOOTSTRAP_URL="https://github.com/packwiz/packwiz-installer-bootstrap/releases/download/v0.0.3/packwiz-installer-bootstrap.jar"
BOOTSTRAP_SHA256="a8fbb24dc604278e97f4688e82d3d91a318b98efc08d5dbfcbcbcab6443d116c"
INSTALLER_URL="https://github.com/packwiz/packwiz-installer/releases/download/v0.5.14/packwiz-installer.jar"
INSTALLER_SHA256="c9f646908d340d84773948a9a7d98bc1dae250d35e1016dc6e2b8459760b5598"

# Downloads $1 to $3 unless $3 already has SHA-256 $2. Refuses a file that doesn't match.
fetch() {
  if [ -f "$3" ] && [ "$(sha256sum "$3" | cut -d' ' -f1)" = "$2" ]; then return; fi
  curl -fsSL -o "$3.part" "$1"
  if [ "$(sha256sum "$3.part" | cut -d' ' -f1)" != "$2" ]; then
    rm -f "$3.part"
    echo "Hash mismatch for $1, refusing to use it." >&2
    exit 1
  fi
  mv "$3.part" "$3"
}

if ! grep -qs '^eula=true' eula.txt; then
  echo "eula=false" > eula.txt
  echo "Read https://aka.ms/MinecraftEULA, then set eula=true in eula.txt and run this again."
  exit 1
fi

[ -f server.properties ] || cp server.properties.defaults server.properties

while true; do
  fetch "$BOOTSTRAP_URL" "$BOOTSTRAP_SHA256" packwiz-installer-bootstrap.jar
  fetch "$INSTALLER_URL" "$INSTALLER_SHA256" packwiz-installer.jar

  # NeoForge version comes from the pack, so a loader bump updates the server too.
  # Its installer hash sits next to the pack in server/neoforge.sha256.
  NEOFORGE="$(curl -fsSL "$PACK_URL" | sed -n 's/^neoforge = "\(.*\)"/\1/p')"
  if [ -z "$NEOFORGE" ]; then
    echo "Couldn't read the NeoForge version from $PACK_URL" >&2
    exit 1
  fi
  if [ ! -f "libraries/net/neoforged/neoforge/$NEOFORGE/unix_args.txt" ]; then
    NEOFORGE_JAR="neoforge-$NEOFORGE-installer.jar"
    NEOFORGE_SHA256="$(curl -fsSL "${PACK_URL%/*}/server/neoforge.sha256" | awk -v f="$NEOFORGE_JAR" '$2 == f { print $1 }')"
    if [ -z "$NEOFORGE_SHA256" ]; then
      echo "No hash for $NEOFORGE_JAR in server/neoforge.sha256, refusing to install it." >&2
      exit 1
    fi
    echo "Installing NeoForge $NEOFORGE..."
    fetch "https://maven.neoforged.net/releases/net/neoforged/neoforge/$NEOFORGE/$NEOFORGE_JAR" "$NEOFORGE_SHA256" neoforge-installer.jar
    java -jar neoforge-installer.jar --installServer
    rm -f neoforge-installer.jar neoforge-installer.jar.log run.sh run.bat
  fi

  java -jar packwiz-installer-bootstrap.jar -g --bootstrap-no-update -s server "$PACK_URL"

  java -Xms"$MEMORY" -Xmx"$MEMORY" @"libraries/net/neoforged/neoforge/$NEOFORGE/unix_args.txt" nogui "$@" || true

  echo "Server stopped. Restarting in 10 seconds (Ctrl+C to quit)..."
  sleep 10
done
