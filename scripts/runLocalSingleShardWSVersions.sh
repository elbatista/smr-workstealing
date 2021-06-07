# Copyright (c) 2018-2019 Eliã Batista
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and limitations under the License.

sizes=(1000)
threads=(2 4 8 16 32 40 56)
writesList=(1 15)
clientProcessesList=(50)
clientServers=4
skewed='false'
distrExpo='true'
duration=20
warmup=10
runSeq=1

for clientProcesses in "${clientProcessesList[@]}" ; do
 for s in "${sizes[@]}" ; do
  runSeq=1
  for writes in "${writesList[@]}" ; do
    for t in "${threads[@]}" ; do

	# executa experimento early:
	./p_smartrun.sh demo.list.ListServer "0-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "1-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "2-${t}-${s}-false-false-false-none-0-${distrExpo}-${duration}-${warmup}-0" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 0 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 5001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 6001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 7001 ${s} ${writes} ${distrExpo} &
	sleep "$((duration+warmup+10))s"
	pkill -f 'java.*bft-smart*'
	sleep 1s

	# executa experimento work stealing v1:
	./p_smartrun.sh demo.list.ListServer "0-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-1" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "1-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-1" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "2-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-1" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 0 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 5001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 6001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 7001 ${s} ${writes} ${distrExpo} &
	sleep "$((duration+warmup+10))s"
	pkill -f 'java.*bft-smart*'
	sleep 1s

	# executa experimento work stealing v2:
	./p_smartrun.sh demo.list.ListServer "0-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-2" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "1-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-2" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "2-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-2" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 0 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 5001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 6001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 7001 ${s} ${writes} ${distrExpo} &
	sleep "$((duration+warmup+10))s"
	pkill -f 'java.*bft-smart*'
	sleep 1s

	# executa experimento work stealing v3:
	./p_smartrun.sh demo.list.ListServer "0-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-3" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "1-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-3" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "2-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-3" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 0 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 5001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 6001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 7001 ${s} ${writes} ${distrExpo} &
	sleep "$((duration+warmup+10))s"
	pkill -f 'java.*bft-smart*'
	sleep 1s

	# executa experimento work stealing v4:
	./p_smartrun.sh demo.list.ListServer "0-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-4" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "1-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-4" & sleep 3s
	./p_smartrun.sh demo.list.ListServer "2-${t}-${s}-false-true-true-none-50-${distrExpo}-${duration}-${warmup}-4" & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 0 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 5001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 6001 ${s} ${writes} ${distrExpo} & sleep 3s
	./p_smartrun.sh demo.list.ListClientMO ${clientProcesses} 7001 ${s} ${writes} ${distrExpo} &
	sleep "$((duration+warmup+10))s"
	pkill -f 'java.*bft-smart*'
	sleep 1s

	./p_smartrun.sh bftsmart.util.ConsolidateFiles "1-${t}-${s}-false-3-false-120-${clientProcesses}-true-false-60-${writes}-50-${distrExpo}-0"

      done;
    done;
  done;
done;

