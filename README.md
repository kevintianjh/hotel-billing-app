# hotel-billing-app
A command line billing application written in Java that demonstrated clean design, JDBC connectivity and multi-threading

Enhancements on top of the original specficications
1) The menu items originally was hardcoded, I organised the menu items into a array of MenuItem objects and printed them dynamically. So that it is easier to change the menu

2) All inputs are validated

3) There is a second thread running in the background to periodically save the unfinalized receipt into the database. So if the application got terminated suddenly or user choose to exit, the state can be later retrieved back using the same customer name
