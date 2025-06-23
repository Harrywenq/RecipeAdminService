FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build --prod

FROM nginx:alpine
COPY --from=build /app/dist/food-angular /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
