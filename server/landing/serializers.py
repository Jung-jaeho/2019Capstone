from rest_framework import serializers
from .models import air
 
class MovieSerializer(serializers.ModelSerializer):
    class Meta:
        model = air # 모델 설정
        fields = ('title','text') # 필드 설정
 



