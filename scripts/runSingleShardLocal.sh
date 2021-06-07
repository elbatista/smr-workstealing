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

threads=(1 2 4 6)
sizes=(1000 10000)
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
			# executa experimento cbase:
			./p_smartrun.sh demo.list.${ListServer} 0 ${t} ${s} true ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 1 ${t} ${s} true ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 2 ${t} ${s} true ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 0 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 1001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 5001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 6001 1000000 0 ${s} true 50 ${cp} &

			sleep 3m
			sleep 10s

			pkill -f 'java.*bft-smart*'
			sleep 2s

			# executa experimento early:
			./p_smartrun.sh demo.list.${ListServer} 0 ${t} ${s} ${late} ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 1 ${t} ${s} ${late} ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 2 ${t} ${s} ${late} ${graph} false false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 0 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 1001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 5001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 6001 1000000 0 ${s} true 50 ${cp} &

			sleep 3m
			sleep 10s

			pkill -f 'java.*bft-smart*'
			sleep 2s
			# executa experimento early busy wait:
			./p_smartrun.sh demo.list.${ListServer} 0 ${t} ${s} ${late} ${graph} true false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 1 ${t} ${s} ${late} ${graph} true false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 2 ${t} ${s} ${late} ${graph} true false &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 0 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 1001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 5001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 6001 1000000 0 ${s} true 50 ${cp} &

			sleep 3m
			sleep 10s

			pkill -f 'java.*bft-smart*'
			sleep 2s
			# executa experimento work stealing:
			./p_smartrun.sh demo.list.${ListServer} 0 ${t} ${s} ${late} ${graph} true true &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 1 ${t} ${s} ${late} ${graph} true true &
			sleep 3s
			./p_smartrun.sh demo.list.${ListServer} 2 ${t} ${s} ${late} ${graph} true true &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 0 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 1001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 5001 1000000 0 ${s} true 50 ${cp} &
			sleep 3s
			./p_smartrun.sh demo.list.${ListClient} ${clients} 6001 1000000 0 ${s} true 50 ${cp} &

			sleep 3m
			sleep 10s

			pkill -f 'java.*bft-smart*'
			sleep 2s
			#* ./p_smartrun.sh bftsmart.util.ConsolidateFiles '4-8-10000-true-3-true-2-50-false-false-30-0'
			#* params: 'shards-threads-size-skewed-replicas-generateReport-execDuration-clients-copyFiles-generateConsolidatedFiles-warmup-conflictPercentage'
			./p_smartrun.sh bftsmart.util.ConsolidateFiles "1-${t}-${s}-false-3-false-120-${clients}-true-false-60-${cp}"
			echo 'files copied'
			pkill -f 'java.*bft-smart*'
		sleep 2s	
		done;
	done;
done;
