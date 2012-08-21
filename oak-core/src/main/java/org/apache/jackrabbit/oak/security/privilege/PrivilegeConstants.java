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
name|security
operator|.
name|privilege
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
import|;
end_import

begin_comment
comment|/**  * PrivilegeConstants... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeConstants
block|{
comment|// constants for privilege serialization
name|String
name|REP_PRIVILEGES
init|=
literal|"rep:privileges"
decl_stmt|;
name|String
name|PRIVILEGES_PATH
init|=
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|'/'
operator|+
name|REP_PRIVILEGES
decl_stmt|;
name|String
name|NT_REP_PRIVILEGES
init|=
literal|"rep:Privileges"
decl_stmt|;
name|String
name|NT_REP_PRIVILEGE
init|=
literal|"rep:Privilege"
decl_stmt|;
name|String
name|REP_IS_ABSTRACT
init|=
literal|"rep:isAbstract"
decl_stmt|;
name|String
name|REP_AGGREGATES
init|=
literal|"rep:aggregates"
decl_stmt|;
comment|// Constants for privilege names
name|String
name|JCR_READ
init|=
literal|"jcr:read"
decl_stmt|;
name|String
name|JCR_MODIFY_PROPERTIES
init|=
literal|"jcr:modifyProperties"
decl_stmt|;
name|String
name|JCR_ADD_CHILD_NODES
init|=
literal|"jcr:addChildNodes"
decl_stmt|;
name|String
name|JCR_REMOVE_NODE
init|=
literal|"jcr:removeNode"
decl_stmt|;
name|String
name|JCR_REMOVE_CHILD_NODES
init|=
literal|"jcr:removeChildNodes"
decl_stmt|;
name|String
name|JCR_WRITE
init|=
literal|"jcr:write"
decl_stmt|;
name|String
name|JCR_READ_ACCESS_CONTROL
init|=
literal|"jcr:readAccessControl"
decl_stmt|;
name|String
name|JCR_MODIFY_ACCESS_CONTROL
init|=
literal|"jcr:modifyAccessControl"
decl_stmt|;
name|String
name|JCR_LOCK_MANAGEMENT
init|=
literal|"jcr:lockManagement"
decl_stmt|;
name|String
name|JCR_VERSION_MANAGEMENT
init|=
literal|"jcr:versionManagement"
decl_stmt|;
name|String
name|JCR_NODE_TYPE_MANAGEMENT
init|=
literal|"jcr:nodeTypeManagement"
decl_stmt|;
name|String
name|JCR_RETENTION_MANAGEMENT
init|=
literal|"jcr:retentionManagement"
decl_stmt|;
name|String
name|JCR_LIFECYCLE_MANAGEMENT
init|=
literal|"jcr:lifecycleManagement"
decl_stmt|;
name|String
name|JCR_WORKSPACE_MANAGEMENT
init|=
literal|"jcr:workspaceManagement"
decl_stmt|;
name|String
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
init|=
literal|"jcr:nodeTypeDefinitionManagement"
decl_stmt|;
name|String
name|JCR_NAMESPACE_MANAGEMENT
init|=
literal|"jcr:namespaceManagement"
decl_stmt|;
name|String
name|JCR_ALL
init|=
literal|"jcr:all"
decl_stmt|;
name|String
name|REP_PRIVILEGE_MANAGEMENT
init|=
literal|"rep:privilegeManagement"
decl_stmt|;
name|String
name|REP_WRITE
init|=
literal|"rep:write"
decl_stmt|;
name|String
name|REP_ADD_PROPERTIES
init|=
literal|"rep:addProperties"
decl_stmt|;
name|String
name|REP_ALTER_PROPERTIES
init|=
literal|"rep:alterProperties"
decl_stmt|;
name|String
name|REP_REMOVE_PROPERTIES
init|=
literal|"rep:removeProperties"
decl_stmt|;
name|String
index|[]
name|NON_AGGR_PRIVILEGES
init|=
operator|new
name|String
index|[]
block|{
name|JCR_READ
block|,
name|REP_ADD_PROPERTIES
block|,
name|REP_ALTER_PROPERTIES
block|,
name|REP_REMOVE_PROPERTIES
block|,
name|JCR_ADD_CHILD_NODES
block|,
name|JCR_REMOVE_CHILD_NODES
block|,
name|JCR_REMOVE_NODE
block|,
name|JCR_READ_ACCESS_CONTROL
block|,
name|JCR_MODIFY_ACCESS_CONTROL
block|,
name|JCR_NODE_TYPE_MANAGEMENT
block|,
name|JCR_VERSION_MANAGEMENT
block|,
name|JCR_LOCK_MANAGEMENT
block|,
name|JCR_LIFECYCLE_MANAGEMENT
block|,
name|JCR_RETENTION_MANAGEMENT
block|,
name|JCR_WORKSPACE_MANAGEMENT
block|,
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
block|,
name|JCR_NAMESPACE_MANAGEMENT
block|,
name|REP_PRIVILEGE_MANAGEMENT
block|}
decl_stmt|;
name|String
index|[]
name|AGGR_PRIVILEGES
init|=
operator|new
name|String
index|[]
block|{
name|JCR_MODIFY_PROPERTIES
block|,
name|JCR_WRITE
block|,
name|REP_WRITE
block|}
decl_stmt|;
name|String
index|[]
name|AGGR_JCR_MODIFY_PROPERTIES
init|=
operator|new
name|String
index|[]
block|{
name|REP_ADD_PROPERTIES
block|,
name|REP_ALTER_PROPERTIES
block|,
name|REP_REMOVE_PROPERTIES
block|}
decl_stmt|;
name|String
index|[]
name|AGGR_JCR_WRITE
init|=
operator|new
name|String
index|[]
block|{
name|JCR_MODIFY_PROPERTIES
block|,
name|JCR_ADD_CHILD_NODES
block|,
name|JCR_REMOVE_CHILD_NODES
block|,
name|JCR_REMOVE_NODE
block|}
decl_stmt|;
name|String
index|[]
name|AGGR_REP_WRITE
init|=
operator|new
name|String
index|[]
block|{
name|JCR_WRITE
block|,
name|JCR_NODE_TYPE_MANAGEMENT
block|}
decl_stmt|;
block|}
end_interface

end_unit

