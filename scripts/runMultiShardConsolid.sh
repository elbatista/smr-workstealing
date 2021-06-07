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

shards=(2)
sizes=(1000 10000)
threads=(16)
writesList=(5)
stealSizes=(50)
clientProcessesList=(50)
globals=(1)
skewed=('false')
distrExpo='true'
duration=60
warmup=20

for global in "${globals[@]}" ; do
for clientProcesses in "${clientProcessesList[@]}" ; do
for skew in "${skewed[@]}" ; do
 for shard in "${shards[@]}" ; do
  for writes in "${writesList[@]}" ; do
   for t in "${threads[@]}" ; do	
    for s in "${sizes[@]}" ; do
     for stealSize in "${stealSizes[@]}"; do
	#* ./p_smartrun.sh bftsmart.util.ConsolidateFiles '4-8-10000-true-3-true-2-50-false-false-30-0'
	#* params: 'shards-threads-size-skewed-replicas-generateReport-execDuration-clients-copyFiles-generateConsolidatedFiles-warmup-conflictPercentage'
	./p_smartrun.sh bftsmart.util.ConsolidateFiles "${shard}-${t}-${s}-${skew}-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-${global}"
     done;
    done;
   done;
  done;
 done;
done;
done;
done;

echo 'Results consolidated...'