#!/bin/sh
monitor_list() {
echo configmap
}

error_exit() {
  echo "Error, please check above error message!"
  exit 1
}

flag=0
for i in `monitor_list`
do
  checked=`git diff HEAD~1 HEAD | grep $i | wc -l`
  if [ $checked -gt 0 ]; then
    echo "######################################"
    echo Warnning!!!$i has been changed by pr!!
    echo "######################################"
    git diff HEAD~1 HEAD |grep "\- " |grep -v "@"
    git diff HEAD~1 HEAD |grep "\+ " |grep -v "@"
    flag=1
  fi
done

if [ $flag -eq 0 ]; then
  echo "######################################"
  echo Success, no cross team change found
  echo "######################################"
fi
exit $flag
