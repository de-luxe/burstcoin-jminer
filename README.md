# burstcoin-jminer
GPU assisted Proof of Capacity (PoC) Miner for Burstcoin (BURST)

1. edit 'jminer.properties' with text editor to configure miner
2. ensure java8 (64bit) and openCL driver/sdk is installed
3. execute 'java -jar -d64 -XX:+UseG1GC burstcoin-jminer-0.4.x-RELEASE.jar' or run the *.bat/*.sh file

> '-d64' to ensure 64bit java (remove for 32bit)
> '-XX:+UseG1GC' to free memory after round finished.


The miner is configured in a text-file named 'jminer.properties'.
This file has to be in the miner directory (same folder as '*.jar' file) 
> To get started, use one of the following examples. 
> The min. required settings for the different mining-modes.

# DevPool-Setup
uses all deadlines below a given target per block to calculate shares.

    devPool=true
    plotPaths=D:/,C:/,E:/plots,F:/plots
    numericAccountId=<YOUR NUMERIC ACCOUNT ID>
    poolServer=http://pool.com:port
    
# Pool-Setup
uses only the best deadline per block to calculate shares.

    plotPaths=D:/,C:/,E:/plots,F:/plots
    numericAccountId=<YOUR NUMERIC ACCOUNT ID>
    poolServer=http://pool.com:port

# Solo-Setup

    plotPaths=D:/,C:/,E:/plots,F:/plots
    poolMining=false
    passPhrase=<YOUR PASS PHRASE>






# List of all properties
your 'jminer.properties' hasn't got to contain all properties listed here,
most of them are optinal or there is a fallback/default value for it.


## Plot-Files

### plotPaths (required)
list of plot paths separated with , e.g. D:/,C:/,E:/plots,F:/plots (in one line)
the miner will treat every path as 'physical' drive and use one thread for it

    plotPaths=D:/,C:/,E:/plots,F:/plots

### scanPathsEveryRound (default:true)  
optional 'true' will check 'plotPaths' for changed plot files on every round 'false' will check only on start/restart
if you are moving/creating plot-files while mining, it could be disabled

    scanPathsEveryRound=false

### listPlotFiles (default:false)
optional ... list all plotFiles on start. If walletServer/soloServer is configured, 
it will show mined blocks and drive seeks/chunks of plotfile, too.

    listPlotFiles=true



## Mining Mode

### poolMining (default:true)
'true' for pool mining, 'false' for solo mining. ensure to configure the chosen mining-mode below.
For solo-mining you need to set

    poolMining=false




## Pool-Mining
Ensure you already setup reward assignment http://localhost:8125/rewardassignment.html

### numericAccountId (required for pool)
first number in all plot-files

    numericAccountId=xxxxxxxxxxxxxxx


### poolServer (required for pool)
format is inclusive protocol and port e.g. 'http://pool.com:8125'

    poolServer=http://pool.com

### walletServer (optional)
define local or online wallet, to receive and show last winner!
if empty, winner feature will be just disabled.
format is inclusive protocol and port e.g. 'http://localhost:8125'
online use e.g. 'https://wallet.burst-team.us:8125'

    walletServer=https://wallet.burst-team.us:8125

### winnerRetriesOnAsync (default:4)
number of retries to get winner from walletServer

    winnerRetriesOnAsync=10

### winnerRetryIntervalInMs (default:500)
time to wait until next retry to get winner from walletServer

    winnerRetryIntervalInMs=250

### devPool (default:false)
'true' for using devPools (V1 or V2), 'false' or empty if not
on using devPool please read NOTICE in description of 'chunkPartNonces'

    devPool=true

### devPoolCommitsPerRound (default:3)
how often the miner tries to commit shares to devPool not needed for other pools.

    devPoolCommitsPerRound=6


>    NOTICE: 'devPool' wants all deadlines below a given target, this miner will only deliver
>            one deadline per chunkPart, so it can happen, that not all shares can be delivered
>            i suggest using lower 'chunkPartNonces' e.g. 160000 ... (1 result per 160000 nonces)
>            play with that on same block to test if you get more shares with lower 'chunkPartNonces'.



## Solo-mining

### soloServer (default:http://localhost:8125)
**WARN!** soloServer should be http://localhost:8125 or http://127.0.0.1:8125
Solo means you send your PASS on commit results!

**DO NOT** try to use a online wallet or pool as Server!

    soloServer=http://127.0.0.1:8125

### passPhrase (required for solo)
secretPhrase/password of solo mining burst-account

    passPhrase=xxxxxxxxxxxxxx

### targetDeadline (optinal)
min. deadline to be committed. 

    targetDeadline=750000

### triggerServer (default: false)
on 'true' miner emulates open wallet gui, to prevent wallet server from
falling asleep (not sure if needed at all)

    triggerServer=true




## OpenCL
The miner uses openCL for most of the mining calculations, ensure it is setup correctly.
Instructions can be found e.g. here (thanks cryo):
https://github.com/bhamon/gpuPlotGenerator/blob/master/README.md
You could also use that instruction to find your platformId and deviceId if needed.
Since version 0.4.4 all available platforms and devices are listed on startup.

### platformId (default:0) 
id of openCL platform on your system. one platform may have multiple
devices, the miner currently uses just one (in general not the bottleneck)

    platformId=0

### deviceId (default:0)
specifies the device used by OCLCecker, can be your first GPU,
in most cases it will not be 100% used. (depends on capacity)

    deviceId=1



## Miner Internals

### refreshInterval (default:2000)
interval of asking wallet/pool for mining info (in ms), to check for new block

    refreshInterval=2000

### connectionTimeout (default:12000)
increase the 'connectionTimeout' on network problems. this timeout is used for all network requests.
if you use pool or online-wallet, the 12 sec. default may cause timeout on committing nonces 
or getting mining info etc.

    connectionTimeout=12000

### debug (default:false)
setting 'debug' to true will log additional information of the mining process,
that are not related to mining, but to miner internals.

    debug=true

### writeLogFile (default:false)
setting 'writeLogFile' to 'true' will write all logs from console to a file, too.
the name of that file can be specified by 'logFilePath'.

    writeLogFile=true

### logFilePath (default:log/jminer.log.txt)
path (filename and optional directory, relative to miner location)

    logFilePath=mylogs/jminier/log.txt


## Miner Appearance

### readProgressPerRound (default:9) 
defines how often the mining progress is shown per round
thats the 'xx% done ...' info.

    readProgressPerRound=16

###  byteUnitDecimal (default:true) 
switch between decimal units (true): TB/GB/MB (divided by 1000),
or binary units (false) TiB/GiB/MiB (divided by 1024) - https://en.wikipedia.org/wiki/Byte

    byteUnitDecimal=false

### showDriveInfo (default:false)
set this to 'true' to show info about every drive on finish reading it,
this is useful to find the slow ones ... can help to optimize your setup.

    showDriveInfo=true

e.g. you see in logs, that 'drive-c' and 'drive-d' slow down this mining setup:

    read 'C:/data/drive-a' (3TB 958GB) in '28s 444ms'
    read 'C:/data/drive-b' (3TB 932GB) in '29s 114ms'
    read 'C:/data/drive-c' (3TB 996GB) in '35s 390ms'
    read 'C:/data/drive-d' (3TB 996GB) in '35s 685ms'




## Miner Memory Usage
 
### chunkPartNonces (default:320000)
staggerSize defines number of nonces per chunk.
the miner will split chunks in smaller pieces called chunkParts.
this makes sense, to save memory and optimize speed.
in the best case chunkPart#1 will be checked before chunkPart#2 is
completely read ... depending on the power of your GPU.
if staggersize is smaller than chunkPartNonces, staggersize will be used.
e.g. play with +/- 160000 steps

    chunkPartNonces=960000 


>    NOTICE: for 'devPool': only one result per chunkPart will be committed,
>            without 'optDevPool' (witch is not implemented, yet)
>            consider using low chunkPartNonces size, to commit more.
>            (guess, there will not be multiple dl below target in small chunkPart)

### readerThreads (default:0)
normally '0' means, the miner takes one thread per drive (plotPath) this is recommend.
choosing a other number of 'readerThreads' can be useful on memory issues.
For example, if you mine on 4 drives (plotPaths), you can reduce the memory usage
by setting 'readerThreads=2', this will reduce mining speed but save memory.

    readerThreads=10





