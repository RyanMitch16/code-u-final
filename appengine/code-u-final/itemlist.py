
from webapp2_extras import json
from google.appengine.ext import ndb

class ItemList(ndb.Model):
    """Holds the list properties and the content of list"""
    
    # Set the constants for updating the conetnt of the list
    UPDATE_CONTENT_OPCODE = "_op"
    UPDATE_CONTENT_OPCODE_ADD = "add"
    UPDATE_CONTENT_OPCODE_DELETE = "delete"
    UPDATE_CONTENT_ID = "_id"
    
    # Set the model properties
    name = ndb.StringProperty(required=True, indexed=False)
    content = ndb.JsonProperty(required=True)

    def update_content(self, changed_content):
        """Adds and deletes items from the list"""
        content = json.decode(self.content)
        
        for item in changed_content["items"]:
            # Remove the hidden operation code from the item
            opcode = item[ItemList.UPDATE_CONTENT_OPCODE]
            item.pop(ItemList.UPDATE_CONTENT_OPCODE)
            
            # Add the item to the list if the op code was to add it
            if (opcode == ItemList.UPDATE_CONTENT_OPCODE_ADD):
                content["items"].append(item)
            
            # Delete the item from the list if the op code was to delete it
            elif (opcode == ItemList.UPDATE_CONTENT_OPCODE_DELETE):
                for item2 in content["items"]:
                    if ((ItemList.UPDATE_CONTENT_ID in item2) and (item2[ItemList.UPDATE_CONTENT_ID] == item[ItemList.UPDATE_CONTENT_ID])):
                        content["items"].remove(item2)
                        break

        # Put the new list content in the database
        self.content = json.encode(content)
        self.put()

