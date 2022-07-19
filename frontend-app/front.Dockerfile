FROM node:16.16.0-slim

RUN mkdir -p /app && chown -R node:node /app

RUN sh -c "npm install npm@8.14.0 --location=global && \
           npm install @angular/cli@13.3.3 --location=global"

WORKDIR /app

COPY --chown=node:node . .

USER node

RUN sh -c "npm install"

CMD ["ng", "serve", "--open", "--host", "0.0.0.0", "--port", "4200", "--disable-host-check"]
