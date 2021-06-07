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

ssh  elia@node90  "pkill -f 'java.*bft-smart*'"
echo 'node90 killed'
ssh  elia@node91  "pkill -f 'java.*bft-smart*'"
echo 'node91 killed'
ssh  elia@node92  "pkill -f 'java.*bft-smart*'"
echo 'node92 killed'
ssh  elia@node1 "pkill -f 'java.*bft-smart*'"
echo 'node1 killed'
ssh  elia@node2 "pkill -f 'java.*bft-smart*'"
echo 'node2 killed'
ssh  elia@node3 "pkill -f 'java.*bft-smart*'"
echo 'node3 killed'
ssh  elia@node4 "pkill -f 'java.*bft-smart*'"
echo 'node4 killed'
# ssh  elia@node5 "pkill -f 'java.*bft-smart*'"
# echo 'node5 killed'
