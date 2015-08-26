# burstcoin-jminer
GPU assisted Proof of Capacity (PoC) Miner for Burstcoin (BURST)

1. edit 'jminer.properties' with text editor to configure miner
2. ensure java8 and openCL driver/sdk is installed
3. execute 'java -jar -XX:+UseG1GC burstcoin-jminer-0.3.6-RELEASE.jar' or run the *.bat file
<br>
<br>

QUICKSTART editing 'jminer.properties'... (make a copy of jmniner.default.properties and rename to jminer.properties)

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
mvn clean dependency:copy-dependencies package



