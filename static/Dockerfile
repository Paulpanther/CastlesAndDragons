ARG PORT=80
FROM node:10 as BUILD
ARG SERVER=localhost:5678

WORKDIR /usr/src/app
COPY . /usr/src/app
RUN npm install
RUN npm run build

FROM nginx
COPY --from=BUILD /usr/src/app/dist  /usr/share/nginx/html
EXPOSE ${PORT}
