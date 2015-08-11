# !/usr/bin/env python

import json
import webapp2
import urllib

from models import User
from models import Group
from models import ItemList

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

class UserDeleteHandler(webapp2.RequestHandler):
    def get(self):
        user_key = self.request.get('user_key')
        
        try:
            # Add the new user to the database
            user_key = User.delete(user_key)

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

class UserGetGroupsHandler(webapp2.RequestHandler):
    def get(self):
        user_key = self.request.get('user_key')
        versions = self.request.get('versions')

        try:
            #user_key = User.get_groups(user_key)

            # Respond with the user key
            self.response.set_status(200)
            self.response.write(User.get_groups(user_key, JSON.unquote(versions)))
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class ItemListCreateHandler(webapp2.RequestHandler):
    def get(self):
        # Check if the list name is provided
        group_key = self.request.get('group_key')
        name = self.request.get('name')
        
        try:
            # Add the new list to the database
            grocery_list_key = ItemList.create(group_key, name)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(grocery_list_key)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))


class ItemListEditHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        list_key = self.request.get('list_key')
        changed_content_str = self.request.get('changed_content')

        try:
            # Add the new list to the database
            changed_content = JSON.decode(changed_content_str)
            new_content = ItemList.update_content(list_key, changed_content)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class CreatGroupHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        user_key = self.request.get('user_key')
        name = self.request.get('name')

        try:
            # Add the new list to the database
            new_content = Group.create(user_key, name)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class EditGroupHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        group_key = self.request.get('group_key')
        name = self.request.get('name')
        photo = self.request.get('photo')

        try:
            # Add the new list to the database
            new_content = Group.edit(group_key, name, photo)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class AddUsersGroupHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        group_key = self.request.get('group_key')
        names = self.request.get('usernames')

        try:
            # Add the new list to the database
            new_content = Group.add_users(group_key, names)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))

class ConfirmGroupHandler(webapp2.RequestHandler):
    def get(self):

        # Check if the list name is provided
        user_key = self.request.get('user_key')
        group_key = self.request.get('group_key')
        confirmation = self.request.get('confirmation')

        try:
            # Add the new list to the database
            new_content = ""
            if (confirmation == "ACCEPT"):
                new_content = Group.accept(user_key, group_key)
            else:
                new_content = Group.deny(user_key, group_key)

            # Respond with the list key
            self.response.set_status(201)
            self.response.write(new_content)
        except Exception as e:
            # Respond with the error
            self.response.set_status(406)
            self.response.write(str(e))       

app = webapp2.WSGIApplication([
    ('/user/create', UserCreateHandler),
    ('/user/delete', UserDeleteHandler),
    ('/user/login', UserLoginHandler),
    ('/user/get/groups', UserGetGroupsHandler),

    ('/group/create', CreatGroupHandler),
    ('/group/edit', EditGroupHandler),
    ('/group/user/add', AddUsersGroupHandler),
    ('/group/user/confirm', ConfirmGroupHandler),

    ('/list/create', ItemListCreateHandler),
    ('/list/edit', ItemListEditHandler),
    ('/', MainHandler)
], debug=True)