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

import json from webapp2_extras
import json as webapp2_json

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
        user = User(email=email,item_lists=webapp2_json.encode(lists))
        user_key = (user.put()).urlsafe()

        #Respond with the user key
        self.response.set_status(201)
        self.response.write(user_key)

class UserLoginHandler(webapp2.RequestHandler):
    def get(self):
        email = self.request.get('email')
    
        #Check if email is not empty
        if (email == ""):
            self.response.set_status(400)
                self.response.write("Email not provided")
                return

        user = User.query(User.email == email).fetch()
        if (len(user) == 0):
            self.response.set_status(406)
            self.response.write("User does not exist")
            return

        #Respond with the user key
        self.response.set_status(201)
        self.response.write(user.urlsafe())

class ItemListCreateHandler(webapp2.RequestHandler):
    def get(self):

        #Check if the list name is provided
        name = self.request.get('list_name')
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
        
        # Checks if list already added, if so add adds a '(#)' like windows for new folders

        # TODO: 
        #   Not checking for same user. 
        #   Need to turn the item_lists into map (pref) or array and check membership
        existing_list = ItemList.query(ItemList.name == name).fetch()
        if (len(existing_user) > 0):
            duplicateNumber = len(existing_user)
            suffix = " (" + duplicateNumber + " )"
            name += suffix

        #Set the list to empty
        list_content = {
            "itemIDCount" : 0,
            "items" : []
        }

        #Add the new item list to the database
        item_list = ItemList(name=name, content=webapp2_json.encode(list_content))
        item_list_key = (item_list.put()).urlsafe()
        
        #Add the item list key to the user's avaliable lists
        user_item_lists = webapp2_json.decode(user.item_lists)
        user_item_lists["itemLists"].append(item_list_key)
        user.item_lists = webapp2_json.encode(user_item_lists)
        user.put()

        #Respond with the list key
        self.response.set_status(201)
        self.response.write(item_list_key)


class ItemListEditHandler(webapp2.RequestHandler):
    
    def get(self):
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
        user_item_lists = webapp2_json.decode(user.item_lists)
        for item_list in user_item_lists["itemLists"]:
            if (item_list == list_key_str):
                allowed = True
                break
        if (allowed == False):
            self.response.set_status(403)
            self.response.write("User not allowed to edit this list")
            return
        
        #Get the json with the new data
        changed_content_str = self.request.get('changed_content')
        changed_content = webapp2_json.decode(changed_content_str)
        
        #Update the content
        list.update_content(changed_content)
            
        self.response.set_status(201)
        self.response.write((str(json.dumps(list.content)).replace("\\\"","\""))[1:-1])

class ItemListGetAllHandler(webapp2.RequestHandler):
    def get(self):
        
        #Checks if user exists
        user_key = ndb.Key(urlsafe=self.request.get('user_key'))
        user = user_key.get()
        if (user == None):
            self.response.set_status(400)
            self.response.write("The user does not exist")
            return

        response = {
            "itemLists" : []
        }

        user_item_lists = webapp2_json.decode(user.item_lists)
        for list_key_str in user_item_lists["itemLists"]:
            list_key = ndb.Key(urlsafe=list_key_str)
            list = list_key.get()

            response["itemLists"].append( {
                    "key" : list_key_str,
                    "name" : list.name
                }
            )

        response = webapp2_json.encode(response)

        self.response.set_status(200)
        self.response.write((str(json.dumps(response)).replace("\\\"","\""))[1:-1])


class ItemListGetHandler(webapp2.RequestHandler):

    def get(self):
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
        self.response.write((str(json.dumps(list.content)).replace("\\\"","\""))[1:-1])


app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/user/create', UserCreateHandler),
    ('/user/login', UserLoginHandler)
    ('/user/lists', ItemListGetAllHandler),
    ('/list/create',ItemListCreateHandler),
    ('/list/edit',ItemListEditHandler),
    ('/list/get',ItemListGetHandler)
], debug=True)
