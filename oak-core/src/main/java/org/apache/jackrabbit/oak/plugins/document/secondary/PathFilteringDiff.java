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
name|document
operator|.
name|secondary
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
name|plugins
operator|.
name|document
operator|.
name|AbstractDocumentNodeState
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
name|document
operator|.
name|RevisionVector
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
name|PathFilter
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|checkArgument
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
name|document
operator|.
name|secondary
operator|.
name|DelegatingDocumentNodeState
operator|.
name|PROP_LAST_REV
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
name|document
operator|.
name|secondary
operator|.
name|DelegatingDocumentNodeState
operator|.
name|PROP_PATH
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
name|document
operator|.
name|secondary
operator|.
name|DelegatingDocumentNodeState
operator|.
name|PROP_REVISION
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_class
specifier|public
class|class
name|PathFilteringDiff
extends|extends
name|ApplyDiff
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PathFilteringDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DiffContext
name|ctx
decl_stmt|;
specifier|private
specifier|final
name|AbstractDocumentNodeState
name|parent
decl_stmt|;
specifier|public
name|PathFilteringDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|PathFilter
name|pathFilter
parameter_list|,
name|AbstractDocumentNodeState
name|parent
parameter_list|)
block|{
name|this
argument_list|(
name|builder
argument_list|,
operator|new
name|DiffContext
argument_list|(
name|pathFilter
argument_list|)
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PathFilteringDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|DiffContext
name|ctx
parameter_list|,
name|AbstractDocumentNodeState
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|ctx
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|AbstractDocumentNodeState
name|afterDoc
init|=
name|asDocumentState
argument_list|(
name|after
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|String
name|nextPath
init|=
name|afterDoc
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|PathFilter
operator|.
name|Result
name|result
init|=
name|ctx
operator|.
name|pathFilter
operator|.
name|filter
argument_list|(
name|nextPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|PathFilter
operator|.
name|Result
operator|.
name|EXCLUDE
condition|)
block|{
return|return
literal|true
return|;
block|}
name|ctx
operator|.
name|traversingNode
argument_list|(
name|nextPath
argument_list|)
expr_stmt|;
comment|//We avoid this as we need to copy meta properties
comment|//super.childNodeAdded(name, after);
name|NodeBuilder
name|childBuilder
init|=
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|copyMetaProperties
argument_list|(
name|afterDoc
argument_list|,
name|childBuilder
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|EMPTY_NODE
argument_list|,
operator|new
name|PathFilteringDiff
argument_list|(
name|childBuilder
argument_list|,
name|ctx
argument_list|,
name|afterDoc
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
name|AbstractDocumentNodeState
name|afterDoc
init|=
name|asDocumentState
argument_list|(
name|after
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|String
name|nextPath
init|=
name|afterDoc
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|pathFilter
operator|.
name|filter
argument_list|(
name|nextPath
argument_list|)
operator|!=
name|PathFilter
operator|.
name|Result
operator|.
name|EXCLUDE
condition|)
block|{
name|ctx
operator|.
name|traversingNode
argument_list|(
name|nextPath
argument_list|)
expr_stmt|;
name|NodeBuilder
name|childBuilder
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|copyMetaProperties
argument_list|(
name|afterDoc
argument_list|,
name|childBuilder
argument_list|)
expr_stmt|;
return|return
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|PathFilteringDiff
argument_list|(
name|builder
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
argument_list|,
name|ctx
argument_list|,
name|afterDoc
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
name|String
name|path
init|=
name|asDocumentState
argument_list|(
name|before
argument_list|,
name|name
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|ctx
operator|.
name|pathFilter
operator|.
name|filter
argument_list|(
name|path
argument_list|)
operator|!=
name|PathFilter
operator|.
name|Result
operator|.
name|EXCLUDE
condition|)
block|{
return|return
name|super
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|AbstractDocumentNodeState
name|asDocumentState
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|state
operator|instanceof
name|AbstractDocumentNodeState
argument_list|,
literal|"Node %s (%s) at [%s/%s] is not"
operator|+
literal|" of expected type i.e. AbstractDocumentNodeState. Parent %s (%s)"
argument_list|,
name|state
argument_list|,
name|state
operator|.
name|getClass
argument_list|()
argument_list|,
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|parent
argument_list|,
name|parent
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|AbstractDocumentNodeState
operator|)
name|state
return|;
block|}
specifier|public
specifier|static
name|void
name|copyMetaProperties
parameter_list|(
name|AbstractDocumentNodeState
name|state
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_REVISION
argument_list|,
name|state
operator|.
name|getRootRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_LAST_REV
argument_list|,
name|state
operator|.
name|getLastRevision
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|PROP_PATH
argument_list|,
name|state
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PropertyState
name|asPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|RevisionVector
name|revision
parameter_list|)
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|revision
operator|.
name|asString
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|DiffContext
block|{
specifier|private
name|long
name|count
decl_stmt|;
specifier|final
name|PathFilter
name|pathFilter
decl_stmt|;
specifier|public
name|DiffContext
parameter_list|(
name|PathFilter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|pathFilter
operator|=
name|filter
expr_stmt|;
block|}
specifier|public
name|void
name|traversingNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|++
name|count
operator|%
literal|10000
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Updating Secondary Store. Traversed #{} - {}"
argument_list|,
name|count
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

