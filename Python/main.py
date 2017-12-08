# Main file for the Tornado App.
import tornado.ioloop as ioloop
import tornado.web as web
import tornado.websocket as websocket
import json

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
        (r"/", webSocketHandler),
    ])

if __name__ == "__main__":
    app = make_app()
    app.listen(8888)
    ioloop.IOLoop.current().start()
