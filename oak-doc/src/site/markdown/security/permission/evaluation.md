<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

Permission Evaluation in Detail
--------------------------------------------------------------------------------

### General Remarks

As of Oak 1.0 Permission evaluation is completely separated from the access control
content and is executed based on the information stored in the permission store.
The latter is kept in sync with the access control information using dedicated
`CommitHook` implementation ([PermissionHook]). The evaluation itself is done
by the configured `PermissionProvider` that read and evaluates the information
stored in the permission store.

Each JCR `Session` (or Oak `ContentSession`) gets it's own `PermissionProvider`
associated with the current repository revision the session is operating on.
Consequently, the evaluated permissions and caches are not shared between different
sessions even if they represent the same subject.

#### Evaluation of Permission Entries

_todo_


### Overview on Permission Evaluation

#### <a name="permissionStore"></a> The Permission Store

  _todo_

#### <a name="PermissionProvider"></a> PermissionProvider

  _todo_

#### <a name="SecureNodeBuilder"></a> SecureNodeBuilder

  _todo_

#### <a name="getTreePermission"></a> TreePermission

  _todo_


#### <a name="getEntryIterator"></a> PermissionEntry Iterator

  _todo_



#### Reading a Node : Step by Step

The following section describes what happens on `session.getNode("/foo").getProperty("jcr:title")`
in terms of permission evaluation:

  1. `SessionImpl.getNode()` internally calls `SessionDelegate.getNode()`
     which calls `Root.getTree()` which calls `Tree.getTree()` on the `/foo` tree.
     This creates a bunch of linked `MutableTree` objects.

  1. The session delegate then checks if the tree really exists, by calling `Tree.exists()`
     which then calls `NodeBuilder.exists()`.

  1. If the session performing the operation is an _admin_ session, then the node builder from
     the persistence layer is directly used. In all other cases, the original node builder
     is wrapped by a `SecureNodeBuilder`. The `SecureNodeBuilder` performs permission
     checks before delegating the calls to the delegated builder.

  1. For non _admin_ sessions the `SecureNodeBuilder` fetches its _tree permissions_ via
     `getTreePermission()` (See [below](#getTreePermission) of how this works) and then
     calls `TreePermission.canRead()`. This method (signature with no arguments) checks the
     `READ_NODE` permission for normal trees (as in this example) or the `READ_ACCESS_CONTROL`
     permission on _AC trees_ [^1] and remembers the result in the `ReadStatus`.

     For that an iterator of the _permission entries_ is [retrieved](#getEntryIterator) which
     provides all the relevant permission entries needed to be evaluated for this tree (and
     and set of principals associated with the permission provider).

  1. The _permission entries_ are analyzed if they include the respective permission and if so,
     the read status is set accordingly. Note that the sequence of the permission entries from
     the iterator is already in the correct order for this kind of evaluation. this is ensured
     by the way how they are stored in the [permission store](#permissionStore) and how they
     are feed into the iterator.

     The iteration also detects if the evaluated permission entries cover _this_ node and all
     its properties. If this is the case, subsequent calls that evaluate the property read
     permissions would then not need to do the same iteration again. In order to detect this,
     the iteration checks if a non-matching permission entry or privilege was skipped
     and eventually sets the respective flag in the `ReadStatus`. This flag indicates if the
     present permission entries are sufficient to tell if the session is allowed to read
     _this_ node and all its properties. If there are more entries present than the ones needed
     for evaluating the `READ_NODE` permission, then it's ambiguous to determine if all
     properties can be read.

  1. Once the `ReadStatus` is calculated (or was calculated earlier) the `canRead()` method
     returns `ReadStatus.allowsThis()` which specifies if _this_ node is allowed to be read.

  [^1]: by default access control content is stored in the `rep:policy` subtree of an access controlled node.

#### Reading an Property : Step by Step

  1. _todo_

#### Adding a Node : Step by Step
_todo_
#### Changing a Property : Step by Step
_todo_
#### Locking a Node : Step by Step
_todo_
#### Registering a Node Type : Step by Step
_todo_


<!-- hidden references -->