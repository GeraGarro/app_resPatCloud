# Etapa de construcción
FROM node:18 AS build

WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY . .
RUN npm run build --prod

# Etapa de producción
FROM nginx:alpine

# Elimina la configuración default de Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copia los archivos construidos
COPY --from=build /app/dist/frontend-admin-residuos /usr/share/nginx/html

# Copia nuestra configuración personalizada
COPY nginx.conf /etc/nginx/conf.d/

# Expone el puerto y arranca Nginx
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]