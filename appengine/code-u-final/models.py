# !/usr/bin/env python

import json

from webapp2_extras import json as JSON
from google.appengine.ext import ndb

class User(ndb.Model):
	"""The model for storing information about each individual user
	"""
	# The required user name and password
	username = ndb.StringProperty(required=True, indexed=True)
	password = ndb.StringProperty(required=True, indexed=False)

	# Helpful for recovering passwords (which may or may not be implemented)
	email = ndb.StringProperty(required=False, indexed=True)

	# Helpful for finding what contacts you can add to the app
	phone = ndb.StringProperty(required=False, indexed=True)

	# The list of items the user has access to
	grocery_lists = ndb.JsonProperty(required=True, indexed=False)

	@staticmethod
	def create(username, password, email="", phone=""):
		"""Creates a new user and adds it to the database

		Keyword arguments:
			username - the username of the user
			password - the encrypted password of the user
			email - the email address to be associate with the user
			phone - the phone address to be associate with the user

		Returns:
			The user key in a string
		"""

		#Check if the user name has been taken
		if (User.check_username(username) == False):
			raise Exception("User name taken")

		#Create an empty json array to represent the avaliable grocery lists
		grocery_lists = { "grocery_lists" : [] }

		#Put the new user in the database
		user = User(username=username, password=password, email=email, phone=phone, grocery_lists=JSON.encode(grocery_lists))
		return (user.put()).urlsafe()

	@staticmethod
	def login(username, password):
		"""Checks if the username and password are valid.

		Keyword arguments:
			username - the username of the user
			password - the encrypted password of the user

		Returns:
			The user key in a string
		"""

		existing_user = User.query(User.username == username).fetch()
		if (len(existing_user) == 0 or existing_user[0].password != password):
			raise Exception("Wrong username or password")
		return (existing_user[0].key).urlsafe()

	@staticmethod
	def get_grocery_lists(user_key, versions):
		"""Get the grocery lists that a user has access

		Keyword arguments:
			user_key - the key of the user
			versions - the json contaning the client versions of each list

		Returns:
			The grocery lists in a json
		"""

		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		response = { "grocery_lists" : [] }

		user_grocery_lists = JSON.decode(user.grocery_lists)
		for list_key_str in user_grocery_lists["grocery_lists"]:
			grocery_list = GroceryList.find_by_key(list_key_str)
			
			if ((list_key_str in versions) and (versions[list_key_str] == grocery_list.version)):
				response["grocery_lists"].append( {
					"list_key" : list_key_str,
					"list_name" : grocery_list.name,
					"list_version" : grocery_list.version,
					"list_contents" : ""
				})
			else:
				response["grocery_lists"].append( {
					"list_key" : list_key_str,
					"list_name" : grocery_list.name,
					"list_version" : grocery_list.version,
					"list_contents" : str(json.dumps(grocery_list.content).replace("\\\"",""))[1:-1]
				})

		response = JSON.encode(response)
		return str(json.dumps(response)).replace("\\\"","\"")[1:-1]


	@staticmethod
	def check_username(username):
		"""Check if the username has not been taken.

		Keyword arguments:
			username - the username to check for availability

		Returns:
			True - if the username has not been taken
			False - if the username has been taken
		"""
		existing_user = User.query(User.username == username).fetch()
		if (len(existing_user) == 0):
			return True
		return False

	@staticmethod
	def check_email(email):

		existing_user = User.query(User.email == email).fetch()
		if (len(existing_user) == 0):
			return True
		return False

	@staticmethod
	def find_by_key(key):
		"""Finds the user by looking up the key

		Keyword arguments:
			key - the user key

		Returns:
			The user if one exists
		"""
		try:
			user = ndb.Key(urlsafe=key).get()
			if (user is not None):
				return user
		except:
			return None
		return None

	@staticmethod	
	def find_by_username(username):
		existing_user = User.query(User.username == username).fetch()
		if (len(existing_user) == 0):
			return None
		return existing_user[0]

	@staticmethod
	def find_by_phone(phone):
		existing_user = User.query(User.phone == phone).fetch()
		if (len(existing_user) == 0):
			return None
		return existing_user[0]   	

	def check_list_access(self, list_key):
		"""Checks if the user has access to the list

		Keyword arguments;
			list_key - the key in string form

		Returns:
			True - if the user has access
			False - if otherwise
		"""
		allowed = False
		user_grocery_lists = JSON.decode(self.grocery_lists)
		for key in user_grocery_lists["grocery_lists"]:
			if (key == list_key):
				return True
		return False

	def update_email(self, new_email):
		self.email = new_email;
		user.put()


