# Main file for the Tornado App.
import tornado.ioloop as ioloop
import tornado.web as web
import tornado.websocket as websocket
import tornado.httpserver as httpserver
from random import randint
import json

class MainHandler(web.RequestHandler):
    def get(self):
        self.write("There is nothing for you here. ;)")

class webSocketHandler(websocket.WebSocketHandler):
    def open(self):
            print("WebSocket opened")

    def check_origin(self, origin):
        return True #fixes forbidden code

    def on_message(self, message):
        try:
            parsedData = json.loads(message)

            #["a_magnitude", "a_frequency", "a_mean", "a_sddeviation", "g_magnitude", "g_frequency", "g_mean", "g_sddeviation"]
            # {
            #     u'accelerationMagnitudeMean': 0.13395412,
            #     u'gyroscopicFrequency': 0,
            #     u'gyroscopicMagnitudeMean': 0.002657922,
            #     u'accelerationMean': -0.13637818,
            #     u'gyroscopicStd': 0.01168305,
            #     u'accelerationFrequency': 0,
            #     u'gyroscopicMean': -0.0014103942,
            #     u'accelerationStd': 0.017011141
            # }
            data = [
                parsedData['accelerationMagnitudeMean'],
                parsedData['accelerationFrequency'],
                parsedData['accelerationMean'],
                parsedData['accelerationStd'],
                parsedData['gyroscopicMagnitudeMean'],
                parsedData['gyroscopicFrequency'],
                parsedData['gyroscopicMean'],
                parsedData['gyroscopicStd']
            ]
	except ValueError:
            self.write_message(json.dumps({"success": False, "error": "invalid json"}))

        self.write_message(json.dumps({"success": True, "data": self.handleData(data)}))

    def on_close(self):
        print("WebSocket closed")

    def handleData(self, data):
        #DO STUFF HERE
	print(data)
        return randint(1,4)

def make_app():
    return web.Application([
        (r"/websock", webSocketHandler),
        (r"/", MainHandler)
    ])

if __name__ == "__main__":
    app = make_app()
    server = httpserver.HTTPServer(app)
    server.bind(80)
    server.start(0)  # forks one process per cpu
    ioloop.IOLoop.current().start()
