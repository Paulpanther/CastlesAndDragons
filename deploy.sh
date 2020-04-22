
git pull
cd static
docker stop castelsanddragonbuild
docker rm castelsanddragonbuild
docker build -t castelsanddragonbuild .
docker run castelsanddragonbuild-t castelsanddragonbuild -d -p 80:80

cd ../server
docker stop castelsanddragonsserver
docker rm castelsanddragonsserver
docker build -t castelsanddragonsserver .
docker run castelsanddragonsserver -t castelsanddragonbuild -d -p 5678:5678
