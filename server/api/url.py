# from django.conf.urls import url, include
from django.urls import path, include
from rest_framework import routers
from api import views



urlpatterns = [
    path('test/',views.testView),
    path('test2/',views.testView2),
    path('create/',views.create),
    path('search/<int:start>/<int:end>/',views.search)
]
