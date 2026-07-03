FROM node:20-alpine

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=8082

COPY package.json ./
COPY src ./src

USER node

EXPOSE 8082

CMD ["node", "src/server.js"]
