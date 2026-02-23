# Etapa build
FROM node:20.19.0-alpine AS build

WORKDIR /app

# Copiar solo package primero (mejor cache)
COPY package*.json ./

RUN npm install

# Copiar resto del código
COPY . .

# Vite necesita las variables en build time (se embedan en el JS)
ARG VITE_API_BASE_URL=http://localhost:8080/tenpo/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# Etapa producción (nginx)
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]