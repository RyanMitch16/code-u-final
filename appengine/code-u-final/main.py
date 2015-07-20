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
        if (user == None):
            self.response.set_status(400)
            self.response.write("The user does not exist")
            return
        
        
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

        #Respond with the list key
        self.response.set_status(201)
        self.response.write(item_list_key)


class ItemListEditHandler(webapp2.RequestHandler):
    def get(self):
        opcode = self.request.get('opcode')
        
        #Checks if user exists
        user_key = ndb.Key(urlsafe=self.request.get('user_key'))
        user = user_key.get()
        if (user == None):
            self.response.set_status(400)
            self.response.write("The user does not exist")
            return
        
        #Checks if the list exists
        list_key_str = self.request.get('list_key')
        list_key = ndb.Key(urlsafe=list_key_str)
        list = list_key.get()
        if (list == None):
            self.response.set_status(400)
            self.response.write("The list does not exist")
            return
        
        #Checks if the user is allowed to edit the list
        allowed = False
        user_item_lists = json.decode(user.item_lists)
        for item_list in user_item_lists["itemLists"]:
            if (item_list == list_key_str):
                allowed = True
                break
        if (allowed == False):
            self.response.set_status(403)
            self.response.write("User not allowed to edit list")
            return

        if (opcode == "add"):
            #Check if item name provided
            item_name = self.request.get('item_name')
            if (item_name == ""):
                self.response.set_status(400)
                self.response.write("Item name not provided")
                return
        
            #Check if item quantity provided
            item_quantity = self.request.get('item_quantity')
            if (item_quantity == ""):
                self.response.set_status(400)
                self.response.write("Item Quantity not provided")
                return
            
            #Construct the new item
            item = {
                "name" : item_name,
                "item-quantity" : item_quantity
            }
            
            #Add the item to the list
            content = json.decode(list.content)
            content["items"].append(item)
            list.content = json.encode(content)
            list.put()
            
            self.response.set_status(201)
            self.response.write(content)

        elif (opcode == "delete"):
            self.response.set_status(201)
            self.response.write("Deleted")
        else:
            self.response.set_status(400)
            self.response.write("Invalid operation")


app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/user/create', UserCreateHandler),
    ('/list/create',ItemListCreateHandler),
    ('/list/edit',ItemListEditHandler)
], debug=True)
