
shards=(2 4 8)
sizes=(1000)
threads=(32)
writesList=(5 25 50)
stealSizes=(50)
clientProcessesList=(50)
stealerTypes=('smartOptStealer')
globals=(1)
clientServers=4
skewed='true'
distrExpo='true'
duration=30
warmup=10
runSeq=1
application='List' # 'Map'

# faz backup dos resultados consolidados anteriores
#backup_dir=$(date +'%Y-%m-%d-%H-%M-%S')
#mv results "results_${backup_dir}"

#./scripts/killAll.sh

# exclui resultados parciais remotos
./scripts/clearResults.sh

# EXECUTA EXPERIMENTOS
for global in "${globals[@]}" ; do
 for clientProcesses in "${clientProcessesList[@]}" ; do
  for shard in "${shards[@]}" ; do
   for s in "${sizes[@]}" ; do

    runSeq=1

    for writes in "${writesList[@]}" ; do
     for t in "${threads[@]}" ; do

		# executa experimento seq:
		#if [ $runSeq -eq 1 ] ; then
			ssh elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '0-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			ssh elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '1-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			ssh elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '2-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			ssh elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 2s
			ssh elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 2s
			ssh elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 2s
			ssh elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & 
			sleep "$((duration+warmup+10))s"
			./scripts/killAll.sh
			sleep 1s
		#fi

		#runSeq=0
	
		# executa experimento cbase:
		#ssh elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '0-${shard}-${t}-${s}-true-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '1-${shard}-${t}-${s}-true-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '2-${shard}-${t}-${s}-true-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-cabse'" & sleep 2s
		#ssh elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-cabse'" & sleep 2s
		#ssh elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-cabse'" & sleep 2s
		#ssh elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-cabse'" & 
		#sleep "$((duration+warmup+10))s"
		#./scripts/killAll.sh
		#sleep 1s
		#
		## executa experimento early:
		#ssh elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '0-${shard}-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '1-${shard}-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '2-${shard}-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-early'" & sleep 2s
		#ssh elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-early'" & sleep 2s
		#ssh elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-early'" & sleep 2s
		#ssh elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-early'" & 
		#sleep "$((duration+warmup+10))s"
		#./scripts/killAll.sh
		#sleep 1s
		#
		## executa experimento early busy wait:
		#ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '0-${shard}-${t}-${s}-false-true-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '1-${shard}-${t}-${s}-false-true-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '2-${shard}-${t}-${s}-false-true-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 10s
		#ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-bw'" & sleep 2s
		#ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-bw'" & sleep 2s
		#ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-bw'" & sleep 2s
		#ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-bw'" & 
		#sleep "$((duration+warmup+10))s"
		#./scripts/killAll.sh
		#sleep 1s
		
		for stealSize in "${stealSizes[@]}"; do
		 #for stealerType in "${stealerTypes[@]}" ; do
			# executa experimento work stealing:
			#ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '0-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			#ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '1-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			#ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${application}ServerMP '2-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}'" & sleep 10s
			#ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-ws'" & sleep 2s
			#ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-ws'" & sleep 2s
			#ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-ws'" & sleep 2s
			#ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.${application}ClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-ws'" & 
			#sleep "$((duration+warmup+10))s"
			#./scripts/killAll.sh
			#sleep 1s
		 #done;
		 ssh  elia@node90 "cd workstealing; ./p_smartrun.sh bftsmart.util.ConsolidateFiles '${shard}-${t}-${s}-${skewed}-3-false-120-${clientProcesses}-true-false-60-${writes}-${stealSize}-${distrExpo}-${global}'"			
		done;
     done;
    done;
   done;
  done;
 done;
done;

# busca os resultados
./scripts/getResults.sh

# consolida os resultados
#./scripts/runMultiShardConsolid.sh
for global in "${globals[@]}" ; do
for clientProcesses in "${clientProcessesList[@]}" ; do
 for shard in "${shards[@]}" ; do
  for writes in "${writesList[@]}" ; do
   for t in "${threads[@]}" ; do	
    for s in "${sizes[@]}" ; do
     for stealSize in "${stealSizes[@]}"; do
		#* ./p_smartrun.sh bftsmart.util.ConsolidateFiles '4-8-10000-true-3-true-2-50-false-false-30-0'
		#* params: 'shards-threads-size-skewed-replicas-generateReport-execDuration-clients-copyFiles-generateConsolidatedFiles-warmup-conflictPercentage'
		#./p_smartrun.sh bftsmart.util.ConsolidateFiles "${shard}-${t}-${s}-${skewed}-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-${global}"
		./p_smartrun.sh bftsmart.util.ConsolidateFilesPerShard "${shard}-${t}-${s}-${skewed}-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-${global}"
     done;
    done;
   done;
  done;
 done;
done;
done;

echo 'Results consolidated...'

rm results*.txt
