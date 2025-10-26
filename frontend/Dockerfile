# Faza 1: Build React aplikacije
FROM node:25-alpine AS build-stage
# Ažuriraj pakete OS-a
RUN apk update && apk upgrade --no-cache 

WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Faza 2: Posluživanje pomoću Nginx-a
FROM nginx:1.29.2-alpine
# Ažuriraj pakete OS-a
RUN apk update && apk upgrade --no-cache

COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]


