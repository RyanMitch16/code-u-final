# !/usr/bin/env python

import webapp2

# 2 JSONs?
import json from webapp2_extras
import json as webapp2_json

from user import User
from itemlist import ItemList
from google.appengine.ext import ndb


# Haven't verified if this works as intended\
class BaseHandler(webapp2.RequestHandler):
    def FindUser(user_key):
        user = ndb.Key(urlsafe=user_key).get()
        ### Perhaps these '.get()'s are the ones returning errors
        if (user == None):
            self.response.set_status(400)
            self.response.write("This user does not exist")
            return
            # Does ^ work?
        return user

    def FindItemList(list_key):
        item_list = ndb.Key(urlsafe=list_key).get()
        ### Perhaps these '.get()'s are the ones returning errors
        if (item_list == None):
            self.response.set_status(400)
            self.response.write("This list does not exist")
            return
            # Does ^ work?
        return item_list

class MainHandler(BaseHandler):
    def get(self):
        self.response.write('Hello world!')

class UserCreateHandler(BaseHandler):
    def get(self):
        email = self.request.get('email')
        
        # Check if email is not empty
        if (email == ""):
            self.response.set_status(400)
            self.response.write("Email not provided")
            return
        
        # Check if the email is already in use
        existing_user = User.query(User.email == email).fetch()
        if (len(existing_user) > 0):
            self.response.set_status(406)
            self.response.write("Email already in use")
            return
        
        lists = {
            "itemLists" : []
        }

        # Add the new user to the database
        user = User(email=email,item_lists=webapp2_json.encode(lists))
        user_key = (user.put()).urlsafe()

        # Respond with the user key
        self.response.set_status(201)
        self.response.write(user_key)

class UserDeleteHandler(BaseHandler):
    def get(self):
        # Checks if user exists
        user = self.FindUser(self.request.get('user_key'))

        # Deletes key ∴ also user
        user.key.delete()
        self.response.set_status(200)
        self.response.write("User successfully deleted")

class UserUpdateHandler(BaseHandler):
    def get(self):
        # Checks if user exists
        user = self.FindUser(self.request.get('user_key'))

        new_email = self.request.get('new_email')
        user.update_email(new_email)
        # Either 'update_' or 'set_', dunno specifics
        # self.response.write('Function not yet implemented')

class UserLoginHandler(BaseHandler):
    def get(self):
        email = self.request.get('email')
        # Check if email is not empty
        if (email == ""):
            self.response.set_status(400)
                self.response.write("Email not provided")
                return

        user = User.query(User.email == email).fetch()
        if (len(user) == 0):
            self.response.set_status(406)
            self.response.write("User does not exist")
            return

        # Respond with the user key
        self.response.set_status(201)
        self.response.write(user.urlsafe())

class ItemListCreateHandler(BaseHandler):
    def get(self):
        # Check if the list name is provided
        name = self.request.get('list_name')
        if (name == ""):
            self.response.set_status(400)
            self.response.write("List name not provided")
            return
        
        # Checks if user exists
        user = self.FindUser(self.request.get('user_key'))
        
        # Checks if list already added, if so add adds a '(#)' like windows for new folders
        # Putting this in pause for now
        ### 
        # TODO: 
        #   Not checking for same user. 
        #   Need to turn the item_lists into map (pref) or array and check membership
        #   How to turn key into name??
        ###
        # existing_user = User.query(User.email == email).fetch()
        # existing_list = ItemList.query(ItemList.name == name).fetch()
        # duplicateNumber = len(existing_user)
        # if (duplicateNumber > 1):
        #     suffix = " (" + duplicateNumber + " )"
        #     name += suffix

        # Set the list to empty
        list_content = {
            "itemIDCount" : 0,
            "items" : []
        }

        # Add the new item list to the database
        item_list = ItemList(name=name, content=webapp2_json.encode(list_content))
        item_list_key = (item_list.put()).urlsafe()
        
        # Add the item list key to the user's avaliable lists
        user_item_lists = webapp2_json.decode(user.item_lists)
        user_item_lists["itemLists"].append(item_list_key)
        user.item_lists = webapp2_json.encode(user_item_lists)
        user.put()

        # Respond with the list key
        self.response.set_status(201)
        self.response.write(item_list_key)

class ItemListDeleteHandler(BaseHandler):
    def get(self):
        # Checks if key exists
        item_list = FindItemList(self.request.get('list_key'))

        # Deletes key ∴ also list
        item_list.key.delete()
        self.response.set_status(200)
        self.response.write("List successfully deleted")

class ItemListEditHandler(BaseHandler):
    def get(self):
        # Checks if user exists
        user = self.
        
        # Checks if the list exists
        list_key_str = self.request.get('list_key')
        item_list = FindItemList(list_key_str))

        # Checks if the user is allowed to edit the list
        allowed = False
        user_item_lists = webapp2_json.decode(user.item_lists)
        for item_list in user_item_lists["itemLists"]:
            if (item_list == list_key_str):
                allowed = True
                break
        if (allowed == False):
            self.response.set_status(403)
            self.response.write("User not allowed to edit this list")
            return
        
        # Get the json with the new data
        changed_content_str = self.request.get('changed_content')
        changed_content = webapp2_json.decode(changed_content_str)
        
        # Update the content
        item_list.update_content(changed_content)
            
        self.response.set_status(201)
        self.response.write((str(json.dumps(item_list.content)).replace("\\\"","\""))[1:-1])

class ItemListGetAllHandler(BaseHandler):
    def get(self):
        # Checks if user exists
        user = self.FindUser(self.request.get('user_key'))

        response = {
            "itemLists" : []
        }

        user_item_lists = webapp2_json.decode(user.item_lists)
        for list_key_str in user_item_lists["itemLists"]:
            item_list = FindItemList(list_key_str)

            response["itemLists"].append( {
                    "key" : list_key_str,
                    "name" : item_list.name
                }
            )

        response = webapp2_json.encode(response)

        self.response.set_status(200)
        self.response.write((str(json.dumps(response)).replace("\\\"","\""))[1:-1])


class ItemListGetHandler(BaseHandler):
    def get(self):
        # Checks if user exists
        user = self.FindUser(self.request.get('user_key'))
        
        # Checks if the list exists
        list_key_str = self.request.get('list_key')
        item_list = FindItemList(list_key_str)
        
        user_item_lists = webapp2_json.decode(user.item_lists)
        
        for item_list in user_item_lists["itemLists"]:
            if (item_list == list_key_str):
                allowed = True
                break
        if (allowed == False):
            self.response.set_status(403)
            self.response.write("User not allowed to view this list")
            return

        self.response.set_status(200)
        self.response.write((str(json.dumps(item_list.content)).replace("\\\"","\""))[1:-1])

# Maybe follow 'CRUD' initials for names?

app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/user/create', UserCreateHandler),
    ('/user/delete',ItemUserDeleteHandler),
    ('/user/login', UserLoginHandler)
    ('/user/lists', ItemListGetAllHandler),
    ('/user/update',UserUpdateHandler),
    ('/list/create',ItemListCreateHandler),
    ('/list/delete',ItemListDeleteHandler),
    ('/list/edit',ItemListEditHandler),
    ('/list/get',ItemListGetHandler),
], debug=True)
