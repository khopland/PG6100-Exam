FROM mhart/alpine-node:12 as build

# install dependencies
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# Copy all local files into the image.
COPY . .

RUN npm run build


FROM mhart/alpine-node:12 AS server

WORKDIR /root
COPY --from=build app/dist ./dist
COPY server/package*.json ./server/
run cd server && npm ci
COPY server/server.js ./server/

EXPOSE 3000

CMD ["node", "./server/server.js"]