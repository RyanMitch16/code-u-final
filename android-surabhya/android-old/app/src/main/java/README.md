Android:

###How to create new users
http://code-u-final.appspot.com/user/create?email=[EMAIL_ADDRESS]
http://code-u-final.appspot.com/user/create?email=surabhya.aryal@gmail.com
USER_KEY: ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw

###How to create a new list
http://code-u-final.appspot.com/list/create?user_key=[USER_KEY]&list_name=[LIST_NAME]

http://code-u-final.appspot.com/list/create?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_name=surabhyalist1
LIST_KEY : ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgICAgAsM

http://code-u-final.appspot.com/list/create?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_name=surabhyalist2
LIST_KEY : ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgNrjhgsM

http://code-u-final.appspot.com/list/create?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_name=surabhyalist3
LISST_KEY : ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgPjtnQkM

###How to get all the lists a user has access to
http://code-u-final.appspot.com/user/lists?user_key=[USER_KEY]

http://code-u-final.appspot.com/user/lists?user_key=ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw

###How to get the list of items
http://code-u-final.appspot.com/list/get?user_key=[USER_KEY]&list_key=[LIST_KEY]

http://code-u-final.appspot.com/list/get?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_key=ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgICAgAsM

http://code-u-final.appspot.com/list/get?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_key=ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgNrjhgsM

http://code-u-final.appspot.com/list/get?user_key= ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw
&list_key=ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgPjtnQkM

###How to update elements in a list ———— NOT WORKING
http://code-u-final.appspot.com/list/edit?user_key=[USER_KEY]&list_key=[LIST_KEY]&changed_content=[CHANGED_CONTENT]

http://code-u-final.appspot.com/list/edit?user_key=ag5zfmNvZGUtdS1maW5hbHIRCxIEVXNlchiAgICAr8iACgw&list_key=ag5zfmNvZGUtdS1maW5hbHIVCxIISXRlbUxpc3QYgICAgICAgAsM&changed_content={"items":[{"_id":"a0","_op":"add","item-quantity":"3","name":"Coke"}]}



