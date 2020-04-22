
git pull
cd static || exit
docker stop castlesanddragons-static
docker rm castlesanddragons-static
docker build -t castlesanddragons-static .
docker run --name castlesanddragons-static -d -p 80:80 castlesanddragons-static

cd ../server || exit
docker stop castlesanddragons-server
docker rm castlesanddragons-server
docker build -t castlesanddragons-server .
docker run --name castlesanddragons-server -d -p 5678:5678 castlesanddragons-server
