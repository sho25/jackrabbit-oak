The Oak Security Layer
======================

Internals of Permission Evaluation
----------------------------------

### What happens on `session.getNode("/foo").getProperty("jar:title").getString()` in respect to access control?

  1. `SessionImpl.getNode()` internally calls `SessionDelegate.getNode()` 
     which calls `Root.getTree()` which calls `Tree.getTree()` on the root tree. 
     This creates a bunch of linked `MutableTree` objects.
    
  1. The session delegate then checks if the tree really exists, by calling `Tree.exists()`
     which then calls `NodeBuilder.exists()`.

  1. If the session performing the operation is an _admin_ session, then the node builder from
     the persistence layer is directly used. In all other cases, the original node builder 
     is wrapped by a `SecureNodeBuilder`. The `SecureNodeBuilder` performs access control
     checks before delegating the calls to the delegated builder.
    
  1. For non _admin_ sessions the `SecureNodeBuilder` fetches its _tree permissions_ via
     `getTreePermissions()` (See [below](#getTreePermissions) of how this works) and then
     calls `TreePermission.canRead()`. This method (signature with no arguments) checks the 
     `READ_NODE` permission for normal trees (as in this example) or the `READ_ACCESS_CONTROL`
     permission on _AC trees_ [^1] and stores the result in the `ReadStatus`.
     
     For that an iterator of the _permission entries_ is [retrieved](#getEntrtyIterator) which
     provides all the relevant permission entries that need to be evaluated for this tree (and
     _subject_). 
     
  1. The _permission entries_ are analyzed if they include the respective permission and if so,
     the read status is set accordingly. Note that the sequence of the permission entries from
     the iterator is already in the correct order for this kind of evaluation. this is ensured
     by the way how they are stored in the [permission store](#permissionStore) and how they
     are feed into the iterator.
     
  1. and then..... (WIP)   
	   
  [^1]: AC trees are usually the `rep:policy` subtrees of access controlled nodes.


### A Shortcut for evaluating read access: _readable tree configuration_
  1. ....
  

### <a name="getTreePermissions"></a> How does the `SecureNodeBuilder` obtain his _tree permissions_ ?

  1. ...
    

### <a name="getEntryIterator"></a> How does the `TreePermission` obtain the permission entry iterator?

  1. ...
  
### <a name="permissionStore"></a> How are the access control entries preprocessed and stored in the permission store?

  1. ....

