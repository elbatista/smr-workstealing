
sizes=(100000)
threads=(8)
writesList=(15 30)
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

# faz backup dos resultados consolidados anteriores
#backup_dir=$(date +'%Y-%m-%d-%H-%M-%S')
#mv results "results_${backup_dir}"

./scripts/killAll.sh

# exclui resultados parciais remotos
./scripts/clearResults.sh

# EXECUTA EXPERIMENTOS
for clientProcesses in "${clientProcessesList[@]}" ; do
 for s in "${sizes[@]}" ; do
  runSeq=1
  for writes in "${writesList[@]}" ; do
    for t in "${threads[@]}" ; do
	
	if [ $runSeq -eq 1 ] ; then
	  # executa experimento seq:
	  ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${server} '0-0-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	  ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${server} '1-0-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	  ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${server} '2-0-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	  ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 0 ${s} ${writes} ${distrExpo}" & sleep 3s
	  ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 5001 ${s} ${writes} ${distrExpo}" & sleep 3s
	  ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 6001 ${s} ${writes} ${distrExpo}" & sleep 3s
	  ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 7001 ${s} ${writes} ${distrExpo}" &
	  sleep "$((duration+warmup+40))s"
	  ./scripts/killAll.sh
	  sleep 1s
	fi
	runSeq=0
	
	# executa experimento cbase:
	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${server} '0-${t}-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${server} '1-${t}-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${server} '2-${t}-${s}-true-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 0 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 5001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 6001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 7001 ${s} ${writes} ${distrExpo}" &
	sleep "$((duration+warmup+40))s"
	./scripts/killAll.sh
	sleep 1s
	
	# executa experimento early:
	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${server} '0-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${server} '1-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${server} '2-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 0 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 5001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 6001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 7001 ${s} ${writes} ${distrExpo}" &
	sleep "$((duration+warmup+40))s"
	./scripts/killAll.sh
	sleep 1s
	
	# executa experimento early busy wait:
	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${server} '0-${t}-${s}-false-true-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${server} '1-${t}-${s}-false-true-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${server} '2-${t}-${s}-false-true-false-none-0-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 0 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 5001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 6001 ${s} ${writes} ${distrExpo}" & sleep 3s
	ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 7001 ${s} ${writes} ${distrExpo}" &
	sleep "$((duration+warmup+40))s"
	./scripts/killAll.sh
	sleep 1s
	
	for stealSize in "${stealSizes[@]}"; do
	#   for stealerType in "${stealerTypes[@]}" ; do
	# 	# executa experimento work stealing:
	# 	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${server} '0-${t}-${s}-false-true-true-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	# 	ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${server} '1-${t}-${s}-false-true-true-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	# 	ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${server} '2-${t}-${s}-false-true-true-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}-0'" & sleep 10s
	# 	ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 0 ${s} ${writes} ${distrExpo}" & sleep 3s
	# 	ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 5001 ${s} ${writes} ${distrExpo}" & sleep 3s
	# 	ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 6001 ${s} ${writes} ${distrExpo}" & sleep 3s
	# 	ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${client} ${clientProcesses} 7001 ${s} ${writes} ${distrExpo}" &
	# 	sleep "$((duration+warmup+40))s"
	# 	./scripts/killAll.sh
	# 	sleep 1s
	#   done;
	  ssh  elia@node90 "cd workstealing; ./p_smartrun.sh bftsmart.util.ConsolidateFiles '1-${t}-${s}-false-3-false-120-${clientProcesses}-true-false-60-${writes}-${stealSize}-${distrExpo}-0'"			
	done;
   done;
  done;
 done;
done;

# busca os resultados
./scripts/getResults.sh

# consolida os resultados
# for clientProcesses in "${clientProcessesList[@]}" ; do
#   for writes in "${writesList[@]}" ; do
#    for t in "${threads[@]}" ; do	
#     for s in "${sizes[@]}" ; do
#      for stealSize in "${stealSizes[@]}"; do
# 		./p_smartrun.sh bftsmart.util.ConsolidateFiles "1-${t}-${s}-false-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-0-true"
#      done;
#     done;
#   done;
#  done;
# done;

# echo 'Results consolidated...'

rm results*.txt steal*.txt
