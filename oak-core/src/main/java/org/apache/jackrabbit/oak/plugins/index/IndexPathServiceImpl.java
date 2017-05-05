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
name|index
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Iterators
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
name|Reference
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
name|plugins
operator|.
name|index
operator|.
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|query
operator|.
name|NodeStateNodeTypeInfoProvider
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
name|query
operator|.
name|QueryEngineSettings
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
name|query
operator|.
name|ast
operator|.
name|NodeTypeInfo
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
name|query
operator|.
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|query
operator|.
name|ast
operator|.
name|SelectorImpl
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|query
operator|.
name|IndexRow
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
name|query
operator|.
name|QueryIndex
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
name|NodeStateUtils
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
name|checkState
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
name|DECLARING_NODE_TYPES
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

begin_class
annotation|@
name|Component
annotation|@
name|Service
specifier|public
class|class
name|IndexPathServiceImpl
implements|implements
name|IndexPathService
block|{
specifier|private
specifier|final
name|QueryEngineSettings
name|settings
init|=
operator|new
name|QueryEngineSettings
argument_list|()
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|MountInfoProvider
name|mountInfoProvider
decl_stmt|;
specifier|public
name|IndexPathServiceImpl
parameter_list|()
block|{
comment|//Required for SCR
name|settings
operator|.
name|setFailTraversal
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|settings
operator|.
name|setLimitReads
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexPathServiceImpl
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|this
argument_list|(
name|nodeStore
argument_list|,
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexPathServiceImpl
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getIndexPaths
parameter_list|()
block|{
name|NodeState
name|nodeType
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"/oak:index/nodetype"
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
literal|"property"
operator|.
name|equals
argument_list|(
name|nodeType
operator|.
name|getString
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|,
literal|"nodetype index at "
operator|+
literal|"/oak:index/nodetype is found to be disabled. Cannot determine the paths of all indexes"
argument_list|)
expr_stmt|;
comment|//Check if oak:QueryIndexDefinition is indexed as part of nodetype index
name|boolean
name|indxDefnTypeIndexed
init|=
name|Iterables
operator|.
name|contains
argument_list|(
name|nodeType
operator|.
name|getNames
argument_list|(
name|DECLARING_NODE_TYPES
argument_list|)
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|indxDefnTypeIndexed
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
operator|+
literal|" is not found to be indexed as part of "
operator|+
literal|"nodetype index. Cannot determine the paths of all indexes"
argument_list|)
expr_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
parameter_list|()
block|{
name|Iterator
argument_list|<
name|IndexRow
argument_list|>
name|itr
init|=
name|getIndex
argument_list|()
operator|.
name|query
argument_list|(
name|createFilter
argument_list|(
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
argument_list|,
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Iterators
operator|.
name|transform
argument_list|(
name|itr
argument_list|,
operator|new
name|Function
argument_list|<
name|IndexRow
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|IndexRow
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getPath
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeTypeInfoProvider
name|nodeTypes
init|=
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|NodeTypeInfo
name|type
init|=
name|nodeTypes
operator|.
name|getNodeTypeInfo
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
name|settings
argument_list|)
return|;
block|}
specifier|private
name|QueryIndex
name|getIndex
parameter_list|()
block|{
name|NodeTypeIndexProvider
name|idxProvider
init|=
operator|new
name|NodeTypeIndexProvider
argument_list|()
decl_stmt|;
name|idxProvider
operator|.
name|with
argument_list|(
name|mountInfoProvider
argument_list|)
expr_stmt|;
return|return
name|idxProvider
operator|.
name|getQueryIndexes
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

