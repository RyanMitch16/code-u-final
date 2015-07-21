#AppEngine API Documentation

(Everything is HTTP Get for testing reasons now put that will change to Post soon)

###How to create new users
http://code-u-final.appspot.com/user/create?email=[EMAIL_ADDRESS]

Returns the key that represents the user on sucessful completion (Status code 201)

###How to create a new list
http://code-u-final.appspot.com/list/create?user_key=[USER_KEY]&list_name=[LIST_NAME]

Returns the key that represents the list on sucessful completion (Status code 201)

###How to update elements in a list
http://code-u-final.appspot.com/list/edit?user_key=[USER_KEY]&list_name=[LIST_NAME]&content=[CHANGED_CONTENT]

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
            "_op":"delete",
        },
    ]
}
```

###How to get the list of items
http://code-u-final.appspot.com/list/get?user_key=[USER_KEY]&list_name=[LIST_NAME]

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


    
