# Create your views here.
import logging

from django.http.response import Http404
from rest_framework.generics import GenericAPIView
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.authentication import TokenAuthentication
from django.core.handlers.wsgi import WSGIRequest
from rest_framework.response import Response
from rest_framework import status
from rest_framework.pagination import LimitOffsetPagination
from drf_yasg.utils import swagger_auto_schema

from .models import Climate
from .serializers import ClimateSerializer

LOGGER = logging.getLogger(__name__)

def celcius_value(temperature_f):
    temperature_c = round((temperature_f - 32) * 5.0/9.0, 2)
    return temperature_c

class TemperatureAPI(GenericAPIView, LimitOffsetPagination):
    
    serializer_class = ClimateSerializer

    # authentication_classes = [TokenAuthentication]
    permission_classes = [AllowAny]

    def get_queryset(self, city = None):
        if not city:
            city = "California"
        queryset = Climate.objects.filter(location=city)
        return queryset

    @swagger_auto_schema(operation_description="Returns current temperature.")
    def get(self, request: WSGIRequest, *args, **kwargs):
        if kwargs:
            city = kwargs.pop('city', None)
        # climate_record = self.get_queryset()
        climate_record = Climate.objects.get(id=1)

        # Increasing or decreasing temperature
        if climate_record.cooling and climate_record.set_temperature_f < climate_record.current_temperature_f:
            logger_msg = "Cooling in progress %s"
            input_data = {"current_temperature_f": climate_record.current_temperature_f - 1}
        elif climate_record.heating and climate_record.set_temperature_f > climate_record.current_temperature_f:
            logger_msg = "Heating in progress %s"
            input_data = {"current_temperature_f": climate_record.current_temperature_f + 1}
        else:
            logger_msg = "User requested entry with data %s"
            input_data = {"cooling": False, "heating": False}

        serializer = ClimateSerializer(climate_record, data=input_data)

        if serializer.is_valid():
            data = serializer.save()
            response = serializer.data
            response['current_temperature_c'] = celcius_value(response['current_temperature_f'])
            LOGGER.info(logger_msg, response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)

    @swagger_auto_schema(operation_description="Set the temperature to input value.")
    def post(self, request: WSGIRequest, *args, **kwars):
        record = Climate.objects.get(id=1)
        if request.data['set_temperature_f'] < record.current_temperature_f:
            request.data['cooling'] = True
            request.data['heating'] = False
        else:
            request.data['cooling'] = False
            request.data['heating'] = True
        serializer = ClimateSerializer(record, data=request.data)
        if serializer.is_valid():
            data = serializer.save()
            LOGGER.info("Target temperature set to %s", data)
            response = serializer.data
            response['current_temperature_c'] = celcius_value(response['current_temperature_f'])
            LOGGER.info("Updated data after setting temperature %s", response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)


class TemperatureResetAPI(GenericAPIView, LimitOffsetPagination):
    
    serializer_class = ClimateSerializer

    # authentication_classes = [TokenAuthentication]
    permission_classes = [AllowAny]

    @swagger_auto_schema(operation_description="Resets temperature values.")
    def post(self, request: WSGIRequest):
        record = Climate.objects.get(id=1)
        data = {
                    "id": 1,
                    "location": "California",
                    "set_temperature_f": 0,
                    "cooling": False,
                    "heating": False,
                    "current_temperature_f": 93,
                }
        serializer = ClimateSerializer(record, data=data)
        if serializer.is_valid():
            data = serializer.save()
            LOGGER.info("Resetting initial values %s", data)
            response = serializer.data
            response['current_temperature_c'] = celcius_value(response['current_temperature_f'])
            LOGGER.info("Reset successfully done %s", response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)