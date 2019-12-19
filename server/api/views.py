from rest_framework import viewsets
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.decorators import api_view
from django.http import HttpResponse, JsonResponse
from elasticsearch import Elasticsearch
import logging
import json
import requests
from datetime import datetime
from pyfcm import FCMNotification

logging.basicConfig(level='DEBUG')

index=['sn100','sn101']
location=['식당A','기계실']
# 변경 x
elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"

'''
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
        {"time_slot":"2019-10-28T16:14:00Z",import requests



URL = "https://jaeho.dev/api/create/"

data = {"time_slot":"2019-11-01T14:14:00Z",
  "tem":29.3,
  "hum":20,
  "co":0.2,
  "ch4":4.3}

res = requests.post(URL,data=data)
Response(res)

print(res.text)





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
'''



def send_fcm_notification(idx, title, body):
    push_service = FCMNotification(api_key="AAAAA_JhHSw:APA91bHsetgeQtaa8hBOju8M4TuRyeqSg2OaRe-KO3snUOt2fRmAbFP-g5fZlhaDwAEWr1B5CnTBV1oqPbC2hNj0-xVag4E_G36YtCoN03KwRIG0CrkyOnj8BZ-z9GXCVwrj5OEE5fYE")
    ids = []
    ids_list = ['dojeLqbd_7k:APA91bGVsN95hgvl1jmCdyXjwg-cS8e_1-n0Mud63VH3ldEwxeAAB-xKQEh2R8W8snoYN5q8Y1ITnPbxGclD9UdFpb_4EWs8gzz-FVEScu0a3iADBRg4014Cknc_OZT9Kt9GZePJDuu5','dojeLqbd_7k:APA91bGVsN95hgvl1jmCdyXjwg-cS8e_1-n0Mud63VH3ldEwxeAAB-xKQEh2R8W8snoYN5q8Y1ITnPbxGclD9UdFpb_4EWs8gzz-FVEScu0a3iADBRg4014Cknc_OZT9Kt9GZePJDuu5']
    
    ids.append(ids_list[idx])

    result = push_service.notify_multiple_devices(registration_ids=ids, message_title=title, message_body=body)





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


@api_view(['POST'])
def create(request):
    index_type = ['sn100','sn101']
    doc_type = ['A','B']

    res = []
    data = {
            "time_slot":"",
            "TEM":0,
            "HUM":0,
            "CO":0,
            "CH4":0,
    }

    if 'date_time' in request.data.keys():
        date_time = request.data['date_time']
        request_data = request.data['data']

        if len(request_data) == 0:
            return Response({"status":"fail","message":"Not found data!"})

        for row in request_data:
            if row['number'] == "A":
                idx = 0
            else:
                idx = 1

            for row_2 in row['data']:

                d1 = datetime.strptime(date_time+" "+row_2['s_time'], "%Y-%m-%d %H:%M:%S")
                d1 = d1.strftime("%Y-%m-%dT%H:%M:%SZ")
                    
                for row_3 in row_2['sensor']:
                    data['time_slot'] = d1

                    if row_3["number"] == 0:
                        data['TEM'] = row_3['data']

                        if row_3['data'] >= 31:
                            send_fcm_notification(idx, "위험","온도 수치가 너무 높습니다.")


                    elif row_3['number'] == 1:
                        data['HUM'] = row_3['data']

                        if row_3['data'] >= 50:
                             send_fcm_notification(idx, "위험", "습도 수치가 너무 높습니다.")
                    

                    elif row_3['number'] == 2:
                        data['CO'] = row_3['data']

                        if row_3['data'] >= 5:
                            send_fcm_notification(idx, "위험","일산화탄소 수치가 너무 높습니다.")
                        

                    elif row_3['number'] == 3:
                        data['CH4'] = row_3['data']

                        if row_3['data'] >= 200:
                            send_fcm_notification(idx, "위험","메탄 수치가 너무 높습니다.")

        
                elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
                es = Elasticsearch(elasticsearch_end_point)
                
                res.append(es.index(index=index_type[idx], doc_type=doc_type[idx], body=data))
                
        res = {"status":"success","elasticsearch_log":res}
                
        return Response(res)
    
    else:
        res = {
            "status":"fail",
            "message":"빈 데이터가 있습니다."
        }
        
        return Response(res)


@api_view(['GET'])
def search(request,start,end,SerialNumber):
    if request.method == 'GET':

        if str(type(start)) == "<class 'int'>" and str(type(end)) == "<class 'int'>":
            
            datas = []

            query = {"query":{"match_all":{}},"sort": [{ "time_slot": "desc" }],"from":start,"size":end}
        
            elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
            es = Elasticsearch(elasticsearch_end_point)

            if SerialNumber == index[0] :
                res = es.search(index="sn100",body=query)
            else:
                res = es.search(index="sn101",body=query)

            for source in res['hits']['hits']:
                datas.append(source['_source'])
            
            res = { "status":"success", "data":datas}
        else:
            res = {"status":"fail", "message":"정상적인 요청이 아닙니다."}

        return Response(res)
    elif request.method == 'POST':

        res = {"status":"fail", "message":"검색은 GET으로 요청해야합니다."}
        return Response(res)



@api_view(['GET'])
def period(request, serial_number, start_date, end_date):
    datas = []

    d1 = datetime.strptime(str(start_date), "%Y%m%d")
    start_date = d1.strftime("%Y-%m-%d")

    d1 = datetime.strptime(str(end_date),"%Y%m%d")
    end_date = d1.strftime("%Y-%m-%d")

    query = {"query":{"range":{"time_slot":{"gte":start_date,"lte":end_date}}},'size':10000}


    elasticsearch_end_point = "https://search-capstone-jxmuqgz457mkhgptlpbvget25m.ap-northeast-2.es.amazonaws.com/"
    es = Elasticsearch(elasticsearch_end_point)
    
    if serial_number == index[0]:
        res = es.search(index="sn100",body=query)
    
    elif serial_number == index[1]:
        res = es.search(index="sn101",body=query)

    hits = res['hits']['total']['value']

    if hits != 0:
        for source in res['hits']['hits']:
            datas.append(source['_source'])
    
        res = {"status":"success", "hits":hits, "data":datas}

    else:
        res = {"status":"fail", "hits":hits, "message":"Not found data"}
    
    return Response(res)



@api_view(['GET'])
def info(request,SerialNumber):
        if SerialNumber == index[0]:
            return Response(location[0])
        else:
            return Response(location[1])
        
