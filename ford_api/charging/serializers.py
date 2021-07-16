from django.db import models
from django.db.models import fields
from rest_framework import serializers
from .models import Charging



class ChargingSerializer(serializers.ModelSerializer):

    def __init__(self, *args, **kwargs):
        kwargs['partial'] = True
        super(ChargingSerializer, self).__init__(*args, **kwargs)

    class Meta:
        model = Charging
        fields = '__all__'