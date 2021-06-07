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

ssh  elia@node90 "rm -r -f workstealing/dist"
ssh  elia@node90 "rm -r -f workstealing/lib"
ssh  elia@node90 "rm -r -f workstealing/results"
ssh  elia@node90 "rm -r -f workstealing/*.txt"
echo 'lib data cleared'

#ssh  elia@node90 "mkdir workstealing"
#echo 'new workstealing directory created'