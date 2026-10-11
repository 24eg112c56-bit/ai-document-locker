#!/bin/sh
# Replace BACKEND_URL placeholder with the actual backend URL from env
BACKEND_URL=${BACKEND_URL:-http://localhost:8080}

# Use the render nginx config and inject the backend URL
sed "s|BACKEND_URL|${BACKEND_URL}|g" /etc/nginx/conf.d/nginx.render.conf > /etc/nginx/conf.d/default.conf

echo "Nginx configured with backend: $BACKEND_URL"

# Start nginx
exec nginx -g "daemon off;"
