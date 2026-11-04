#!/bin/sh
set -eu

# Unabhängig vom aktuellen Arbeitsverzeichnis im Projektverzeichnis arbeiten.
cd "$(dirname "$0")"

archive="de.thm.mni.vs.gruppeF.tgz"

# Nur die für die Abgabe benötigten Dateien und den Maven-Wrapper verpacken.
# macOS-Metadaten werden nicht in das Archiv aufgenommen.
COPYFILE_DISABLE=1 tar -czf "$archive" --exclude='.DS_Store' \
    pom.xml README.md src mvnw mvnw.cmd .mvn

printf 'Abgabe erstellt: %s\n' "$archive"
