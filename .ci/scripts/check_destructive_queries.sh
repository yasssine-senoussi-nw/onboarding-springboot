#!/bin/bash

####################### Colors #######################
color_red='\033[0;31m'
color_green='\033[0;32m'
color_yellow='\033[0;33m'
color_default='\033[0m'

CHANGELOG_DIR="src/main/resources/db/changelog"

WHITELIST_FILE="src/main/resources/db/changelog-whitelist.yml"

DESTRUCTIVE_KEYWORDS="dropTable|dropColumn|delete|update|truncateTable"

set -e

check_destructive_queries() {
    local file=$1
    # Check the exit status of the yq command explicitly because 'set -e' does not cause the script to exit on failure within an 'if' statement or a pipeline.
    if ! output=$(yq eval ".databaseChangeLog[].changeSet.changes[] | to_entries[] | select(.key | test(\"$DESTRUCTIVE_KEYWORDS\")) | .key" "$file"); then
        printf "${color_red}Error occurred while checking file: $file${color_default}\n"
        return 1
    fi
    if [[ ! -z "$output" ]]; then
        printf "${color_red}Destructive query found in $file:\n"
        printf "${color_red}%s${color_default}\n" "$output"
        return 1
    fi
    return 0
}

for file in "$CHANGELOG_DIR"/*.yaml; do
    relative_path="changelog/${file#$CHANGELOG_DIR/}"

    if ! whitelist_output=$(yq eval ".whitelisted_files[] | select(.file == \"$relative_path\")" "$WHITELIST_FILE"); then
        printf "${color_red}Error occurred while checking the whitelist for file: $file${color_default}\n"
        exit 1
    fi

    if echo "$whitelist_output" | grep -q "$relative_path"; then
        explanation=$(yq eval ".whitelisted_files[] | select(.file == \"$relative_path\") | .explanation" "$WHITELIST_FILE")
        printf "Skipping whitelisted file: $relative_path:\n - Explanation:${color_yellow} $explanation${color_default}\n"
        continue
    fi

    if ! check_destructive_queries "$file"; then
        exit 1
    fi
done


printf "${color_green}No destructive queries found in any changelog file.${color_default}\n"
exit 0
