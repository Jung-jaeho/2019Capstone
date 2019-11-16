"""
pip3 install elasticsearch 설치 필요

공식 도큐먼트  : https://www.elastic.co/guide/en/elasticsearch/reference/7.1/index.html?baymax=KR-ES-getting-started&elektra=landing-page 
AWS 계정 연결하는 방법 : https://blog.ruanbekker.com/blog/2018/08/20/using-iam-authentication-with-amazon-elasticsearch-service/
네이버 엘라스틱서치로 로그 검색 시스템 만들기: https://d2.naver.com/helloworld/273788
ElasticSearch 엔드포인트: https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com
Kibana: https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/_plugin/kibana/


"""


from uuid import getnode as get_mac
from datetime import datetime
from elasticsearch import Elasticsearch

elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"

es = Elasticsearch(elasticsearch_end_point)


"""
******  elasticsearch 정보 확인
"""
print(es.info)


"""
****** elasticsearch 데이터 삽입 

용어 정리

Elasticsearch    mysql(관계형데이터베이스)
------------------------------------
Index            database
Type             table
Doc              row
Column           Field
Schema           Mapping
Index            Everything is indexed
SQL              Query DSL
------------------------------------

"""

# 맥 주소 가져오기
mac_address = get_mac()

# 데이터 json 형태로 만들기
doc = {
    'uuid': mac_address,
    'value': 12.9,
    'timestamp': datetime.now(),
}

res = es.index(index="capstone", doc_type="device_data",  body=doc)

print(res)


"""
****** elasticsearch 데이터 조회(검색)
 
Response되는 key 설명

took – Elasticsearch가 검색을 실행하는 데 걸린 시간(밀리초)
timed_out – 검색의 시간 초과 여부
_shards – 검색한 샤드 수 및 검색에 성공/실패한 샤드 수
hits – 검색 결과
hits.total – 검색 조건과 일치하는 문서의 총 개수
hits.hits – 검색 결과의 실제 배열(기본 설정은 처음 10개 문서)
hits.sort - 결과의 정렬 키(점수 기준 정렬일 경우 표시되지 않음)
hits._score 및 max_score - 지금은 이 필드를 무시하십시오.
"""

res = es.search(index="sn100",
                body={
                    "query": {"match_all": {}}
                })
print(res)
