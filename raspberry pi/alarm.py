import RPi.GPIO as GPIO
from time import sleep
import time

alarm = False

def updateAlarmStatus(response):
  global alarm

  if (response == "false"):
    alarm = False

def enableAlarm(buzzer, led, firebase):
  global alarm
  alarm = True

  while alarm == True:
    GPIO.output(buzzer,GPIO.HIGH)
    GPIO.output(led,GPIO.HIGH)
    sleep(0.5)

    GPIO.output(buzzer,GPIO.LOW)
    GPIO.output(led,GPIO.LOW)
    sleep(0.5)

    firebase.get_async('/ringingstatus', None, callback=updateAlarmStatus)
