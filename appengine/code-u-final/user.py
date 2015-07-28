from google.appengine.ext import ndb

class User(ndb.Model):
    
    # Set the database properties
    email = ndb.StringProperty(required=True, indexed=True)
    item_lists = ndb.JsonProperty(required=True)

    def update_email(self, new_email):
    	self.email = new_email;
    	user.put()