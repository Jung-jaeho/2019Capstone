from rest_framework import viewsets
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.http import HttpResponse, JsonResponse
from elasticsearch import Elasticsearch
import logging
import json
logging.basicConfig(level='DEBUG')

# 변경 x
elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"


"""
{
    "uuid":"edsd-dsfsd-assda-fdsds",
    "datas":[
        {"time_slot":"2019-10-28T16:14:00Z",
        "tem":"2019-10-28T16:14:00Z",
        "hum":"2019-10-28T16:14:00Z",
        "co":"2019-10-28T16:14:00Z",
        "ch4":"2019-10-28T16:14:00Z"
        },
        {"time_slot":"2019-10-28T16:14:00Z",
        "tem":"2019-10-28T16:14:00Z",
        "hum":"2019-10-28T16:14:00Z",
        "co":"2019-10-28T16:14:00Z",
        "ch4":"2019-10-28T16:14:00Z"
        },
    ]
}
"""
index=['sn100','sn101']
docType=['A','B']

for idx, data in enumerate(request.data.datas):

    req= {"time_slot": data['time_slot'],
        "tem":data['tem'],
        "hum":data['hum'],
        "co":data['co'],
        "ch4": data['ch4']
    }

    elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
    es = Elasticsearch(elasticsearch_end_point)

    res = es.index(index=index[idx], doc_type=docType[idx],  body=req)

@api_view(['GET','POST'])
def testView(request):

    if request.method == 'GET':
        return Response("[테스트] POST로 요청하세요.")

    elif request.method == 'POST':
        data = request.data
        elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
        es = Elasticsearch(elasticsearch_end_point)
        res = es.index(index="sn100", doc_type="A",  body=data)
        #logging.debug(res)
        return Response(res)


@api_view(['GET','POST'])
def testView2(request):

    if request.method == 'GET':
        return Response("[테스트] POST로 요청하세요.")

    elif request.method == 'POST':
        data = request.data
        return Response(data)


@api_view(['GET','POST'])
# create 데이터를 라즈베리에서 가져와서 ES에다가 넘겨준다 
def create(request):
    #get 요청이오면 데이터를 받아와야되는데 get은 주는거니까 명령어가 잘못왔으니까 그거에 해당하는 잘못됐다는것을 return 해준다.
    if request.method == 'GET':
        data = {
                "status":"fail",
                "message":"정상적인 요청이 아닙니다."
        }

        return Response(data)
        
    elif request.method == 'POST':
        #키값들이 잘 들어왔는지 확인을 하고 데이터 형식에 때려박는다.
        if 'time_slot' in request.data.keys() and 'tem' in request.data.keys() and 'hum' in request.data.keys() and 'co' in request.data.keys() and 'ch4' in request.data.keys():
            data = {
                "time_slot":request.data['time_slot'],
                "TEM":request.data['tem'],
                "HUM":request.data['hum'],
                "CO":request.data['co'],
                "CH4":request.data['ch4']
            }
            elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
            es = Elasticsearch(elasticsearch_end_point)
            res = es.index(index="sn100", doc_type="A",  body=data)
            res = {"status":"success","elasticsearch_log":res}
        else:
            res = {
                    "status":"fail",
                    "message":"빈 데이터가 있습니다."
            }
        
        return Response(res)


@api_view(['GET','POST'])
def search(request,start,end):
    if request.method == 'GET':

        if str(type(start)) == "<class 'int'>" and str(type(end)) == "<class 'int'>":
        
            query = {"query":{"match_all":{}},"sort": [{ "time_slot": "desc" }],"from":start,"size":end}
        
            elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
            es = Elasticsearch(elasticsearch_end_point)
        
            res = es.search(index="sn100",body=query)
            res = { "status":"success", "data":res}
        else:
            res = {"status":"fail", "message":"정상적인 요청이 아닙니다."}

        return Response(res)
    elif request.method == 'POST':

        res = {"status":"fail", "message":"검색은 GET으로 요청해야합니다."}
        return Response(res)


        ###