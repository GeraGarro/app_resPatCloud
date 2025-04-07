# Etapa de construcción
FROM node:18 AS build

WORKDIR /app

# 1. Copia solo los archivos necesarios para instalar dependencias
COPY package.json package-lock.json ./

# 2. Instala Angular CLI como dependencia de desarrollo
RUN npm install -g @angular/cli && \
    npm ci --omit=dev

# 3. Copia el resto de los archivos
COPY . .

# 4. Ejecuta el build (ahora ng estará disponible)
RUN ng build --configuration production

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