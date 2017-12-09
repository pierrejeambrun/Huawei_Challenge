# Main file for the Tornado App.
import tornado.ioloop as ioloop
import tornado.web as web
import tornado.websocket as websocket
import tornado.httpserver as httpserver
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
            data = json.loads(message)
        except ValueError:
            self.write_message(json.dumps({"success": False, "error": "invalid json"}))

        self.write_message(json.dumps({"success": True, "data": self.handleData(data)}))

    def on_close(self):
        print("WebSocket closed")

    def handleData(self, data):
        #DO STUFF HERE
        return data

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
