from django.urls import path
from django.views.generic import TemplateView

from .views import (
    ChargingAPI,
    ChargingResetAPI,
)


app_name = "climate"
urlpatterns = [
    path("status", view=ChargingAPI.as_view()),
    path("reset", view=ChargingResetAPI.as_view()),
]
