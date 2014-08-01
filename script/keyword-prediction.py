import tornado.ioloop
import tornado.web
import urllib
import json
from textblob import TextBlob

def unique(a):
    """ return the list with duplicate elements removed """
    return list(set(a))

def intersect(a, b):
    """ return the intersection of two lists """
    return list(set(a) & set(b))

def union(a, b):
    """ return the union of two lists """
    return list(set(a) | set(b))

class MainHandler(tornado.web.RequestHandler):
    def get(self):
    	title = urllib.unquote(self.get_argument('title', default='', strip=True))
    	content = urllib.unquote(self.get_argument('content', default='', strip=True))
    	blob_title = TextBlob(title)
    	blob_content = TextBlob(content)
    	keywords = intersect(blob_title.noun_phrases, blob_content.noun_phrases)
        #self.write("<br> blob_title " + json.dumps(blob_title.noun_phrases))
        #self.write("<br> blob_content " + json.dumps(blob_content.noun_phrases))
        #self.write("<br> keywords " + json.dumps(keywords))
        self.write(json.dumps(keywords))

application = tornado.web.Application([
    (r"/keywords", MainHandler),
])

if __name__ == "__main__":
    application.listen(8888)
    tornado.ioloop.IOLoop.instance().start()