class GroceryList(ndb.Model):

    # Set the name of the list
    name = ndb.StringProperty(required=True, indexed=False)

    #The content of the grocery list
    content = ndb.JsonProperty(required=True, indexed=False)

    #The current version number of the list
    version = ndb.IntegerProperty(required=True, indexed=False)
    
    # Set the constants for updating the conetnt of the list
    UPDATE_CONTENT_OPCODE = "_op"
    UPDATE_CONTENT_OPCODE_ADD = "add"
    UPDATE_CONTENT_OPCODE_DELETE = "delete"
    UPDATE_CONTENT_ID = "_id"

    @staticmethod
    def create(user_key, name="Untilted"):
        """Creates a new list with the provided name

        Keyword arguments:
            user_key - the key of the user who is creating the list
            name - the name of the list

        Returns:
            the key of the list
        """

        #Find the user to add the key to
        user = User.find_by_key(user_key)
        if (user == None):
            raise Exception("Invalid key")

        if (name == ""):
        	name = "Untilted"

        # Set the list to empty
        grocery_list_contents = { "items" : [] }

        # Add the new item list to the database
        grocery_list = GroceryList(name=name, content=JSON.encode(grocery_list_contents),version=0)
        grocery_list_key = (grocery_list.put()).urlsafe()
        
        # Add the item list key to the user's avaliable lists
        user_grocery_lists = JSON.decode(user.grocery_lists)
        user_grocery_lists["grocery_lists"].append(grocery_list_key)
        user.grocery_lists = JSON.encode(user_grocery_lists)
        user.put()

        return grocery_list_key

    @staticmethod
    def get(user_key, list_key):
    	"""Gets the grocery list if the user has access to it

    	Keyword arguments:
            user_key - the string key of the user
            list_key - the string key of the grocery list

        Returns:
        	The contents of the grocery list
    	"""

    	#Find the user
        user = User.find_by_key(user_key)
        if (user == None):
            raise Exception("Invalid user key")

        #Find the grocery list
        grocery_list = GroceryList.find_by_key(list_key)
        if (grocery_list == None):
            raise Exception("Invalid list key")

        # Checks if the user is allowed to edit the list
        if (user.check_list_access(list_key) == False):
            raise Exception("User does not have access to this list")

        #Get the contents of the current grocery list
        return (str(json.dumps(grocery_list.content)).replace("\\\"","\""))[1:-1]

    @staticmethod
    def update_content(user_key, list_key, changed_content):
        """Adds and deletes items from the list (see readme for more info)

        Keyword arguments:
            user_key - the string key of the user
            list_key - the string key of the grocery list
            changed_content - the items to add and delete from the list
        """

        #Find the user
        user = User.find_by_key(user_key)
        if (user == None):
            raise Exception("Invalid user key")

        #Find the grocery list
        grocery_list = GroceryList.find_by_key(list_key)
        if (grocery_list == None):
            raise Exception("Invalid list key")

        # Checks if the user is allowed to edit the list
        if (user.check_list_access(list_key) == False):
            raise Exception("User does not have access to this list")

        #Get the contents of the current grocery list
        content = JSON.decode(grocery_list.content)
        
        for item in changed_content["items"]:
            # Remove the hidden operation code from the item
            opcode = item[GroceryList.UPDATE_CONTENT_OPCODE]
            item.pop(GroceryList.UPDATE_CONTENT_OPCODE)
            
            # Add the item to the list if the op code was to add it
            if (opcode == GroceryList.UPDATE_CONTENT_OPCODE_ADD):
                content["items"].append(item)
            
            # Delete the item from the list if the op code was to delete it
            elif (opcode == GroceryList.UPDATE_CONTENT_OPCODE_DELETE):
                for item2 in content["items"]:
                    if ((GroceryList.UPDATE_CONTENT_ID in item2) and (item2[GroceryList.UPDATE_CONTENT_ID] == item[GroceryList.UPDATE_CONTENT_ID])):
                        content["items"].remove(item2)
                        break

        #Increment the version number
        grocery_list.version += 1

        # Put the new list content in the database
        grocery_list.content = JSON.encode(content)
        grocery_list.put()

        return (str(json.dumps(grocery_list.content)).replace("\\\"","\""))[1:-1]

    @staticmethod
    def find_by_key(key):
        """Finds the grocery list by looking up the key

        Keyword arguments:
            key - the user key

        Returns:
            The user if one exists or None
        """
        try:
            grocery_list = ndb.Key(urlsafe=key).get()
            if (grocery_list is not None):
                return grocery_list
        except:
            return None
        return None



