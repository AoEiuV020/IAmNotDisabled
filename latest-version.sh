#!/bin/sh
cur="$(dirname $0)"
cd $cur
cat ChangeLog.txt |head -2 |tail -1 |sed  's/\(.*\):/\1/'
