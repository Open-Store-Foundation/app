#!/bin/bash
set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}=== Kitten Publisher ===${NC}"

# Variables to hold secrets
CENTRAL_TOKEN_USERNAME_VAL=""
CENTRAL_TOKEN_PASSWORD_VAL=""
SIGNING_KEY_ID_VAL=""
SIGNING_PASSWORD_VAL=""
SIGNING_SECRET_KEY_RING_FILE_VAL=""

# Helper to get value
get_config() {
    local var_name=$1
    local prompt_text=$2
    local is_secret=$3
    local existing_val="${!var_name}"
    
    if [ -n "$existing_val" ]; then
        echo -e "${YELLOW}$var_name${NC} found in environment."
        eval "${var_name}_VAL=\"\$existing_val\""
    else
        if [ "$is_secret" = "true" ]; then
            read -s -p "$prompt_text: " input_val
        else
            read -p "$prompt_text: " input_val
        fi
        echo ""
        eval "${var_name}_VAL=\"\$input_val\""
    fi
}

# 1. Credentials
get_config "CENTRAL_TOKEN_USERNAME" "Enter Sonatype Username" "false"
get_config "CENTRAL_TOKEN_PASSWORD" "Enter Sonatype Password" "true"
get_config "SIGNING_KEY_ID" "Enter Signing Key ID" "false"
get_config "SIGNING_PASSWORD" "Enter Signing Password" "true"
get_config "SIGNING_SECRET_KEY_RING_FILE" "Enter Secret Key Ring File Path" "false"

# 3. Version
# 3. Version
CENTRAL_VERSION_VAL=$1

if [ -z "$CENTRAL_VERSION_VAL" ]; then
    if [ -n "$CENTRAL_VERSION" ]; then
        echo -e "${YELLOW}CENTRAL_VERSION${NC} found in environment."
        CENTRAL_VERSION_VAL="$CENTRAL_VERSION"
    else
        read -p "Enter Version to publish (e.g. 1.0.0): " CENTRAL_VERSION_VAL
    fi
fi

if [ -z "$CENTRAL_VERSION_VAL" ]; then
    echo -e "${RED}Version is required.${NC}"
    exit 1
fi

# Target Modules
MODULES=(
    ":lib:kitten:api"
    ":lib:kitten:core"
    ":lib:kitten:test-core"
    ":lib:kitten:test-graph"
    ":lib:kitten:viewmodel"
)

# Construct Task List
TASKS=""
for mod in "${MODULES[@]}"; do
    TASKS="$TASKS $mod:publishToMavenCentral"
done

echo -e "Preparing to publish version: ${GREEN}$CENTRAL_VERSION_VAL${NC}"
echo -e "Modules: $TASKS"

read -p "Ready to publish? (y/N) " confirm
if [[ $confirm != [yY] && $confirm != [yY][eE][sS] ]]; then
    echo "Aborted."
    exit 0
fi

echo -e "${YELLOW}Running Gradle Publish...${NC}"

# Execute Gradle with scoped environment variables
# Vanniktech plugin properties
PUBLISHING="1" \
ORG_GRADLE_PROJECT_mavenCentralUsername="$CENTRAL_TOKEN_USERNAME_VAL" \
ORG_GRADLE_PROJECT_mavenCentralPassword="$CENTRAL_TOKEN_PASSWORD_VAL" \
REPO_VERSION="$CENTRAL_VERSION_VAL" \
REPO_PATH="github.com/Open-Store-Foundation/app/tree/main/lib/kitten" \
REPO_NAME="Kitten DI" \
REPO_DESCRIPTION="KMP compile time DI framework without codegen!" \
./gradlew $TASKS \
    -Psigning.keyId="$SIGNING_KEY_ID_VAL" \
    -Psigning.password="$SIGNING_PASSWORD_VAL" \
    -Psigning.secretKeyRingFile="$SIGNING_SECRET_KEY_RING_FILE_VAL" \
    --no-configuration-cache

echo -e "${GREEN}Publishing Complete!${NC}"
