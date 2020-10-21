# Credible

블록 체인이란? 분산 컴퓨팅 기술 기반의 데이터 위변조 방지 기술
네트워크에 참여하는 모든 사용자가 모든 거래 내역 등의 데이터를 분산, 저장한다.

블록체인이 작동하기 위해서는 다수의 거래내역을 묶어 블록을 구성하고, 이 블록을 기존 블록체인의 끝에 연결하며, 다수의 컴퓨터에 피투피(P2P) 방식으로 분산 저장해야 한다.

문제점
블록체인이 작동하기 위해선 맨 처음의 거래 내역부터 현재의 거래 내역까지 모든 내역이 모든 사용자(peer)의 컴퓨터에 저장되고 검증되어야 하여 모바일 기기의 성능으로 사용에는 제한이 있다.

해결 방법
1. 블록체인의 느린 속도와 확장성 문제를 해결하기 위해 일부 기능을 개선한 새로운 알고리즘이 출현하고 있다. 라이트닝 네트워크(lightning network)는 기존 블록체인의 느린 속도를 해결하고 번개처럼 빠른 속도를 구현하기 위해, 개별 거래를 별도의 채널에서 처리한 후 그 결과만 블록체인에 기록하는 방식으로 작동하는 알고리즘이다. 이와 유사한 방식으로 플라즈마(plasma) 알고리즘이 개발되었다. 모든 거래내역을 메인체인(main chain)에서 처리하지 않고 별도의 차일드체인(child chain)에서 처리한 후 결과만 메인체인에 전달하는 방식으로 작동하는 알고리즘이다. 플라즈마를 개량하여 플라즈마캐시(plasma cash) 알고리즘이 등장했다. 기존처럼 모든 사용자가 모든 블록을 다운로드해서 검증하지 않고, 개별 사용자가 관심을 가진 특정 코인이 포함된 블록만 추적함으로써 처리 속도를 향상시킨 알고리즘이다. 이런 새로운 알고리즘의 등장으로 기존 블록체인의 느린 속도 문제를 개선하고 좀 더 빠른 속도를 기대할 수 있게 되었다.

2. 비트코인 네트워크는 다양한 노드(6종류)가 서로 네트워크로 연결되어 운영됩니다.
블록의 Full Size는 현재 200G 정도가 됩니다. 하지만 모든 개별사용자나 노드가 이것을 보유해야 할 필요는 없습니다. 이 대용량을 저장해야 하는 노드는 Bitcoin Core, Full block chain node, Solo miner들입니다. 즉 전문적으로 블록체인 네트워크를 운영하는 노드나 Mining을 해서 비트코인을 보상받으려는 노드들에게 가장 최신의 블록체인 복사본 200G의 데이터가 필요하고 노드들이 보유한 HDD에 이 200G가 저장됩니다.

하지만 일반적으로 지갑기능을 사용하는 사용자나 중계역할을 해주는 노드들은 이 200G의 Full block이 필요하지 않기때문에 local 저장소에 저장하지 않습니다. 노드의 목적에 따라 블록의 요약정보만 가지고 있고 세세한 조회가 필요한 경우 위에서 언급한 Full size를 가진 노드들에게 물어보고 답변을 받는 형식을 취합니다.

3. BigchainDB
블록체인을 이용한 데이터베이스
블록체인 기술을 통해 데이터베이스를 만드는 실험이 진행 중에 있으며 BigchainDB가 그러한 실험을 진행하는 최초의 회사이다. 이 데이터베이스 개발자들은 엔터프라이즈 등급의 분산형 데이터베이스 위에 자신들이 개발한 기술을 얹고 분산형, 불역성(immutability), 자산 등록 및 이전 능력이라는 블록체인의 3개 핵심 특징을 추가했다. 이들의 창조물이 얼마나 유용할지는 아직 입증되지 않았다.
https://www.bigchaindb.com/

[블록체인 - 해시넷]
http://wiki.hash.kr/index.php/%EB%B8%94%EB%A1%9D%EC%B2%B4%EC%9D%B8

[]
https://www.a-ha.io/questions/49d2b7ca35fb9525977a7d8ff4666099

- 결론 -
블록체인이란 다수의 거래내역을 묶어 블록을 구성하고, 해시를 이용하여 여러 블록들을 체인처럼 연결한 뒤, 다수의 사람들이 복사하여 분산 저장하는 알고리즘이다. 블록체인의 특징은 신뢰성, 안전성, 탈중앙화 세 가지를 들 수 있다. 블록체인에 기록된 내용은 해시함수에 의해 암호화된 형태로 저장되어 위변조가 불가능하다. 또한 거래내역을 네트워크에 연결된 참여자들이 P2P방식으로 분산 저장하여 검증하므로 조작되지 않은 자료임을 인증할 수 있다.
Credible 앱은 블록체인을 이용하여 조작되지 않은 미디어 (사진, 동영상, 음성)를 생성하고 열람, 재생을 할 수 있다. 이러한 동작은 다음 과정을 따라 수행된다.
1. Credible 앱에서 미디어를 생성합니다.
2. 단말이 네트워크에 연결되어 있다면 즉시 블록체인에 거래 내역을 생성하고 미디어를 포함시켜 등록합니다.
3. 블록체인에 포함되었다면 검증된 미디어가 되어 Credible 앱에서 열람할 수 있습니다.
블록체인을 모바일 기기에서 사용하는데 있어 가장 큰 문제점은 성능, 용량의 문제 입니다. 블록체인은 모든 거래 내역을 모든 기기에 젖아하여 검증하므로 모든 미디어를 기기에 저장해야 한다. 이를 해결할 수 있는 3 가지 방법이 있다.

1. 블록체인의 느린 속도와 확장성 문제를 해결하기 위해 일부 기능을 개선한 새로운 알고리즘이 출현하고 있다. 라이트닝 네트워크(lightning network)는 기존 블록체인의 느린 속도를 해결하고 번개처럼 빠른 속도를 구현하기 위해, 개별 거래를 별도의 채널에서 처리한 후 그 결과만 블록체인에 기록하는 방식으로 작동하는 알고리즘이다. 이와 유사한 방식으로 플라즈마(plasma) 알고리즘이 개발되었다. 모든 거래내역을 메인체인(main chain)에서 처리하지 않고 별도의 차일드체인(child chain)에서 처리한 후 결과만 메인체인에 전달하는 방식으로 작동하는 알고리즘이다. 플라즈마를 개량하여 플라즈마캐시(plasma cash) 알고리즘이 등장했다. 기존처럼 모든 사용자가 모든 블록을 다운로드해서 검증하지 않고, 개별 사용자가 관심을 가진 특정 코인이 포함된 블록만 추적함으로써 처리 속도를 향상시킨 알고리즘이다. 이런 알고리즘을 사용할 수 있다.

2. 사용자가 저장하고 열람하는 등 필요한 노드의 정보만 저장하여 사용하고 나머지 정보는 저장하지 않아 최적화 하는 방법이 있다.

3. BigchainDB 이용하기
BigchainDB는 블록체인을 이용한 데이터베이스다. 앱에서 블록체인에 직접 접근하는것이 아닌 BigchainDB에 접근하여 미디어를 생성하고 등록하고 열람하는 것이다.