# Main file for the Tornado App.
import tornado.ioloop as ioloop
import tornado.web as web
import tornado.websocket as websocket

class webSocketHandler(websocket.WebSocketHandler):
    def open(self):
            print("WebSocket opened")

    def check_origin(self, origin):
        return True

    def on_message(self, message):
        print(message)
        self.write_message(u"You said: " + message)

    def on_close(self):
        print("WebSocket closed")

def make_app():
    return web.Application([
        (r"/", webSocketHandler),
    ])

if __name__ == "__main__":
    app = make_app()
    app.listen(8888)
    ioloop.IOLoop.current().start()
