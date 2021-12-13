FROM node:16-alpine as build

# install dependencies
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# Copy all local files into the image.
COPY tsconfig.json index.html vite.config.ts ./
COPY public ./public
COPY src ./src

RUN npm run build


FROM node:16-alpine AS server

WORKDIR /root
COPY server/package*.json ./server/
RUN npm ci --prefix server

COPY server/server.js ./server/
COPY --from=build app/dist ./dist

EXPOSE 3000

CMD ["node", "./server/server.js"]