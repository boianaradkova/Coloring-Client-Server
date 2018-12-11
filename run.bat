cd bin

start java.exe -classpath . com.gmail.boianaradkova.GameServer 3379 5 0

start /MIN java.exe -classpath . com.gmail.boianaradkova.RandomClient 3379 localhost
start /MIN java.exe -classpath . com.gmail.boianaradkova.RandomClient 3379 localhost
start /MIN java.exe -classpath . com.gmail.boianaradkova.RandomClient 3379 localhost

cd ..

