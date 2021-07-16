from django.urls import path
from django.views.generic import TemplateView

from .views import (
    TemperatureAPI,
    TemperatureResetAPI,
)


app_name = "climate"
urlpatterns = [
    path("temperature", view=TemperatureAPI.as_view()),
    path("reset", view=TemperatureResetAPI.as_view()),
]
