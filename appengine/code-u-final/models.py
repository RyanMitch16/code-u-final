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

	# The list of groups the user has access to
	groups = ndb.JsonProperty(required=True, indexed=False)

	version = ndb.IntegerProperty(required=True, indexed=False)

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
		groups = []

		#Put the new user in the database
		user = User(username=username, password=password, email=email, phone=phone, groups=JSON.encode(groups), version=0)

		return (user.put()).urlsafe()

	@staticmethod
	def delete(user_key):

		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		user.key.delete()

		return "Deleted"

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
	def get_groups(user_key, versions):
		"""Get the groups the user is included in
		
		Keyword arguments:
			user_key - the key of the user

		Returns:
			The groups the user has access to in a json array
		"""

		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		response = {"user_version" : user.version,
					"groups" : []}

		if (user_key not in versions) or (versions[user_key] < user.version):

			user_groups = JSON.decode(user.groups)

			for group_key in user_groups:

				group = Group.find_by_key(group_key)

				if (group_key not in versions) or (versions[group_key] < group.version):

					response["groups"].append(
						{
							"group_key" : group_key,
							"group_name" : group.name,
							"group_usernames" : JSON.decode(group.usernames),
							"group_pending_usernames" : JSON.decode(group.pending_usernames),
							"group_version" : group.version,
							"group_lists" : JSON.decode(group.get_item_lists(versions)),
							"group_photo" : group.photo if ((group_key+"_photo" not in versions) or (versions[group_key+"_photo"] < group.photo_version)) else "",
							"group_photo_version" : group.photo_version
						})

		return str(json.dumps(response)).replace("\\\"","\"")

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

	def update_email(self, new_email):
		self.email = new_email;
		self.put()

	def increment_version(self):
		self.version += 1
		self.put()

class Group(ndb.Model):

	# Set the name of the list
	name = ndb.StringProperty(required=True, indexed=False)

	#The content of the grocery list
	usernames = ndb.JsonProperty(required=True, indexed=False)

	pending_usernames = ndb.JsonProperty(required=True, indexed=False)

	version = ndb.IntegerProperty(required=True, indexed=False)

	photo = ndb.StringProperty(required=True, indexed=False)

	photo_version = ndb.IntegerProperty(required=True, indexed=False)

	@staticmethod
	def create(user_key, name=""):

		#Find the user to add the key to
		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		if (name == ""):
			name = "Untilted"

		# Set the list to empty
		usernames = [user.username]
		pending_usernames = []

		# Add the new item list to the database
		group = Group(name=name, usernames=JSON.encode(usernames),
			pending_usernames=JSON.encode(pending_usernames), version=0,photo="",photo_version=0)

		group_key = (group.put()).urlsafe()
		user_groups = JSON.decode(user.groups)
		user_groups.append(group_key)
		user.groups = JSON.encode(user_groups)
		user.increment_version()
		user.put()

		return group_key

	@staticmethod
	def add_users(group_key, usernames):

		#Find the user to add the key to
		group = Group.find_by_key(group_key)
		if (group == None):
			raise Exception("Invalid group key")

		#Get the pending usernames and current usernames
		group_usernames = JSON.decode(group.usernames)
		group_pending_usernames = JSON.decode(group.pending_usernames)

		usernames = JSON.unquote(usernames)

		for username in usernames:

			#Find the user to add the key to
			user = User.find_by_username(username)
			if (user != None):

				if (user.username not in group_pending_usernames) and (user.username not in group_usernames):
					
					#Add the username to the list of pending users
					group_pending_usernames.append(user.username)

					#Add the group to the list of user groups
					user_groups = JSON.decode(user.groups)
					user_groups.append(group_key)
					user.groups = JSON.encode(user_groups)
					user.increment_version()
					user.put()


		group.pending_usernames = JSON.encode(group_pending_usernames)
		group.increment_version()
		group.put()

		#Return the currently pending users
		return str(json.dumps(group_pending_usernames)).replace("\\\"","\"")

	@staticmethod
	def accept(user_key, group_key):

		#Find the user to add the key to
		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		#Find the user to add the key to
		group = Group.find_by_key(group_key)
		if (group == None):
			raise Exception("Invalid group key")

		#Remove the user from the pending users
		group_pending_usernames = JSON.unquote(group.pending_usernames)
		group_pending_usernames.remove(user.username)
		group.pending_usernames = JSON.encode(group_pending_usernames)

		#Put the user in the confirmed users
		group_usernames = JSON.unquote(group.usernames)
		group_usernames.append(user.username)
		group.usernames = JSON.encode(group_usernames)
		group.increment_version()
		group.put()

		user.increment_version()

		return "Group request accepted"

		#return str(user.username in group.pending_usernames ) + " , " + str(group_pending_usernames)

	@staticmethod
	def deny(user_key, group_key):

		#Find the user to add the key to
		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		#Find the user to add the key to
		group = Group.find_by_key(group_key)
		if (group == None):
			raise Exception("Invalid group key")

		#Remove the user from the pending users
		group_pending_usernames = JSON.unquote(group.pending_usernames)
		group_pending_usernames.remove(user.username)
		group.pending_usernames = JSON.encode(group_pending_usernames)
		group.increment_version()
		group.put()

		#Remove the list from the users lists
		user_groups = JSON.unquote(user.groups)
		user_groups.remove(group_key)
		user.groups = JSON.encode(user_groups)
		user.increment_version()
		user.put()

		return "Group request denied"

	@staticmethod
	def leave(user_key, group_key):

		#Find the user to add the key to
		user = User.find_by_key(user_key)
		if (user == None):
			raise Exception("Invalid key")

		#Find the user to add the key to
		group = Group.find_by_key(group_key)
		if (group == None):
			raise Exception("Invalid group key")

		#Remove the user from the pending users
		group_usernames = JSON.unquote(group.usernames)
		group_usernames.remove(user.username)
		group.usernames = JSON.encode(group_usernames)
		group.increment_version()
		group.put()	

		#Remove the list from the users lists
		user_groups = JSON.unquote(user.groups)
		user_groups.remove(group_key)
		user.groups = JSON.encode(user_groups)
		user.increment_version()
		user.put()

		return "Group left"

	@staticmethod
	def edit(group_key, group_name, group_photo):

		#Find the user to add the key to
		group = Group.find_by_key(group_key)
		if (group == None):
			raise Exception("Invalid group key")

		if (group_name != ""):
			group.name = group_name
			group.increment_version()

		if (group_photo != ""):
			group.photo = group_photo
			group.photo_version += 1
			group.increment_version()

	
	def get_item_lists(self, versions):
		"""Get the grocery lists that a user has access

		Keyword arguments:
			user_key - the key of the user
			versions - the json contaning the client versions of each list

		Returns:
			The grocery lists in a json
		"""

		response = []

		item_lists = ItemList.query(ItemList.group_key==(self.put()).urlsafe())

		for item_list in item_lists:

			list_key_str = (item_list.put()).urlsafe()
			
			if ((list_key_str not in versions) or (versions[list_key_str] < item_list.version)):
				response.append( {
					"list_key" : list_key_str,
					"list_name" : item_list.name,
					"list_version" : item_list.version,
					"list_contents" : JSON.unquote(item_list.content)
				})
			else:
				response.append( {
					"list_key" : list_key_str,
					"list_name" : item_list.name,
					"list_version" : item_list.version,
					"list_contents" : ""
				})

		response = JSON.encode(response)
		return response

	@staticmethod
	def find_by_key(key):
		"""Finds the group by looking up the key

		Keyword arguments:
		    key - the group key

		Returns:
		    The group if one exists or None
		"""
		try:
			group = ndb.Key(urlsafe=key).get()
			if (group is not None):
				return group
		except:
			return None
		return None

	def increment_version(self):
		self.version += 1
		self.put()

		usernames = JSON.unquote(self.usernames)
		for username in usernames:
			user = User.find_by_username(username)
			user.increment_version()
			user.put()

		usernames = JSON.unquote(self.pending_usernames)
		for username in usernames:
			user = User.find_by_username(username)
			user.increment_version()
			user.put()

