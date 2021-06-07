# Copyright (c) 2018-2019 Eliã Batista
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.

shards=(2)
sizes=(1000)
threads=(16)
writesList=(5)
stealSizes=(50)
stealerTypes=('smartOptStealer')
global=0
clients=50
skewed='false'
distrExpo='true'

for shard in "${shards[@]}" ; do
for writes in "${writesList[@]}" ; do
for t in "${threads[@]}" ; do	
for s in "${sizes[@]}" ; do
	# executa experimento early busy wait:
	./p_smartrun.sh demo.list.ListServerMP "0-${shard}-${t}-${s}-false-true-false-${skewed}-none-0" & sleep 3s
	./p_smartrun.sh demo.list.ListServerMP "1-${shard}-${t}-${s}-false-true-false-${skewed}-none-0" & sleep 3s
	./p_smartrun.sh demo.list.ListServerMP "2-${shard}-${t}-${s}-false-true-false-${skewed}-none-0" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 0    ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 1001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 5001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 6001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & 
	sleep 50s 
	pkill -f 'java.*bft-smart*'
	sleep 1s
	for stealSize in "${stealSizes[@]}"; do
	for stealerType in "${stealerTypes[@]}" ; do
		# executa experimento work stealing:
		./p_smartrun.sh demo.list.ListServerMP "0-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}" & sleep 3s
		./p_smartrun.sh demo.list.ListServerMP "1-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}" & sleep 3s
		./p_smartrun.sh demo.list.ListServerMP "2-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}" & sleep 3s
		./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 0    ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
		./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 1001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
		./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 5001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} & sleep 3s
		./p_smartrun.sh demo.list.ListClientMOMPExpDist ${clients} 6001 ${s} ${shard} ${skewed} ${writes} ${global} ${distrExpo} &
		sleep 50s
		pkill -f 'java.*bft-smart*'
		sleep 1s
	done;
	done;
done;
done;
done;
done;

