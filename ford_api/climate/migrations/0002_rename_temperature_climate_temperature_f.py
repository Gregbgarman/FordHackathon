# Generated by Django 3.2.5 on 2021-07-11 17:51

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ('climate', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='climate',
            old_name='temperature',
            new_name='temperature_f',
        ),
    ]