# Usa una imagen de Node.js para construir la app
FROM node:18 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos del proyecto
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build --prod

# Usa Nginx para servir el frontend
FROM nginx:alpine

# Copia los archivos de la build al servidor de Nginx
COPY --from=build /app/dist/frontend-admin-residuos /usr/share/nginx/html

#nuevo
COPY nginx.conf /etc/nginx/conf.d/default.conf 

# Expone el puerto para Nginx
EXPOSE 80

# Inicia el servidor Nginx
CMD ["nginx", "-g", "daemon off;"]