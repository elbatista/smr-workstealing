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

sleep 60m
sleep 60m
sleep 60m
sleep 60m
sleep 60m

ssh  elia@node92 "pkill -f 'java.*bft-smart*'"
echo 'node92 killed'
ssh  elia@node91 "pkill -f 'java.*bft-smart*'"
echo 'node91 killed'
ssh  elia@node90 "pkill -f 'java.*bft-smart*'"
echo 'node90 killed'
ssh  elia@node11  "pkill -f 'java.*bft-smart*'"
echo 'node11 killed'
ssh  elia@node12  "pkill -f 'java.*bft-smart*'"
echo 'node12 killed'
ssh  elia@node13  "pkill -f 'java.*bft-smart*'"
echo 'node13 killed'
ssh  elia@node14  "pkill -f 'java.*bft-smart*'"
echo 'node14 killed'

reboot

