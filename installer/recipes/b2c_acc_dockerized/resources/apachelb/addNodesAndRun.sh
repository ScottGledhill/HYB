#!/bin/bash


fileToReplace="$1"

balanceMembers="\t"

while IFS='=' read -r key value 
do
    if [[ "$key" == NODE_IP_* ]]; then
        node=$(echo "${key##*_}" | awk '{print tolower($0)}')
        member="BalancerMember ${value} route=${node}"
        balanceMembers="${balanceMembers}${member}\n\t"
    fi
done <<< "$(env)"

awk 'BEGIN {p=1} /# BALANCE_MEMBERS_START/ {print;print "'"$balanceMembers"'";p=0} /# BALANCE_MEMBERS_END/ {p=1} p' "${fileToReplace}" > "${fileToReplace}.tmp" && mv "${fileToReplace}.tmp" "${fileToReplace}"

exec httpd-foreground
