from django.db import models

# Create your models here.
class Climate(models.Model):
    """Base model class with created and updated datetime fields
    automatically populated"""

    location = models.CharField(max_length=100, default="California")
    outside_temperature_f = models.IntegerField(default=93)
    set_temperature_f = models.IntegerField(default=0)
    cooling = models.BooleanField(default=False)
    heating = models.BooleanField(default=False)
    current_temperature_f = models.IntegerField(default=93)
