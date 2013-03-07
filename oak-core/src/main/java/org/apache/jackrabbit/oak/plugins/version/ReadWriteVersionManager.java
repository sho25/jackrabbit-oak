begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|plugins
operator|.
name|version
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Root
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
name|api
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
name|api
operator|.
name|Type
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
name|commons
operator|.
name|PathUtils
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
name|core
operator|.
name|ReadOnlyRoot
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
name|core
operator|.
name|ReadOnlyTree
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
name|namepath
operator|.
name|NamePathMapper
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
name|identifier
operator|.
name|IdentifierManager
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|value
operator|.
name|Conversions
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
name|state
operator|.
name|NodeBuilder
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
name|util
operator|.
name|TODO
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_BASEVERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CREATED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_FROZENMIXINTYPES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_FROZENNODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_FROZENPRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_FROZENUUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_ISCHECKEDOUT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PREDECESSORS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_ROOTVERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_SUCCESSORS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_UUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_VERSIONABLEUUID
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_VERSIONHISTORY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_VERSIONLABELS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_FROZENNODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_VERSION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_VERSIONHISTORY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_VERSIONLABELS
import|;
end_import

begin_import
import|import static
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
name|version
operator|.
name|VersionConstants
operator|.
name|REP_VERSIONSTORAGE
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|ReadWriteVersionManager
extends|extends
name|ReadOnlyVersionManager
block|{
specifier|private
specifier|final
name|NodeBuilder
name|versionStorageNode
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|workspaceRoot
decl_stmt|;
name|ReadWriteVersionManager
parameter_list|(
name|NodeBuilder
name|versionStorageNode
parameter_list|,
name|NodeBuilder
name|workspaceRoot
parameter_list|)
block|{
name|this
operator|.
name|versionStorageNode
operator|=
name|checkNotNull
argument_list|(
name|versionStorageNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|workspaceRoot
operator|=
name|checkNotNull
argument_list|(
name|workspaceRoot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|TreeLocation
name|getVersionStorageLocation
parameter_list|()
block|{
return|return
operator|new
name|ReadOnlyTree
argument_list|(
name|versionStorageNode
operator|.
name|getNodeState
argument_list|()
argument_list|)
operator|.
name|getLocation
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|Root
name|getWorkspaceRoot
parameter_list|()
block|{
return|return
operator|new
name|ReadOnlyRoot
argument_list|(
name|workspaceRoot
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|protected
name|ReadOnlyNodeTypeManager
name|getNodeTypeManager
parameter_list|()
block|{
return|return
name|ReadOnlyNodeTypeManager
operator|.
name|getInstance
argument_list|(
name|getWorkspaceRoot
argument_list|()
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
return|;
block|}
comment|/**      * Gets or creates the version history for the given      *<code>versionable</code> node.      *      * @param versionable the versionable node.      * @return the version history node.      * @throws IllegalArgumentException if the given node does not have a      *<code>jcr:uuid</code> property.      */
annotation|@
name|Nonnull
name|NodeBuilder
name|getOrCreateVersionHistory
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|versionable
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|versionable
argument_list|)
expr_stmt|;
name|PropertyState
name|p
init|=
name|versionable
operator|.
name|getProperty
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not referenceable"
argument_list|)
throw|;
block|}
name|String
name|vUUID
init|=
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|String
name|relPath
init|=
name|getVersionHistoryPath
argument_list|(
name|vUUID
argument_list|)
decl_stmt|;
name|NodeBuilder
name|node
init|=
name|versionStorageNode
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|relPath
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|nt
decl_stmt|;
if|if
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|nt
operator|=
name|REP_VERSIONSTORAGE
expr_stmt|;
block|}
else|else
block|{
comment|// last path element denotes nt:versionHistory node
name|nt
operator|=
name|NT_VERSIONHISTORY
expr_stmt|;
block|}
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|nt
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
block|}
comment|// use jcr:versionLabels node to detect if we need to initialize the
comment|// version history
if|if
condition|(
operator|!
name|node
operator|.
name|hasChildNode
argument_list|(
name|JCR_VERSIONLABELS
argument_list|)
condition|)
block|{
comment|// jcr:versionableUuuid property
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_VERSIONABLEUUID
argument_list|,
name|vUUID
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
comment|// jcr:versionLabels child node
name|NodeBuilder
name|vLabels
init|=
name|node
operator|.
name|child
argument_list|(
name|JCR_VERSIONLABELS
argument_list|)
decl_stmt|;
name|vLabels
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_VERSIONLABELS
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
comment|// jcr:rootVersion child node
name|NodeBuilder
name|rootVersion
init|=
name|node
operator|.
name|child
argument_list|(
name|JCR_ROOTVERSION
argument_list|)
decl_stmt|;
name|rootVersion
operator|.
name|setProperty
argument_list|(
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|rootVersion
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_VERSION
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|String
name|now
init|=
name|Conversions
operator|.
name|convert
argument_list|(
name|GregorianCalendar
operator|.
name|getInstance
argument_list|()
argument_list|)
operator|.
name|toDate
argument_list|()
decl_stmt|;
name|rootVersion
operator|.
name|setProperty
argument_list|(
name|JCR_CREATED
argument_list|,
name|now
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|rootVersion
operator|.
name|setProperty
argument_list|(
name|JCR_PREDECESSORS
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Type
operator|.
name|REFERENCES
argument_list|)
expr_stmt|;
name|rootVersion
operator|.
name|setProperty
argument_list|(
name|JCR_SUCCESSORS
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|,
name|Type
operator|.
name|REFERENCES
argument_list|)
expr_stmt|;
comment|// jcr:frozenNode of jcr:rootVersion
name|NodeBuilder
name|frozenNode
init|=
name|rootVersion
operator|.
name|child
argument_list|(
name|JCR_FROZENNODE
argument_list|)
decl_stmt|;
name|frozenNode
operator|.
name|setProperty
argument_list|(
name|JCR_UUID
argument_list|,
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|frozenNode
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_FROZENNODE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|mixinTypes
decl_stmt|;
if|if
condition|(
name|versionable
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|mixinTypes
operator|=
name|versionable
operator|.
name|getProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mixinTypes
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|frozenNode
operator|.
name|setProperty
argument_list|(
name|JCR_FROZENMIXINTYPES
argument_list|,
name|mixinTypes
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|frozenNode
operator|.
name|setProperty
argument_list|(
name|JCR_FROZENPRIMARYTYPE
argument_list|,
name|versionable
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAME
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|frozenNode
operator|.
name|setProperty
argument_list|(
name|JCR_FROZENUUID
argument_list|,
name|vUUID
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
comment|// set jcr:isCheckedOut, jcr:versionHistory, jcr:baseVersion and
comment|// jcr:predecessors on versionable node
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_VERSIONHISTORY
argument_list|,
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_UUID
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|String
name|rootVersionUUID
init|=
name|rootVersion
operator|.
name|getProperty
argument_list|(
name|JCR_UUID
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_BASEVERSION
argument_list|,
name|rootVersionUUID
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
expr_stmt|;
name|versionable
operator|.
name|setProperty
argument_list|(
name|JCR_PREDECESSORS
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|rootVersionUUID
argument_list|)
argument_list|,
name|Type
operator|.
name|REFERENCES
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
specifier|public
name|void
name|checkout
parameter_list|(
name|NodeBuilder
name|versionable
parameter_list|)
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|checkin
parameter_list|(
name|NodeBuilder
name|versionable
parameter_list|)
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|restore
parameter_list|(
name|NodeBuilder
name|versionable
parameter_list|)
block|{
name|TODO
operator|.
name|unimplemented
argument_list|()
expr_stmt|;
block|}
comment|// TODO: more methods that modify versions
block|}
end_class

end_unit

