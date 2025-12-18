# ETAPA 1: Construcción (Build)
FROM node:22-alpine as build-step
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
ENV NODE_OPTIONS=--openssl-legacy-provider

RUN npm run build -- --configuration production
CMD ["tail", "-f", "/dev/null"]

FROM nginx:1.25-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build-step /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]