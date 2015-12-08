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
name|plugins
operator|.
name|nodetype
operator|.
name|write
package|;
end_package

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
import|import static
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
operator|.
name|NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|ImmutableList
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|IndexUtils
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
name|index
operator|.
name|counter
operator|.
name|NodeCounterEditorProvider
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
name|memory
operator|.
name|MemoryNodeStore
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
name|memory
operator|.
name|ModifiedNodeState
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
name|name
operator|.
name|NamespaceEditorProvider
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
name|name
operator|.
name|Namespaces
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
name|plugins
operator|.
name|nodetype
operator|.
name|TypeEditorProvider
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
name|RootFactory
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
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|CompositeEditorProvider
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
name|commit
operator|.
name|EditorHook
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|ApplyDiff
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * {@code InitialContent} implements a {@link RepositoryInitializer} and  * registers built-in node types when the micro kernel becomes available.  */
end_comment

begin_class
specifier|public
class|class
name|InitialContent
implements|implements
name|RepositoryInitializer
implements|,
name|NodeTypeConstants
block|{
specifier|public
specifier|static
specifier|final
name|NodeState
name|INITIAL_CONTENT
init|=
name|createInitialContent
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|NodeState
name|createInitialContent
parameter_list|()
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Whether to pre-populate the version store with intermediate nodes.      */
specifier|private
name|boolean
name|prePopulateVS
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_ROOT
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|builder
operator|.
name|hasChildNode
argument_list|(
name|JCR_SYSTEM
argument_list|)
condition|)
block|{
name|NodeBuilder
name|system
init|=
name|builder
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
decl_stmt|;
name|system
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_SYSTEM
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_REP_NODE_TYPES
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|system
operator|.
name|child
argument_list|(
name|VersionConstants
operator|.
name|JCR_ACTIVITIES
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|VersionConstants
operator|.
name|REP_ACTIVITIES
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Namespaces
operator|.
name|setupNamespaces
argument_list|(
name|system
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|versionStorage
init|=
name|builder
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_VERSIONSTORAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|prePopulateVS
operator|&&
operator|!
name|isInitialized
argument_list|(
name|versionStorage
argument_list|)
condition|)
block|{
name|createIntermediateNodes
argument_list|(
name|versionStorage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|builder
operator|.
name|hasChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
condition|)
block|{
name|NodeBuilder
name|index
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|NodeBuilder
name|uuid
init|=
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"uuid"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|(
name|JCR_UUID
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|uuid
operator|.
name|setProperty
argument_list|(
literal|"info"
argument_list|,
literal|"Oak index for UUID lookup (direct lookup of nodes with the mixin 'mix:referenceable')."
argument_list|)
expr_stmt|;
name|NodeBuilder
name|nodetype
init|=
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"nodetype"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|JCR_MIXINTYPES
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|nodetype
operator|.
name|setProperty
argument_list|(
literal|"info"
argument_list|,
literal|"Oak index for queries with node type, and possibly path restrictions, "
operator|+
literal|"for example \"/jcr:root/content//element(*, mix:language)\"."
argument_list|)
expr_stmt|;
name|IndexUtils
operator|.
name|createReferenceIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|index
operator|.
name|child
argument_list|(
literal|"counter"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|NodeCounterEditorProvider
operator|.
name|TYPE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"info"
argument_list|,
literal|"Oak index that allows to estimate "
operator|+
literal|"how many nodes are stored below a given path, "
operator|+
literal|"to decide whether traversing or using an index is faster."
argument_list|)
expr_stmt|;
block|}
comment|// squeeze node state before it is passed to store (OAK-2411)
name|NodeState
name|base
init|=
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|NodeTypeRegistry
operator|.
name|registerBuiltIn
argument_list|(
name|RootFactory
operator|.
name|createSystemRoot
argument_list|(
name|store
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|CompositeEditorProvider
argument_list|(
operator|new
name|NamespaceEditorProvider
argument_list|()
argument_list|,
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|target
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|target
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|ApplyDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Instructs the initializer to pre-populate the version store with      * intermediate nodes.      *      * @return this instance.      */
specifier|public
name|InitialContent
name|withPrePopulatedVersionStore
parameter_list|()
block|{
name|this
operator|.
name|prePopulateVS
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|//--------------------------< internal>------------------------------------
specifier|private
name|boolean
name|isInitialized
parameter_list|(
name|NodeBuilder
name|versionStorage
parameter_list|)
block|{
name|PropertyState
name|init
init|=
name|versionStorage
operator|.
name|getProperty
argument_list|(
literal|":initialized"
argument_list|)
decl_stmt|;
return|return
name|init
operator|!=
literal|null
operator|&&
name|init
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|>
literal|0
return|;
block|}
specifier|private
name|void
name|createIntermediateNodes
parameter_list|(
name|NodeBuilder
name|versionStorage
parameter_list|)
block|{
name|String
name|fmt
init|=
literal|"%02x"
decl_stmt|;
name|versionStorage
operator|.
name|setProperty
argument_list|(
literal|":initialized"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|0xff
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|c
init|=
name|storageChild
argument_list|(
name|versionStorage
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|i
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|0xff
condition|;
name|j
operator|++
control|)
block|{
name|storageChild
argument_list|(
name|c
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|NodeBuilder
name|storageChild
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|NodeBuilder
name|c
init|=
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|hasProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
condition|)
block|{
name|c
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|REP_VERSIONSTORAGE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