class ItemList(ndb.Model):

    # Set the name of the list
    name = ndb.StringProperty(required=True, indexed=False)

    # The group the list belongs to
    group_key = ndb.StringProperty(required=True, indexed=True)

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
    def create(group_key, name=""):
        """Creates a new list with the provided name

        Keyword arguments:
            user_key - the key of the user who is creating the list
            name - the name of the list

        Returns:
            the key of the list
        """

        #Find the user to add the key to
        group = Group.find_by_key(group_key)
        if (group == None):
            raise Exception("Invalid key")

        if (name == ""):
        	name = "Untilted"

        # Set the list to empty
        item_list_contents = []

        # Add the new item list to the database
        item_list = ItemList(name=name, group_key=group_key, content=JSON.encode(item_list_contents),version=0)
        item_list_key = (item_list.put()).urlsafe()

        group.increment_version()

        return item_list_key

    @staticmethod
    def delete(list_key):

        #Find the user to add the key to
        item_list = ItemList.find_by_key(list_key)
        if (item_list == None):
            raise Exception("Invalid list key")

        group = Group.find_by_key(item_list.group_key)

        #Figuee out a way to represent deletions
       	group.increment_version()

        item_list.key.delete()


    @staticmethod
    def update_content(list_key, changed_content):
        """Adds and deletes items from the list (see readme for more info)

        Keyword arguments:
            user_key - the string key of the user
            list_key - the string key of the grocery list
            changed_content - the items to add and delete from the list
        """

        #Find the grocery list
        item_list = ItemList.find_by_key(list_key)
        if (item_list == None):
            raise Exception("Invalid list key")

        #Get the contents of the current grocery list
        content = JSON.decode(item_list.content)
        
        for item in changed_content:
            # Remove the hidden operation code from the item
            opcode = item[ItemList.UPDATE_CONTENT_OPCODE]
            item.pop(ItemList.UPDATE_CONTENT_OPCODE)
            
            # Add the item to the list if the op code was to add it
            if (opcode == ItemList.UPDATE_CONTENT_OPCODE_ADD):
                content.append(item)
            
            # Delete the item from the list if the op code was to delete it
            elif (opcode == ItemList.UPDATE_CONTENT_OPCODE_DELETE):
                for item2 in content:
                    if ((ItemList.UPDATE_CONTENT_ID in item2) and (item2[ItemList.UPDATE_CONTENT_ID] == item[ItemList.UPDATE_CONTENT_ID])):
                        content.remove(item2)
                        break

        #Increment the version number
        item_list.increment_version()

        # Put the new list content in the database
        item_list.content = JSON.encode(content)
        item_list.put()

        return (str(json.dumps(item_list.content)).replace("\\\"","\""))[1:-1]

    @staticmethod
    def find_by_key(key):
        """Finds the grocery list by looking up the key

        Keyword arguments:
            key - the user key

        Returns:
            The user if one exists or None
        """
        try:
            item_list = ndb.Key(urlsafe=key).get()
            if (item_list is not None):
                return item_list
        except:
            return None
        return None

    def increment_version(self):
		self.version += 1
		self.put()

		group = Group.find_by_key(self.group_key)
		group.increment_version()



