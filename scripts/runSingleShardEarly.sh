# Copyright (c) 2018-2019 Eliã Batista
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

threads=(1 2 4 6 8 10 12 14)
sizes=(1000)
clients=50
conflictPercent=(0 15)
late='false'
busyWait='true'
ws='true'

graph='coarseLock'
ListServer="ListServer"
#Usage: ... ListServer <processId> <num threads> <initial entries> <late scheduling?> <graph type> <busy wait ?> <workstealing ?>
ListClient="ListClientMO"
#Usage: ... ListClient <num. threads> <process id> <number of requests> <interval> <maxIndex> <parallel?> <operations per request> <conflict percent>

for cp in "${conflictPercent[@]}" ;
do
	for t in "${threads[@]}" ;
	do
		for s in "${sizes[@]}" ;
		do

			# executa experimento early:
			ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.${ListServer} 0 ${t} ${s} ${late} ${graph} false false" &
			sleep 3s
			ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.${ListServer} 1 ${t} ${s} ${late} ${graph} false false" &
			sleep 3s
			ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.${ListServer} 2 ${t} ${s} ${late} ${graph} false false" &
			sleep 3s
			ssh  elia@node11  "cd workstealing; ./p_smartrun.sh demo.list.${ListClient} ${clients} 0 1000000 0 ${s} true 50 ${cp}" &
			sleep 3s
			ssh  elia@node12  "cd workstealing; ./p_smartrun.sh demo.list.${ListClient} ${clients} 1001 1000000 0 ${s} true 50 ${cp}" &
			sleep 3s
			ssh  elia@node13  "cd workstealing; ./p_smartrun.sh demo.list.${ListClient} ${clients} 5001 1000000 0 ${s} true 50 ${cp}" &
			sleep 3s
			ssh  elia@node14  "cd workstealing; ./p_smartrun.sh demo.list.${ListClient} ${clients} 6001 1000000 0 ${s} true 50 ${cp}" &

			sleep 3m
			sleep 10s

			./scripts/killAll.sh
			sleep 2s

			#* ./p_smartrun.sh bftsmart.util.ConsolidateFiles '4-8-10000-true-3-true-2-50-false-false-30-0'
			#* params: 'shards-threads-size-skewed-replicas-generateReport-execDuration-clients-copyFiles-generateConsolidatedFiles-warmup-conflictPercentage'
			ssh  elia@node90 "cd workstealing; ./p_smartrun.sh bftsmart.util.ConsolidateFiles '1-${t}-${s}-false-3-false-120-${clients}-true-false-60-${cp}'"
			ssh  elia@node90 "pkill -f 'java.*bft-smart*'"
			sleep 2s
			echo 'files copied'
			
		done;
	done;
done;
