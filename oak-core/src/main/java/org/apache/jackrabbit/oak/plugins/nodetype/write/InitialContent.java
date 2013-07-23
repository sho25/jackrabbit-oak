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
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|CommitFailedException
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
name|core
operator|.
name|RootImpl
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
name|RegistrationEditorProvider
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
name|commit
operator|.
name|EmptyHook
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
name|PostCommitHook
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
name|NodeStoreBranch
import|;
end_import

begin_comment
comment|/**  * {@code InitialContent} implements a {@link RepositoryInitializer} and  * registers built-in node types when the micro kernel becomes available.  */
end_comment

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|RepositoryInitializer
operator|.
name|class
argument_list|)
specifier|public
class|class
name|InitialContent
implements|implements
name|RepositoryInitializer
implements|,
name|NodeTypeConstants
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|initialize
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|NodeBuilder
name|root
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|root
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
name|root
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
name|root
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
name|VersionConstants
operator|.
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
block|}
if|if
condition|(
operator|!
name|root
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
name|root
argument_list|)
decl_stmt|;
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
expr_stmt|;
name|NodeBuilder
name|nt
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
comment|// the cost of using the property index for "@primaryType is not null" is very high
name|nt
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ENTRY_COUNT_PROPERTY_NAME
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|NodeStoreBranch
name|branch
init|=
name|store
operator|.
name|branch
argument_list|()
decl_stmt|;
name|branch
operator|.
name|setRoot
argument_list|(
name|root
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|branch
operator|.
name|merge
argument_list|(
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|PostCommitHook
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|BuiltInNodeTypes
operator|.
name|register
argument_list|(
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|RegistrationEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
block|}
end_class

end_unit

