#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname ${ABSPATH})
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)
IDLE_PROFILE_URL=http://localhost:${IDLE_PORT}/profile

echo "> Health Check Start!"
echo "> IDLE_PORT: ${IDLE_PORT}"
echo "> curl -s ${IDLE_PROFILE_URL}"
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s ${IDLE_PROFILE_URL})
  UP_COUNT=$(echo ${RESPONSE} | grep 'real' | wc -l)

  echo ">> ${UP_COUNT} 번째 시도중..."

  if [ ${UP_COUNT} -ge 1 ]
  then # $up_count >= 1 ("real" 문자열이 있는지 검증)
    echo "> Health check 성공"
    switch_proxy
    break
  else
    echo "> Health check 의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다"
    echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RESPONSE} -eq 10 ]
  then
    echo "> Health check 실패"
    echo "> nginx:${IDLE_PORT} 에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done
