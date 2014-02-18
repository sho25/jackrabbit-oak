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
name|privilege
package|;
end_package

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
comment|/**  * Internal name constants used for the privilege management.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeConstants
block|{
comment|//--------------------------------< Constants for Privilege Definitions>---
comment|/**      * Internal (oak) name for the root node of the privilege store.      */
name|String
name|REP_PRIVILEGES
init|=
literal|"rep:privileges"
decl_stmt|;
comment|/**      * Name of the property that defines if the privilege is abstract.      */
name|String
name|REP_IS_ABSTRACT
init|=
literal|"rep:isAbstract"
decl_stmt|;
comment|/**      * Name of the privilege definition property that stores the aggregate privilege names.      */
name|String
name|REP_AGGREGATES
init|=
literal|"rep:aggregates"
decl_stmt|;
comment|/**      * Name of the property storing the value of the next available privilege bits.      */
name|String
name|REP_NEXT
init|=
literal|"rep:next"
decl_stmt|;
comment|/**      * The internal names of all property definitions that are associated with      * the {@link #NT_REP_PRIVILEGE rep:Privilege} node type      */
name|Set
argument_list|<
name|String
argument_list|>
name|PRIVILEGE_PROPERTY_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|REP_IS_ABSTRACT
argument_list|,
name|REP_AGGREGATES
argument_list|,
name|REP_NEXT
argument_list|)
decl_stmt|;
comment|/**      * Internal (oak) path for the privilege store.      */
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
comment|/**      * Node type name of the root node of the privilege store      */
name|String
name|NT_REP_PRIVILEGES
init|=
literal|"rep:Privileges"
decl_stmt|;
comment|/**      * Node type name of the privilege definition nodes      */
name|String
name|NT_REP_PRIVILEGE
init|=
literal|"rep:Privilege"
decl_stmt|;
comment|/**      * Node type names associated with privilege content      */
name|Set
argument_list|<
name|String
argument_list|>
name|PRIVILEGE_NODETYPE_NAMES
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NT_REP_PRIVILEGES
argument_list|,
name|NT_REP_PRIVILEGE
argument_list|)
decl_stmt|;
comment|/**      * Name of the privilege definition property that stores the internal representation      * of this privilege.      */
name|String
name|REP_BITS
init|=
literal|"rep:bits"
decl_stmt|;
comment|//----------------------------------------------------< Privilege Names>---
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_READ} privilege      */
name|String
name|JCR_READ
init|=
literal|"jcr:read"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_MODIFY_PROPERTIES} privilege      */
name|String
name|JCR_MODIFY_PROPERTIES
init|=
literal|"jcr:modifyProperties"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_ADD_CHILD_NODES} privilege      */
name|String
name|JCR_ADD_CHILD_NODES
init|=
literal|"jcr:addChildNodes"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_REMOVE_NODE} privilege      */
name|String
name|JCR_REMOVE_NODE
init|=
literal|"jcr:removeNode"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_REMOVE_CHILD_NODES} privilege      */
name|String
name|JCR_REMOVE_CHILD_NODES
init|=
literal|"jcr:removeChildNodes"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_WRITE} privilege      */
name|String
name|JCR_WRITE
init|=
literal|"jcr:write"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_READ_ACCESS_CONTROL} privilege      */
name|String
name|JCR_READ_ACCESS_CONTROL
init|=
literal|"jcr:readAccessControl"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_MODIFY_ACCESS_CONTROL} privilege      */
name|String
name|JCR_MODIFY_ACCESS_CONTROL
init|=
literal|"jcr:modifyAccessControl"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_LOCK_MANAGEMENT} privilege      */
name|String
name|JCR_LOCK_MANAGEMENT
init|=
literal|"jcr:lockManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_VERSION_MANAGEMENT} privilege      */
name|String
name|JCR_VERSION_MANAGEMENT
init|=
literal|"jcr:versionManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_NODE_TYPE_MANAGEMENT} privilege      */
name|String
name|JCR_NODE_TYPE_MANAGEMENT
init|=
literal|"jcr:nodeTypeManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_RETENTION_MANAGEMENT} privilege      */
name|String
name|JCR_RETENTION_MANAGEMENT
init|=
literal|"jcr:retentionManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_LIFECYCLE_MANAGEMENT} privilege      */
name|String
name|JCR_LIFECYCLE_MANAGEMENT
init|=
literal|"jcr:lifecycleManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the jcr:workspaceManagement privilege      */
name|String
name|JCR_WORKSPACE_MANAGEMENT
init|=
literal|"jcr:workspaceManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the jcr:nodeTypeDefinitionManagement privilege      */
name|String
name|JCR_NODE_TYPE_DEFINITION_MANAGEMENT
init|=
literal|"jcr:nodeTypeDefinitionManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the jcr:namespaceManagement privilege      */
name|String
name|JCR_NAMESPACE_MANAGEMENT
init|=
literal|"jcr:namespaceManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the {@link javax.jcr.security.Privilege#JCR_ALL} privilege      */
name|String
name|JCR_ALL
init|=
literal|"jcr:all"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:privilegeManagement privilege      */
name|String
name|REP_PRIVILEGE_MANAGEMENT
init|=
literal|"rep:privilegeManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:write privilege      */
name|String
name|REP_WRITE
init|=
literal|"rep:write"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:userManagement privilege      *      * @since OAK 1.0      */
name|String
name|REP_USER_MANAGEMENT
init|=
literal|"rep:userManagement"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:readNodes privilege      *      * @since OAK 1.0      */
name|String
name|REP_READ_NODES
init|=
literal|"rep:readNodes"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:readProperties privilege      *      * @since OAK 1.0      */
name|String
name|REP_READ_PROPERTIES
init|=
literal|"rep:readProperties"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:addProperties privilege      *      * @since OAK 1.0      */
name|String
name|REP_ADD_PROPERTIES
init|=
literal|"rep:addProperties"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:alterProperties privilege      *      * @since OAK 1.0      */
name|String
name|REP_ALTER_PROPERTIES
init|=
literal|"rep:alterProperties"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:removeProperties privilege      *      * @since OAK 1.0      */
name|String
name|REP_REMOVE_PROPERTIES
init|=
literal|"rep:removeProperties"
decl_stmt|;
comment|/**      * Internal (oak) name of the rep:indexDefinitionManagement privilege      *      * @since OAK 1.0      */
name|String
name|REP_INDEX_DEFINITION_MANAGEMENT
init|=
literal|"rep:indexDefinitionManagement"
decl_stmt|;
block|}
end_interface

end_unit

