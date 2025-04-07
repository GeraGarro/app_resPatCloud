# Etapa de construcción
FROM node:18 AS build

WORKDIR /app

# 1. Copia archivos de dependencias primero
COPY package.json package-lock.json ./

# 2. Instala todas las dependencias (incluyendo devDependencies para el build)
RUN npm install

# 3. Instala Angular CLI globalmente (opcional pero recomendado)
RUN npm install -g @angular/cli

# 4. Copia el resto de los archivos
COPY . .

# 5. Ejecuta el build de producción
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