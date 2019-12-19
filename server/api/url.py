# from django.conf.urls import url, include
from django.urls import path, include
from rest_framework import routers
from api import views



urlpatterns = [
    path('test/',views.testView),
    path('test2/',views.testView2),
    path('create/',views.create),
    path('search/<str:SerialNumber>/<int:start>/<int:end>/',views.search),
    path('period/<str:serial_number>/<int:start_date>/<int:end_date>/',views.period),
    path('info/<str:SerialNumber>/',views.info)
]
