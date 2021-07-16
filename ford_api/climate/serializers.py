from django.db import models
from django.db.models import fields
from rest_framework import serializers
from .models import Climate



class ClimateSerializer(serializers.ModelSerializer):

    def __init__(self, *args, **kwargs):
        kwargs['partial'] = True
        super(ClimateSerializer, self).__init__(*args, **kwargs)

    class Meta:
        model = Climate
        fields = '__all__'