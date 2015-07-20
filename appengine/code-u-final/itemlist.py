from google.appengine.ext import ndb

class ItemList(ndb.Model):
    """Holds the list properties and the content of list"""
    name = ndb.StringProperty(required=True, indexed=False)
    content = ndb.JsonProperty(required=True)
