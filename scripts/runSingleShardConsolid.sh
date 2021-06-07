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

sizes=(1000 10000)
threads=(2 4 8 16 32 40)
writesList=(30)
stealSizes=(50)
clientProcessesList=(50)
skewed=('false')
distrExpo='true'
duration=20
warmup=10

for clientProcesses in "${clientProcessesList[@]}" ; do
 for skew in "${skewed[@]}" ; do
  for writes in "${writesList[@]}" ; do
   for t in "${threads[@]}" ; do	
    for s in "${sizes[@]}" ; do
     for stealSize in "${stealSizes[@]}"; do
	./p_smartrun.sh bftsmart.util.ConsolidateFiles "1-${t}-${s}-${skew}-3-false-${duration}-${clientProcesses}-false-true-${warmup}-${writes}-${stealSize}-${distrExpo}-0-true"
     done;
    done;
   done;
  done;
 done;
done;

