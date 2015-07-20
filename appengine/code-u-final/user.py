from google.appengine.ext import ndb

class User(ndb.Model):
    email = ndb.StringProperty(required=True, indexed=True)
    item_lists = ndb.JsonProperty(required=True)