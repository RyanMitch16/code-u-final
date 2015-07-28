# AppEngine API Documentation

(Everything is HTTP Get for testing reasons now put that will change to Post soon)

### How to create new users
http://code-u-final.appspot.com/user/create?email=[EMAIL_ADDRESS]

Returns the key that represents the user on sucessful completion (Status code 201)

### Changing user email
code-u-final.appspot.com/user/update?user_key=[USER_KEY]&new_email=[NEW_EMAIL]

Changes user emails to one that that's NOT already on the database

### How to create a new list
http://code-u-final.appspot.com/list/create?user_key=[USER_KEY]&list_name=[LIST_NAME]

Returns the key that represents the list on sucessful completion (Status code 201)

### How to get all the lists a user has access to
http://code-u-final.appspot.com/user/lists?user_key=[USER_KEY]

Returns the lists the user can access and their names in a JSON format

```javascript
{  
    "itemLists":[  
        {  
            "key":"bfui3478y73284yr0782y7843yb7843y99782y",
            "name":"List Title"
        },
        {  
            "key":"8ycbr789234t937t8y347tb9843ty7843byt78",
            "name":"The Other List"
        }
    ]
}
```

### How to update elements in a list
http://code-u-final.appspot.com/list/edit?user_key=[USER_KEY]&list_key=[LIST_KEY]&changed_content=[CHANGED_CONTENT]

The changed content is a JSON string that represnets what is being added to the list and deleted from the list. As an example:

```javascript
{  
    "items":[  
        {  
            "_id":"a0",
            "_op":"add",
            "item-quantity":"2",
            "name":"Pillow"
        },
        { 
            "_id":"b0",
            "_op":"add",
            "item-quantity":"1",
            "name":"Bed"
        }
    ]
}
```

When passed as the changed content, this will add two items to the item list. The **_id** and **_op** are both required fields. Any additional properties will be included in the item (name, quantity, ect.). I will elaborate on how we generate the id later, but each **_id** will be unique. The operation code specifies what we do with this item (add/delete). When set to "add" the items are added to the list. In order to delete elements, set the **_op** to "delete":

```javascript
{  
    "items":[  
        {  
            "_id":"a0",
            "_op":"delete"
        }
    ]
}
```

### How to get the list of items
http://code-u-final.appspot.com/list/get?user_key=[USER_KEY]&list_key=[LIST_KEY]

This returns the list of items in a json format:

```javascript
{  
    "items":[  
        {  
            "_id":"a0",
            "item-quantity":"2",
            "name":"Pillow"
        },
        { 
            "_id":"b0",
            "item-quantity":"1",
            "name":"Bed"
        }
    ]
}
```

### Deleting users and lists
Users: code-u-final.appspot.com/user/delete?user_key=[USER_KEY]
Lists: code-u-final.appspot.com/list/delete?list_key=[LIST_KEY]

Returns either a 400 with "This user/list does not exist" at the end, or "User/List successfully deleted"
