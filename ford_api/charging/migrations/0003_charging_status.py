# Generated by Django 3.2.5 on 2021-07-15 05:02

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('charging', '0002_rename_set_charge_threshold_charging_charge_threshold'),
    ]

    operations = [
        migrations.AddField(
            model_name='charging',
            name='status',
            field=models.CharField(default='Charger not plugged in.', max_length=200),
            preserve_default=False,
        ),
    ]
