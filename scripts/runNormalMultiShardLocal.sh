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
sizes=(100)
threads=(16)
writesList=(1)
stealSizes=(1000)
clientProcessesList=(3)
stealerTypes=('smartOptStealer')
globals=(0)
clientServers=4
skewed='false'
distrExpo='true'
duration=20
warmup=10

for global in "${globals[@]}" ; do
for clientProcesses in "${clientProcessesList[@]}" ; do
 for shard in "${shards[@]}" ; do
  for s in "${sizes[@]}" ; do
   for writes in "${writesList[@]}" ; do
    for t in "${threads[@]}" ; do
	
	# executa experimento early:
	./p_smartrun.sh demo.list.MapServerMP "0-$shard-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}" & sleep 3s
	./p_smartrun.sh demo.list.MapServerMP "1-${shard}-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}" & sleep 3s
	./p_smartrun.sh demo.list.MapServerMP "2-${shard}-${t}-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}" & sleep 3s
	
	./p_smartrun.sh demo.list.MapClientMOMP "${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-early" & sleep 3s
	
	sleep "$((duration+warmup+8))s"
	
	pkill -f 'java.*bft-smart*'

	sleep 1s
		
	for stealSize in "${stealSizes[@]}"; do
	  for stealerType in "${stealerTypes[@]}" ; do
		# executa experimento work stealing:
		./p_smartrun.sh demo.list.MapServerMP "0-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}" & sleep 3s
		./p_smartrun.sh demo.list.MapServerMP "1-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}" & sleep 3s
		./p_smartrun.sh demo.list.MapServerMP "2-${shard}-${t}-${s}-false-true-true-${skewed}-${stealerType}-${stealSize}-${distrExpo}-${duration}-${warmup}" & sleep 3s
		
		./p_smartrun.sh demo.list.MapClientMOMP "${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-ws" & sleep 3s
		
		sleep "$((duration+warmup+8))s"
		
		pkill -f 'java.*bft-smart*'

		sleep 1s
	  done;

	done;
    done;
   done;
  done;
 done;
done;
done;
