#!/bin/sh
# Replace BACKEND_PLACEHOLDER with the actual backend URL from env
BACKEND_URL=${BACKEND_URL:-http://localhost:8080}

sed "s|BACKEND_PLACEHOLDER|${BACKEND_URL}|g" /etc/nginx/conf.d/nginx.render.conf > /etc/nginx/conf.d/default.conf

echo "Nginx configured with backend: $BACKEND_URL"

# Validate nginx config
nginx -t

# Start nginx
exec nginx -g "daemon off;"
