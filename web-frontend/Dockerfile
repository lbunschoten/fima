FROM node:alpine

WORKDIR /usr/src/app

COPY package*.json ./
COPY build ./build
COPY config ./config

RUN npm install

RUN npm run build

EXPOSE 8080

CMD ["npm", "start"]
