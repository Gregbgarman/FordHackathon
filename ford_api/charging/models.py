from django.db import models

# Create your models here.
class Charging(models.Model):

    location = models.CharField(max_length=100, default="California")
    charging_type = models.CharField(max_length=20, default="Level 2")
    charging = models.BooleanField(default=False)
    peak = models.BooleanField(default=True)
    charge_threshold = models.IntegerField(default=30)
    current_charge = models.IntegerField(default=23)
    status = models.CharField(max_length=200)