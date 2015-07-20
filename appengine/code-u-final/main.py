#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
import webapp2

from webapp2_extras import json

from google.appengine.ext import ndb

from user import User
from itemlist import ItemList

class MainHandler(webapp2.RequestHandler):
    def get(self):
        self.response.write('Hello world!')

class UserCreateHandler(webapp2.RequestHandler):
    def get(self):
        email = self.request.get('email')
        
        #Check if email is not empty
        if (email == ""):
            self.response.set_status(400)
            self.response.write("Email not provided")
            return
        
        
        #Check if the email is already in use
        existing_user = User.query(User.email == email).fetch()
        if (len(existing_user) > 0):
            self.response.set_status(406)
            self.response.write("Email already in use")
            return
        
        lists = {
            "itemLists" : []
        }

        #Add the new user to the database
        user = User(email=email,item_lists=json.encode(lists))
        user_key = (user.put()).urlsafe()


        #Respond with the user key
        self.response.set_status(201)
        self.response.write(user_key)


class ItemListCreateHandler(webapp2.RequestHandler):
    def get(self):

        #Check if the list name is provided
        name = self.request.get('name')
        if (name == ""):
            self.response.set_status(400)
            self.response.write("List name not provided")
            return
        
        #Checks if user exists
        user_key = ndb.Key(urlsafe=self.request.get('user_key'))
        user = user_key.get()
        if (user == null){
            self.response.set_status(400)
            self.response.write("The user does not exist")
            return
        }
        
        #Set the list to empty
        list_content = {
            "items" : []
        }
        
        #Add the new item list to the database
        item_list = ItemList(name=name, content=json.encode(list_content))
        item_list_key = (item_list.put()).urlsafe()
        
        #Add the item list key to the user's avaliable lists
        user_item_lists = json.decode(user.item_lists)
        user_item_lists["itemLists"].append(item_list_key)
        user.item_lists = json.encode(user_item_lists)
        user.put()

        #Respond with a success
        self.response.set_status(201)
        self.response.write(user_item_lists)

class ItemListItemHandler(webapp2.RequestHandler):
    def get(self):
        pass


app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/user/create', UserCreateHandler),
    ('/list/create',ItemListCreateHandler)
    ('/list/item',ItemListCreateHandler)
], debug=True)
