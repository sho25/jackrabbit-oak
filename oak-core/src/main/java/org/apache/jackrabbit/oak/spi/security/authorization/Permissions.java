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
package|;
end_package

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

begin_comment
comment|/**  * Permissions... TODO  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Permissions
block|{
specifier|public
specifier|static
specifier|final
name|int
name|NO_PERMISSION
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|READ_NODE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|READ_PROPERTY
init|=
name|READ_NODE
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ADD_PROPERTY
init|=
name|READ_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MODIFY_PROPERTY
init|=
name|ADD_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|REMOVE_PROPERTY
init|=
name|MODIFY_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ADD_NODE
init|=
name|REMOVE_PROPERTY
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|REMOVE_NODE
init|=
name|ADD_NODE
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|READ_ACCESS_CONTROL
init|=
name|REMOVE_NODE
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MODIFY_ACCESS_CONTROL
init|=
name|READ_ACCESS_CONTROL
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NODE_TYPE_MANAGEMENT
init|=
name|MODIFY_ACCESS_CONTROL
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_MANAGEMENT
init|=
name|NODE_TYPE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|LOCK_MANAGEMENT
init|=
name|VERSION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|LIFECYCLE_MANAGEMENT
init|=
name|LOCK_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|RETENTION_MANAGEMENT
init|=
name|LIFECYCLE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MODIFY_CHILD_NODE_COLLECTION
init|=
name|RETENTION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NODE_TYPE_DEFINITION_MANAGEMENT
init|=
name|RETENTION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NAMESPACE_MANAGEMENT
init|=
name|NODE_TYPE_DEFINITION_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WORKSPACE_MANAGEMENT
init|=
name|NAMESPACE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|PRIVILEGE_MANAGEMENT
init|=
name|WORKSPACE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|USER_MANAGEMENT
init|=
name|PRIVILEGE_MANAGEMENT
operator|<<
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|READ
init|=
name|READ_NODE
operator||
name|READ_PROPERTY
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ALL
init|=
operator|(
name|READ
operator||
name|ADD_PROPERTY
operator||
name|MODIFY_PROPERTY
operator||
name|REMOVE_PROPERTY
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
operator|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
name|PERMISSION_NAMES
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
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
block|}
specifier|public
name|String
name|getString
parameter_list|(
name|int
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
name|sb
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|key
range|:
name|PERMISSION_NAMES
operator|.
name|keySet
argument_list|()
control|)
block|{
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
name|sb
operator|.
name|append
argument_list|(
name|PERMISSION_NAMES
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'|'
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
block|}
end_class

end_unit

