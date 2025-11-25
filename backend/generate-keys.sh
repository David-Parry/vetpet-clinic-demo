#!/bin/bash

# Script to generate RSA keys for JWT authentication
# This script should be run locally for development only
# NEVER commit the generated keys to Git!

set -e

KEYS_DIR="src/main/resources/keys"

echo "🔐 Generating RSA keys for JWT authentication..."

# Create keys directory if it doesn't exist
mkdir -p "$KEYS_DIR"

# Generate private key (4096-bit RSA)
echo "📝 Generating private key..."
openssl genpkey -out "$KEYS_DIR/private_key.pem" -algorithm RSA -pkeyopt rsa_keygen_bits:4096

# Generate public key from private key
echo "📝 Generating public key..."
openssl rsa -pubout -outform pem -in "$KEYS_DIR/private_key.pem" -out "$KEYS_DIR/public_key.pem"

# Set appropriate permissions
chmod 600 "$KEYS_DIR/private_key.pem"
chmod 644 "$KEYS_DIR/public_key.pem"

echo "✅ Keys generated successfully!"
echo ""
echo "📁 Keys location:"
echo "   Private key: $KEYS_DIR/private_key.pem"
echo "   Public key:  $KEYS_DIR/public_key.pem"
echo ""
echo "⚠️  IMPORTANT:"
echo "   - These keys are for LOCAL DEVELOPMENT ONLY"
echo "   - NEVER commit these keys to Git"
echo "   - For production, use environment variables to load keys from secure storage"
echo "   - See README-SECURITY.md for more information"
echo ""
