#!/bin/bash
# Deploy to GitHub Pages,
# based on a script from https://gist.github.com/domenic/ec8b0fc8ab45f39403dd

# Exit with nonzero exit code if anything fails
set -e

# Go to the target directory and create a new Git repo.
# Any old one should have been removed by the maven 'clean' command.
cd html/target/sim-html-1.0-SNAPSHOT
git init

# Inside this git repo we'll pretend to be a new user
git config user.name "Travis CI"
git config user.email "${CI_EMAIL}"

# Commit the Web deployment files to the repository, ignoring folders that are unnecessary,
# and commit as "Deploy to GitHub Pages".
git add . 
git reset -- WEB-INF META-INF
git commit -m "Deploy to GitHub Pages"

# Force push from the current repo's master branch to the remote
# repo's gh-pages branch. (All previous history on the gh-pages branch
# will be lost, since we are overwriting it.) We redirect any output to
# /dev/null to hide any sensitive credential data that might otherwise be exposed.
git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages  > /dev/null 2>&1

