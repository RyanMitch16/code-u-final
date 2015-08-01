# !/usr/bin/env python

import json
import webapp2

from models import User
from models import GroceryList

from webapp2_extras import json as JSON
from google.appengine.ext import ndb

# webapp2 => 'JSON'
# Python  => 'json'

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello world!')

class UserCreateHandler(webapp2.RequestHandler):
    def get(self):
        username = self.request.get('username')
        password = self.request.get('password')
        email = self.request.get('email')
        phone = self.request.get('phone')
        
        try:
            # Add the new user to the database
            user_key = User.create(username,password,email,phone)

            # Respond with the user key
            self.response.set_status(201)
            self.response.write(user_key)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class UserLoginHandler(webapp2.RequestHandler):
    def get(self):
        username = self.request.get('username')
        password = self.request.get('password')

        try:
            # Add the new user to the database
            user_key = User.login(username,password)

            # Respond with the user key
            self.response.set_status(200)
            self.response.write(user_key)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class UserGetListsHandler(webapp2.RequestHandler):
    def get(self):
        user_key = self.request.get('user_key')
        versions = self.request.get('versions')

        try:
            # Add the new user to the database
            versions_json = JSON.decode(versions)
            user_key = User.get_grocery_lists(user_key,versions_json)

            # Respond with the user key
            self.response.set_status(200)
            self.response.write(user_key)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class GroceryListCreateHandler(webapp2.RequestHandler):
    def get(self):
        # Check if the list name is provided
        user_key = self.request.get('user_key')
        name = self.request.get('name')
        
        try:
            # Add the new list to the database
            grocery_list_key = GroceryList.create(user_key, name)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(grocery_list_key)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))


class GroceryListGetHandler(webapp2.RequestHandler):
    def get(self):
        #
        user_key = self.request.get('user_key')
        list_key = self.request.get('list_key')

        try:
            # Add the new user to the database
            contents = GroceryList.get(user_key,list_key)

            # Respond with the user key
            self.response.set_status(200)
            self.response.write(contents)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))


class GroceryListEditHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        user_key = self.request.get('user_key')
        list_key = self.request.get('list_key')
        changed_content_str = self.request.get('changed_content')

        try:
            # Add the new list to the database
            changed_content = JSON.decode(changed_content_str)
            new_content = GroceryList.update_content(user_key, list_key, changed_content)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

app = webapp2.WSGIApplication([
    ('/user/create', UserCreateHandler),
    ('/user/login', UserLoginHandler),
    ('/user/get/lists', UserGetListsHandler),
    ('/list/create', GroceryListCreateHandler),
    ('/list/get', GroceryListGetHandler),
    ('/list/edit', GroceryListEditHandler),
    ('/', MainHandler),
], debug=True)