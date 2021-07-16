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

from .models import Charging
from .serializers import ChargingSerializer

LOGGER = logging.getLogger(__name__)


class ChargingAPI(GenericAPIView, LimitOffsetPagination):
    
    serializer_class = ChargingSerializer

    # authentication_classes = [TokenAuthentication]
    permission_classes = [AllowAny]

    def get_queryset(self, city = None):
        if not city:
            city = "California"
        queryset = Charging.objects.filter(location=city)
        return queryset

    @swagger_auto_schema(operation_description="Returns current temperature.")
    def get(self, request: WSGIRequest, *args, **kwargs):
        if kwargs:
            city = kwargs.pop('city', None)
        # charging_record = self.get_queryset()
        charging_record = Charging.objects.get(id=1)

        # Increasing or decreasing temperature
        print(charging_record.charging)
        print(charging_record.current_charge)
        print(charging_record.charge_threshold)
        if charging_record.charging and charging_record.current_charge < charging_record.charge_threshold:

            logger_msg = "Charging in progress %s"
            input_data = {"current_charge": charging_record.current_charge + 1}
        elif charging_record.charging:
            logger_msg = "Charging stopped due to peak hour and current charge matching threshold %s"
            input_data = {
                            "charging": False,
                            "status": "Peak hour in effect. Charger plugged in and charging will resume during off-peak hour as current charge is at threshold of 30"}
        else:
            logger_msg = "User requested entry with data %s"
            input_data = {"charging": False}
            

        serializer = ChargingSerializer(charging_record, data=input_data)

        if serializer.is_valid():
            data = serializer.save()
            response = serializer.data
            LOGGER.info(logger_msg, response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)

    @swagger_auto_schema(operation_description="Set the temperature to input value.")
    def post(self, request: WSGIRequest, *args, **kwars):
        record = Charging.objects.get(id=1)
        input_data = {
                        "charging": True,
                        "status": "Peak hour in effect. Charger plugged in and charging will continue until threshold is met."
                    }
        serializer = ChargingSerializer(record, data=input_data)
        if serializer.is_valid():
            data = serializer.save()
            LOGGER.info("Charging started %s", data)
            response = serializer.data
            LOGGER.info("Updated data after starting temperature %s", response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)


class ChargingResetAPI(GenericAPIView, LimitOffsetPagination):
    
    serializer_class = ChargingSerializer

    # authentication_classes = [TokenAuthentication]
    permission_classes = [AllowAny]

    @swagger_auto_schema(operation_description="Resets temperature values.")
    def post(self, request: WSGIRequest):
        record = Charging.objects.get(id=1)
        data = {
                    "id": 1,
                    "location": "California",
                    "charging_type": "Level 2",
                    "charging": False,
                    "set_charge_threshold": 30,
                    "current_charge": 23,
                    "status": "Charger not plugged in."
                }
        serializer = ChargingSerializer(record, data=data)
        if serializer.is_valid():
            data = serializer.save()
            LOGGER.info("Resetting initial values %s", data)
            response = serializer.data
            LOGGER.info("Reset successfully done %s", response)
            response_status = status.HTTP_202_ACCEPTED
        else:
            response = serializer.errors
            response_status = status.HTTP_400_BAD_REQUEST
        return Response(response, status=response_status)