# Etap budowania
FROM node:20-alpine AS build
WORKDIR /app
COPY web-ui/package*.json ./
RUN npm install
COPY web-ui/ ./
RUN npx ng build --configuration production

# Etap uruchamiania (Nginx)
FROM nginx:alpine
COPY --from=build /app/dist/web-ui/browser /usr/share/nginx/html
# Kopiujemy własną konfigurację Nginx, aby obsłużyć Angular Routing
RUN echo 'server { \
    listen 80; \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
