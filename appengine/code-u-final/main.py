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
        lists = {
            "itemLists" : []
        }

        #Add the new user to the database
        user = User(email=email,item_lists=json.encode(lists))
        user_key = user.put()

        #Respond with the user key
        self.response.write(user_key.urlsafe())


class ItemListCreateHandler(webapp2.RequestHandler):
    def get(self):

        name = self.request.get('name')
        user_key = ndb.Key(urlsafe=self.request.get('user_key'))
        user = user_key.get()
        
        
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


        self.response.write(user_item_lists)



app = webapp2.WSGIApplication([
    ('/', MainHandler),
    ('/user/create', UserCreateHandler),
    ('/list/create',ItemListCreateHandler)
], debug=True)
