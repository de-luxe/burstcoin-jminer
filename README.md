# burstcoin-jminer
GPU assisted Proof of Capacity (PoC) Miner for Burstcoin (BURST)

1. edit 'jminer.properties' with text editor to configure miner
2. ensure java8 (64bit) and openCL driver/sdk is installed
3. execute 'java -jar -d64 -XX:+UseG1GC burstcoin-jminer-0.4.x-RELEASE.jar' or run the *.bat file
<br>
<br>

QUICKSTART editing 'jminer.properties'... (e.g. make a copy of jminer.default.properties and rename to jminer.properties)

POOL min. required settings:
--------------------------------------------------------------------------------
plotPaths=D:/,C:/,E:/plots,F:/plots<br>
numericAccountId=YOUR NUMERIC ACCOUNT ID<br>
poolServer=http://pool.com:port<br>

DEV-POOL min. required settings:
--------------------------------------------------------------------------------
plotPaths=D:/,C:/,E:/plots,F:/plots<br>
numericAccountId=YOUR NUMERIC ACCOUNT ID<br>
poolServer=http://pool.com:port<br>
devPool=true<br>

SOLO min. required settings 
--------------------------------------------------------------------------------
plotPaths=D:/,C:/,E:/plots,F:/plots<br>
poolMining=false<br>
passPhrase=YOUR PASS PHRASE<br>

# BUILD
burstcoin-jminer can be build with maven, install maven and execute e.g.:<br>
mvn package



