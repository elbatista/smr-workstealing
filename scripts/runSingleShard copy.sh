
sizes=(100000)
threads=(2 4 8 10 12 16 32 40 48 56)
writesList=(1 15 30)
stealSizes=(50)
clientProcessesList=(50)
stealerTypes=('smartOptStealer')
clientServers=4
distrExpo='true'
duration=60
warmup=20
runSeq=1
server='ListServer'
client='ListClientMO'

for clientProcesses in "${clientProcessesList[@]}" ; do
  for writes in "${writesList[@]}" ; do
   for t in "${threads[@]}" ; do	
    for s in "${sizes[@]}" ; do
     for stealSize in "${stealSizes[@]}"; do
		./p_smartrun.sh bftsmart.util.ConsolidateFiles "1-${t}-${s}-false-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-0-true"
     done;
    done;
  done;
 done;
done;

echo 'Results consolidated...'

