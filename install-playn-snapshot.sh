#!/usr/bin/env bash
# Downloads and installs the current playn snapshot

# Exit with nonzero exit code if anything fails
set -e

# Echo each command to facilitate debugging
set -x

# Clone the current playn repository, build it, and install it
mkdir playn-snapshot
cd playn-snapshot
git clone https://github.com/playn/playn.git
cd playn
mvn clean install

