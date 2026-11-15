import os
import wifi
import time
import board
import busio
import ssl
import socketpool
import adafruit_requests
from adafruit_seesaw.seesaw import Seesaw
import microcontroller

# establish wifi connection if not present already
try:
    wifi.radio.connect(
        os.getenv("CIRCUITPY_WIFI_SSID"), 
        os.getenv("CIRCUITPY_WIFI_PASSWORD")
    )
    print("Connected!")
    print(f"My IP address: {wifi.radio.ipv4_address}")
except Exception as e:
    print(f"Failed to connect: {e}")


# SCL first, then SDA
i2c = busio.I2C(board.GP11, board.GP10) # hardcode gpio pins to indicate I2c

ss = Seesaw(i2c, addr=0x36)

pool = socketpool.SocketPool(wifi.radio)
requests = adafruit_requests.Session(pool, ssl.create_default_context())

# manually create UUID
hex_string = ''.join(f'{byte:02x}' for byte in microcontroller.cpu.uid)
# Pad with zeros manually
while len(hex_string) < 32:
    hex_string = hex_string + '0'
plant_id = f'{hex_string[0:8]}-{hex_string[8:12]}-{hex_string[12:16]}-{hex_string[16:20]}-{hex_string[20:32]}'
print(plant_id)

while True:
    moisture = ss.moisture_read()
    temp = ss.get_temp()

    payload = {
        "device_id": "pico01",
        "temperature": temp,
        "moisture": moisture
    }

    try:
        response = requests.post(
            "http://172.20.10.11:8080/api/sensor",
            json=payload,
            headers={
                "Content-Type": "application/json"
            }
        )

        print("Status:", response.status_code)
        print(response.text)

        response.close()

    except Exception as e:
        print("POST failed:", e)

    time.sleep(20)