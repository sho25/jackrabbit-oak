begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|api
operator|.
name|JackrabbitSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|tree
operator|.
name|TreeLocation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|namespace
operator|.
name|NamespaceConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|nodetype
operator|.
name|NodeTypeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|version
operator|.
name|VersionConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Provides constants for permissions used in the OAK access evaluation as well  * as permission related utility methods.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Permissions
block|{
specifier|private
name|Permissions
parameter_list|()
block|{     }
specifier|public
specifier|static
specifier|final
name|long
name|NO_PERMISSION
init|=
literal|0
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|READ_NODE
init|=
literal|1
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|READ_PROPERTY
init|=
name|READ_NODE
operator|<<
literal|1
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|ADD_PROPERTY
init|=
name|READ_PROPERTY
operator|<<
literal|1
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|MODIFY_PROPERTY
init|=
name|ADD_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|REMOVE_PROPERTY
init|=
name|MODIFY_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|ADD_NODE
init|=
name|REMOVE_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|REMOVE_NODE
init|=
name|ADD_NODE
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|READ_ACCESS_CONTROL
init|=
name|REMOVE_NODE
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|MODIFY_ACCESS_CONTROL
init|=
name|READ_ACCESS_CONTROL
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|NODE_TYPE_MANAGEMENT
init|=
name|MODIFY_ACCESS_CONTROL
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|VERSION_MANAGEMENT
init|=
name|NODE_TYPE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|LOCK_MANAGEMENT
init|=
name|VERSION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|LIFECYCLE_MANAGEMENT
init|=
name|LOCK_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|RETENTION_MANAGEMENT
init|=
name|LIFECYCLE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|MODIFY_CHILD_NODE_COLLECTION
init|=
name|RETENTION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|NODE_TYPE_DEFINITION_MANAGEMENT
init|=
name|MODIFY_CHILD_NODE_COLLECTION
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|NAMESPACE_MANAGEMENT
init|=
name|NODE_TYPE_DEFINITION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|WORKSPACE_MANAGEMENT
init|=
name|NAMESPACE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|PRIVILEGE_MANAGEMENT
init|=
name|WORKSPACE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|USER_MANAGEMENT
init|=
name|PRIVILEGE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|INDEX_DEFINITION_MANAGEMENT
init|=
name|USER_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|READ
init|=
name|READ_NODE
operator||
name|READ_PROPERTY
decl_stmt|;
comment|/**      * @since OAK 1.0      */
specifier|public
specifier|static
specifier|final
name|long
name|REMOVE
init|=
name|REMOVE_NODE
operator||
name|REMOVE_PROPERTY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|SET_PROPERTY
init|=
name|ADD_PROPERTY
operator||
name|MODIFY_PROPERTY
operator||
name|REMOVE_PROPERTY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|WRITE
init|=
name|ADD_NODE
operator||
name|REMOVE_NODE
operator||
name|SET_PROPERTY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|ALL
init|=
operator|(
name|READ
operator||
name|SET_PROPERTY
operator||
name|ADD_NODE
operator||
name|REMOVE_NODE
operator||
name|READ_ACCESS_CONTROL
operator||
name|MODIFY_ACCESS_CONTROL
operator||
name|NODE_TYPE_MANAGEMENT
operator||
name|VERSION_MANAGEMENT
operator||
name|LOCK_MANAGEMENT
operator||
name|LIFECYCLE_MANAGEMENT
operator||
name|RETENTION_MANAGEMENT
operator||
name|MODIFY_CHILD_NODE_COLLECTION
operator||
name|NODE_TYPE_DEFINITION_MANAGEMENT
operator||
name|NAMESPACE_MANAGEMENT
operator||
name|WORKSPACE_MANAGEMENT
operator||
name|PRIVILEGE_MANAGEMENT
operator||
name|USER_MANAGEMENT
operator||
name|INDEX_DEFINITION_MANAGEMENT
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Long
argument_list|>
name|NON_AGGREGATES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|READ_NODE
argument_list|,
name|READ_PROPERTY
argument_list|,
name|ADD_PROPERTY
argument_list|,
name|MODIFY_PROPERTY
argument_list|,
name|REMOVE_PROPERTY
argument_list|,
name|ADD_NODE
argument_list|,
name|REMOVE_NODE
argument_list|,
name|MODIFY_CHILD_NODE_COLLECTION
argument_list|,
name|READ_ACCESS_CONTROL
argument_list|,
name|MODIFY_ACCESS_CONTROL
argument_list|,
name|NODE_TYPE_MANAGEMENT
argument_list|,
name|VERSION_MANAGEMENT
argument_list|,
name|LOCK_MANAGEMENT
argument_list|,
name|LIFECYCLE_MANAGEMENT
argument_list|,
name|RETENTION_MANAGEMENT
argument_list|,
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|,
name|NAMESPACE_MANAGEMENT
argument_list|,
name|WORKSPACE_MANAGEMENT
argument_list|,
name|PRIVILEGE_MANAGEMENT
argument_list|,
name|USER_MANAGEMENT
argument_list|,
name|INDEX_DEFINITION_MANAGEMENT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|PERMISSION_NAMES
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|ALL
argument_list|,
literal|"ALL"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|READ
argument_list|,
literal|"READ"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|READ_NODE
argument_list|,
literal|"READ_NODE"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|READ_PROPERTY
argument_list|,
literal|"READ_PROPERTY"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|SET_PROPERTY
argument_list|,
literal|"SET_PROPERTY"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|ADD_PROPERTY
argument_list|,
literal|"ADD_PROPERTY"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|MODIFY_PROPERTY
argument_list|,
literal|"MODIFY_PROPERTY"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|REMOVE_PROPERTY
argument_list|,
literal|"REMOVE_PROPERTY"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|ADD_NODE
argument_list|,
literal|"ADD_NODE"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|REMOVE_NODE
argument_list|,
literal|"REMOVE_NODE"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|REMOVE
argument_list|,
literal|"REMOVE"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|WRITE
argument_list|,
literal|"WRITE"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|MODIFY_CHILD_NODE_COLLECTION
argument_list|,
literal|"MODIFY_CHILD_NODE_COLLECTION"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|READ_ACCESS_CONTROL
argument_list|,
literal|"READ_ACCESS_CONTROL"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|MODIFY_ACCESS_CONTROL
argument_list|,
literal|"MODIFY_ACCESS_CONTROL"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|NODE_TYPE_MANAGEMENT
argument_list|,
literal|"NODE_TYPE_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|VERSION_MANAGEMENT
argument_list|,
literal|"VERSION_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|LOCK_MANAGEMENT
argument_list|,
literal|"LOCK_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|LIFECYCLE_MANAGEMENT
argument_list|,
literal|"LIFECYCLE_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|RETENTION_MANAGEMENT
argument_list|,
literal|"RETENTION_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|,
literal|"NODE_TYPE_DEFINITION_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|NAMESPACE_MANAGEMENT
argument_list|,
literal|"NAMESPACE_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|WORKSPACE_MANAGEMENT
argument_list|,
literal|"WORKSPACE_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|PRIVILEGE_MANAGEMENT
argument_list|,
literal|"PRIVILEGE_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|USER_MANAGEMENT
argument_list|,
literal|"USER_MANAGEMENT"
argument_list|)
expr_stmt|;
name|PERMISSION_NAMES
operator|.
name|put
argument_list|(
name|INDEX_DEFINITION_MANAGEMENT
argument_list|,
literal|"INDEX_DEFINITION_MANAGEMENT"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|PERMISSION_LOOKUP
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"ALL"
argument_list|,
name|ALL
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"READ"
argument_list|,
name|READ
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"READ_NODE"
argument_list|,
name|READ_NODE
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"READ_PROPERTY"
argument_list|,
name|READ_PROPERTY
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"SET_PROPERTY"
argument_list|,
name|SET_PROPERTY
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"ADD_PROPERTY"
argument_list|,
name|ADD_PROPERTY
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"MODIFY_PROPERTY"
argument_list|,
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"REMOVE_PROPERTY"
argument_list|,
name|REMOVE_PROPERTY
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"ADD_NODE"
argument_list|,
name|ADD_NODE
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"REMOVE_NODE"
argument_list|,
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"REMOVE"
argument_list|,
name|REMOVE
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"WRITE"
argument_list|,
name|WRITE
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"MODIFY_CHILD_NODE_COLLECTION"
argument_list|,
name|MODIFY_CHILD_NODE_COLLECTION
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"READ_ACCESS_CONTROL"
argument_list|,
name|READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"MODIFY_ACCESS_CONTROL"
argument_list|,
name|MODIFY_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"NODE_TYPE_MANAGEMENT"
argument_list|,
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"VERSION_MANAGEMENT"
argument_list|,
name|VERSION_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"LOCK_MANAGEMENT"
argument_list|,
name|LOCK_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"LIFECYCLE_MANAGEMENT"
argument_list|,
name|LIFECYCLE_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"RETENTION_MANAGEMENT"
argument_list|,
name|RETENTION_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"NODE_TYPE_DEFINITION_MANAGEMENT"
argument_list|,
name|NODE_TYPE_DEFINITION_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"NAMESPACE_MANAGEMENT"
argument_list|,
name|NAMESPACE_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"WORKSPACE_MANAGEMENT"
argument_list|,
name|WORKSPACE_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"PRIVILEGE_MANAGEMENT"
argument_list|,
name|PRIVILEGE_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"USER_MANAGEMENT"
argument_list|,
name|USER_MANAGEMENT
argument_list|)
expr_stmt|;
name|PERMISSION_LOOKUP
operator|.
name|put
argument_list|(
literal|"INDEX_DEFINITION_MANAGEMENT"
argument_list|,
name|INDEX_DEFINITION_MANAGEMENT
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|WRITE_ACTIONS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|Session
operator|.
name|ACTION_REMOVE
argument_list|,
name|Session
operator|.
name|ACTION_ADD_NODE
argument_list|,
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_NODE
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_ADD_PROPERTY
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_MODIFY_PROPERTY
argument_list|,
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_PROPERTY
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|ACTIONS_MAP
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|Session
operator|.
name|ACTION_ADD_NODE
argument_list|,
name|ADD_NODE
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_ADD_PROPERTY
argument_list|,
name|ADD_PROPERTY
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_MODIFY_PROPERTY
argument_list|,
name|MODIFY_PROPERTY
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_PROPERTY
argument_list|,
name|REMOVE_PROPERTY
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_REMOVE_NODE
argument_list|,
name|REMOVE_NODE
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_NODE_TYPE_MANAGEMENT
argument_list|,
name|NODE_TYPE_MANAGEMENT
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_LOCKING
argument_list|,
name|LOCK_MANAGEMENT
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_VERSIONING
argument_list|,
name|VERSION_MANAGEMENT
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_READ_ACCESS_CONTROL
argument_list|,
name|READ_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_MODIFY_ACCESS_CONTROL
argument_list|,
name|MODIFY_ACCESS_CONTROL
argument_list|)
expr_stmt|;
name|ACTIONS_MAP
operator|.
name|put
argument_list|(
name|JackrabbitSession
operator|.
name|ACTION_USER_MANAGEMENT
argument_list|,
name|USER_MANAGEMENT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns names of the specified permissions.      *      * @param permissions The permissions for which the string representation      * should be collected.      * @return The names of the given permissions.      */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getNames
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|PERMISSION_NAMES
operator|.
name|containsKey
argument_list|(
name|permissions
argument_list|)
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|PERMISSION_NAMES
operator|.
name|get
argument_list|(
name|permissions
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|PERMISSION_NAMES
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|permissions
operator|&
name|key
operator|)
operator|==
name|key
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|names
return|;
block|}
block|}
comment|/**      * Returns the names of the specified permissions separated by ','.      *      * @param permissions The permissions for which the string representation      * should be collected.      * @return The names of the given permissions separated by ',' such      * that i can be passed to {@link Session#hasPermission(String, String)}      * and {@link Session#checkPermission(String, String)}.      */
specifier|public
specifier|static
name|String
name|getString
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|PERMISSION_NAMES
operator|.
name|containsKey
argument_list|(
name|permissions
argument_list|)
condition|)
block|{
return|return
name|PERMISSION_NAMES
operator|.
name|get
argument_list|(
name|permissions
argument_list|)
return|;
block|}
else|else
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|PERMISSION_NAMES
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|permissions
operator|&
name|key
operator|)
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isRepositoryPermission
parameter_list|(
name|long
name|permission
parameter_list|)
block|{
return|return
name|permission
operator|==
name|NAMESPACE_MANAGEMENT
operator|||
name|permission
operator|==
name|NODE_TYPE_DEFINITION_MANAGEMENT
operator|||
name|permission
operator|==
name|PRIVILEGE_MANAGEMENT
operator|||
name|permission
operator|==
name|WORKSPACE_MANAGEMENT
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isAggregate
parameter_list|(
name|long
name|permission
parameter_list|)
block|{
return|return
name|permission
operator|>
name|NO_PERMISSION
operator|&&
operator|!
name|NON_AGGREGATES
operator|.
name|contains
argument_list|(
name|permission
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Long
argument_list|>
name|aggregates
parameter_list|(
specifier|final
name|long
name|permissions
parameter_list|)
block|{
if|if
condition|(
name|ALL
operator|==
name|permissions
condition|)
block|{
return|return
name|NON_AGGREGATES
return|;
block|}
else|else
block|{
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|NON_AGGREGATES
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Long
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Long
name|permission
parameter_list|)
block|{
return|return
name|permission
operator|!=
literal|null
operator|&&
name|includes
argument_list|(
name|permissions
argument_list|,
name|permission
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|includes
parameter_list|(
name|long
name|permissions
parameter_list|,
name|long
name|permissionsToTest
parameter_list|)
block|{
return|return
operator|(
name|permissions
operator|&
name|permissionsToTest
operator|)
operator|==
name|permissionsToTest
return|;
block|}
specifier|public
specifier|static
name|boolean
name|respectParentPermissions
parameter_list|(
name|long
name|permissions
parameter_list|)
block|{
return|return
name|Permissions
operator|.
name|includes
argument_list|(
name|permissions
argument_list|,
name|Permissions
operator|.
name|ADD_NODE
argument_list|)
operator|||
name|Permissions
operator|.
name|includes
argument_list|(
name|permissions
argument_list|,
name|Permissions
operator|.
name|REMOVE_NODE
argument_list|)
return|;
block|}
comment|/**      * Returns those bits from {@code permissions} that are not present in      * the {@code otherPermissions}, i.e. subtracts the other permissions      * from permissions.<br>      * If the specified {@code otherPermissions} do not intersect with      * {@code permissions},  {@code permissions} are returned.<br>      * If {@code permissions} is included in {@code otherPermissions},      * {@link #NO_PERMISSION} is returned.      *      * @param permissions      * @param otherPermissions      * @return the differences of the 2 permissions or {@link #NO_PERMISSION}.      */
specifier|public
specifier|static
name|long
name|diff
parameter_list|(
name|long
name|permissions
parameter_list|,
name|long
name|otherPermissions
parameter_list|)
block|{
return|return
name|permissions
operator|&
operator|~
name|otherPermissions
return|;
block|}
comment|/**      * Returns the permissions that correspond the given jcr actions such as      * specified in {@link Session#hasPermission(String, String)}. Note that      * in addition to the regular JCR actions ({@link Session#ACTION_READ},      * {@link Session#ACTION_ADD_NODE}, {@link Session#ACTION_REMOVE} and      * {@link Session#ACTION_SET_PROPERTY}) the string may also contain      * the names of all permissions defined by this class.      *      * @param jcrActions A comma separated string of JCR actions and permission      * names.      * @param location The tree location for which the permissions should be      * calculated.      * @param isAccessControlContent Flag to mark the given location as access      * control content.      * @return The permissions.      * @throws IllegalArgumentException If the string contains unknown actions      * or permission names.      */
specifier|public
specifier|static
name|long
name|getPermissions
parameter_list|(
annotation|@
name|NotNull
name|String
name|jcrActions
parameter_list|,
annotation|@
name|NotNull
name|TreeLocation
name|location
parameter_list|,
name|boolean
name|isAccessControlContent
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|actions
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Text
operator|.
name|explode
argument_list|(
name|jcrActions
argument_list|,
literal|','
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|permissions
init|=
name|NO_PERMISSION
decl_stmt|;
comment|// map read action respecting the 'isAccessControlContent' flag.
if|if
condition|(
name|actions
operator|.
name|remove
argument_list|(
name|Session
operator|.
name|ACTION_READ
argument_list|)
condition|)
block|{
if|if
condition|(
name|isAccessControlContent
condition|)
block|{
name|permissions
operator||=
name|READ_ACCESS_CONTROL
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|location
operator|.
name|exists
argument_list|()
condition|)
block|{
name|permissions
operator||=
name|READ
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|location
operator|.
name|getProperty
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|permissions
operator||=
name|READ_PROPERTY
expr_stmt|;
block|}
else|else
block|{
name|permissions
operator||=
name|READ_NODE
expr_stmt|;
block|}
block|}
comment|// map write actions respecting the 'isAccessControlContent' flag.
if|if
condition|(
operator|!
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|isAccessControlContent
condition|)
block|{
if|if
condition|(
name|actions
operator|.
name|removeAll
argument_list|(
name|WRITE_ACTIONS
argument_list|)
condition|)
block|{
name|permissions
operator||=
name|MODIFY_ACCESS_CONTROL
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// item is not access controlled -> cover actions that don't have
comment|// a 1:1 mapping to a given permission.
if|if
condition|(
name|actions
operator|.
name|remove
argument_list|(
name|Session
operator|.
name|ACTION_SET_PROPERTY
argument_list|)
condition|)
block|{
if|if
condition|(
name|location
operator|.
name|getProperty
argument_list|()
operator|==
literal|null
condition|)
block|{
name|permissions
operator||=
name|ADD_PROPERTY
expr_stmt|;
block|}
else|else
block|{
name|permissions
operator||=
name|MODIFY_PROPERTY
expr_stmt|;
block|}
block|}
if|if
condition|(
name|actions
operator|.
name|remove
argument_list|(
name|Session
operator|.
name|ACTION_REMOVE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|location
operator|.
name|exists
argument_list|()
condition|)
block|{
name|permissions
operator||=
name|REMOVE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|location
operator|.
name|getProperty
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|permissions
operator||=
name|REMOVE_PROPERTY
expr_stmt|;
block|}
else|else
block|{
name|permissions
operator||=
name|REMOVE_NODE
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// map remaining actions and permission-names that have a simple 1:1
comment|// mapping between action and permission
if|if
condition|(
operator|!
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|actionEntry
range|:
name|ACTIONS_MAP
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|actions
operator|.
name|remove
argument_list|(
name|actionEntry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|permissions
operator||=
name|actionEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
name|permissions
operator||=
name|getPermissions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
comment|// now the action set must be empty; otherwise it contained unsupported action(s)
if|if
condition|(
operator|!
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown actions: "
operator|+
name|actions
argument_list|)
throw|;
block|}
return|return
name|permissions
return|;
block|}
comment|/**      * Returns the permissions that correspond the given permission names.      *      * @param permissionNames A comma separated string of permission names.      * @return The permissions.      * @throws IllegalArgumentException If the string contains unknown actions      * or permission names.      */
specifier|public
specifier|static
name|long
name|getPermissions
parameter_list|(
annotation|@
name|Nullable
name|String
name|permissionNames
parameter_list|)
block|{
if|if
condition|(
name|permissionNames
operator|==
literal|null
operator|||
name|permissionNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|NO_PERMISSION
return|;
block|}
else|else
block|{
return|return
name|getPermissions
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|permissionNames
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|long
name|getPermissions
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|permissionNames
parameter_list|)
block|{
name|long
name|permissions
init|=
name|NO_PERMISSION
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|permissionNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|PERMISSION_LOOKUP
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|permissions
operator||=
name|PERMISSION_LOOKUP
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|permissions
return|;
block|}
specifier|public
specifier|static
name|long
name|getPermission
parameter_list|(
annotation|@
name|Nullable
name|String
name|path
parameter_list|,
name|long
name|defaultPermission
parameter_list|)
block|{
name|long
name|permission
decl_stmt|;
if|if
condition|(
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NAMESPACE_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|NODE_TYPE_DEFINITION_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|VersionConstants
operator|.
name|SYSTEM_PATHS
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|VERSION_MANAGEMENT
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|permission
operator|=
name|Permissions
operator|.
name|PRIVILEGE_MANAGEMENT
expr_stmt|;
block|}
else|else
block|{
comment|// FIXME: workspace-mgt (blocked by OAK-916)
name|permission
operator|=
name|defaultPermission
expr_stmt|;
block|}
return|return
name|permission
return|;
block|}
block|}
end_class

end_unit

