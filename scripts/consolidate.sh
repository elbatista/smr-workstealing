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

workloads=('0-0-0' '15-0-0')
particoes=(1)
threads=(50)
sizes=(1000)
skews=('false')
n=1
clients=50
warmup=30
interval=60
intervalMinutes=$((interval/60))

for t in "${threads[@]}" ;
do
for p in "${particoes[@]}" ;
do
#t=$((p*2))
	for s in "${sizes[@]}" ;
	do
		for skew in "${skews[@]}" ;
		do
			for w in "${workloads[@]}" ;
			do
				#params: 'w-w-w-shards-threads-size-skewed-experiment-replicas-generateReport-totalExp-execDuration-clients-copyFiles-generateConsolidatedFiles-warmup'
				./p_smartrun.sh bftsmart.util.ConsolidateFiles "$w-$p-$t-$s-$skew-1-3-true-1-${intervalMinutes}-$clients-false-true-${warmup}"
			done;
		done;
	done;
done;
done;

