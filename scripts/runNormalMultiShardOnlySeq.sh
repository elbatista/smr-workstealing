# Copyright (c) 2018-2019 Eliã Batista
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.

shards=(2 4 8)
sizes=(1000)
threads=(32)
writesList=(5 25 50)
stealSizes=(50)
clientProcessesList=(50)
stealerTypes=('smartOptStealer')
globals=(1 15)
clientServers=4
skeweds=('true' 'false')
distrExpo='true'
duration=20
warmup=10
runSeq=1

for skewed in "${skeweds[@]}" ; do
for global in "${globals[@]}" ; do
for clientProcesses in "${clientProcessesList[@]}" ; do
 for shard in "${shards[@]}" ; do
  for s in "${sizes[@]}" ; do
   for writes in "${writesList[@]}" ; do
    for t in "${threads[@]}" ; do

	# executa experimento seq:
	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh demo.list.ListServerMP '0-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 3s
	ssh  elia@node91 "cd workstealing; ./p_smartrun.sh demo.list.ListServerMP '1-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 3s
	ssh  elia@node92 "cd workstealing; ./p_smartrun.sh demo.list.ListServerMP '2-${shard}-0-${s}-false-false-false-${skewed}-none-0-${distrExpo}-${duration}-${warmup}'" & sleep 3s
	ssh  elia@node1  "cd workstealing; ./p_smartrun.sh demo.list.ListClientMOMP '${clientProcesses}-0-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 3s
	ssh  elia@node2  "cd workstealing; ./p_smartrun.sh demo.list.ListClientMOMP '${clientProcesses}-1001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 3s
	ssh  elia@node3  "cd workstealing; ./p_smartrun.sh demo.list.ListClientMOMP '${clientProcesses}-5001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & sleep 3s
	ssh  elia@node4  "cd workstealing; ./p_smartrun.sh demo.list.ListClientMOMP '${clientProcesses}-6001-${s}-${shard}-${skewed}-${writes}-${global}-${distrExpo}-${clientServers}-seq'" & 
	sleep "$((duration+warmup+10))s"
	./scripts/killAll.sh
	sleep 3s

	ssh  elia@node90 "cd workstealing; ./p_smartrun.sh bftsmart.util.ConsolidateFiles '${shard}-${t}-${s}-${skewed}-3-false-120-${clientProcesses}-true-false-60-${writes}-50-${distrExpo}-${global}'"
	
      done;
    done;
  done;
 done;
done;
done;
done;
