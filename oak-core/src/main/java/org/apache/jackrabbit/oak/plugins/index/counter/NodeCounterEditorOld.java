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
operator|.
name|counter
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|IndexUpdateCallback
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
name|jmx
operator|.
name|NodeCounter
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
name|Editor
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
name|commons
operator|.
name|hash
operator|.
name|SipHash
import|;
end_import

begin_comment
comment|/**  * An approximate descendant node counter mechanism.  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|NodeCounterEditorOld
implements|implements
name|Editor
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DATA_NODE_NAME
init|=
literal|":index"
decl_stmt|;
comment|// the property that is used with the "old" (pseudo-random number generator based) method
specifier|public
specifier|static
specifier|final
name|String
name|COUNT_PROPERTY_NAME
init|=
literal|":count"
decl_stmt|;
comment|// the property that is used with the "new" (hash of the path based) method
specifier|public
specifier|static
specifier|final
name|String
name|COUNT_HASH_PROPERTY_NAME
init|=
literal|":cnt"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_RESOLUTION
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|NodeCounterRoot
name|root
decl_stmt|;
specifier|private
specifier|final
name|NodeCounterEditorOld
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|long
name|countOffset
decl_stmt|;
specifier|private
name|SipHash
name|hash
decl_stmt|;
specifier|public
name|NodeCounterEditorOld
parameter_list|(
name|NodeCounterRoot
name|root
parameter_list|,
name|NodeCounterEditorOld
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|SipHash
name|hash
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|hash
expr_stmt|;
block|}
specifier|private
name|SipHash
name|getHash
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|!=
literal|null
condition|)
block|{
return|return
name|hash
return|;
block|}
name|SipHash
name|h
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|h
operator|=
operator|new
name|SipHash
argument_list|(
name|root
operator|.
name|seed
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|h
operator|=
operator|new
name|SipHash
argument_list|(
name|parent
operator|.
name|getHash
argument_list|()
argument_list|,
name|name
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|hash
operator|=
name|h
expr_stmt|;
return|return
name|h
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|NodeCounter
operator|.
name|COUNT_HASH
condition|)
block|{
name|leaveNew
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
return|return;
block|}
name|leaveOld
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|leaveOld
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|long
name|offset
init|=
name|ApproximateCounter
operator|.
name|calculateOffset
argument_list|(
name|countOffset
argument_list|,
name|root
operator|.
name|resolution
argument_list|)
decl_stmt|;
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
return|return;
block|}
comment|// only read the value of the property if really needed
name|NodeBuilder
name|builder
init|=
name|getBuilder
argument_list|()
decl_stmt|;
name|PropertyState
name|p
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|COUNT_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|p
operator|==
literal|null
condition|?
literal|0
else|:
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|offset
operator|=
name|ApproximateCounter
operator|.
name|adjustOffset
argument_list|(
name|count
argument_list|,
name|offset
argument_list|,
name|root
operator|.
name|resolution
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|count
operator|+=
name|offset
expr_stmt|;
name|root
operator|.
name|callback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|builder
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|COUNT_PROPERTY_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|COUNT_PROPERTY_NAME
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|leaveNew
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|countOffset
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|NodeBuilder
name|builder
init|=
name|getBuilder
argument_list|()
decl_stmt|;
name|PropertyState
name|p
init|=
name|builder
operator|.
name|getProperty
argument_list|(
name|COUNT_HASH_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|p
operator|==
literal|null
condition|?
literal|0
else|:
name|p
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|count
operator|+=
name|countOffset
expr_stmt|;
name|root
operator|.
name|callback
operator|.
name|indexUpdate
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|builder
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|builder
operator|.
name|removeProperty
argument_list|(
name|COUNT_HASH_PROPERTY_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|COUNT_HASH_PROPERTY_NAME
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|NodeBuilder
name|getBuilder
parameter_list|()
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
name|root
operator|.
name|definition
operator|.
name|child
argument_list|(
name|DATA_NODE_NAME
argument_list|)
return|;
block|}
return|return
name|parent
operator|.
name|getBuilder
argument_list|()
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// nothing to do
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Editor
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
throws|throws
name|CommitFailedException
block|{
return|return
name|getChildIndexEditor
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|NodeCounter
operator|.
name|COUNT_HASH
condition|)
block|{
name|SipHash
name|h
init|=
operator|new
name|SipHash
argument_list|(
name|getHash
argument_list|()
argument_list|,
name|name
operator|.
name|hashCode
argument_list|()
argument_list|)
decl_stmt|;
comment|// with bitMask=1024: with a probability of 1:1024,
if|if
condition|(
operator|(
name|h
operator|.
name|hashCode
argument_list|()
operator|&
name|root
operator|.
name|bitMask
operator|)
operator|==
literal|0
condition|)
block|{
comment|// add 1024
name|count
argument_list|(
name|root
operator|.
name|bitMask
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|getChildIndexEditor
argument_list|(
name|name
argument_list|,
name|h
argument_list|)
return|;
block|}
name|count
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|getChildIndexEditor
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|NodeCounter
operator|.
name|COUNT_HASH
condition|)
block|{
name|SipHash
name|h
init|=
operator|new
name|SipHash
argument_list|(
name|getHash
argument_list|()
argument_list|,
name|name
operator|.
name|hashCode
argument_list|()
argument_list|)
decl_stmt|;
comment|// with bitMask=1024: with a probability of 1:1024,
if|if
condition|(
operator|(
name|h
operator|.
name|hashCode
argument_list|()
operator|&
name|root
operator|.
name|bitMask
operator|)
operator|==
literal|0
condition|)
block|{
comment|// subtract 1024
name|count
argument_list|(
operator|-
operator|(
name|root
operator|.
name|bitMask
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|getChildIndexEditor
argument_list|(
name|name
argument_list|,
name|h
argument_list|)
return|;
block|}
name|count
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|getChildIndexEditor
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|void
name|count
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|countOffset
operator|+=
name|offset
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|count
argument_list|(
name|offset
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Editor
name|getChildIndexEditor
parameter_list|(
name|String
name|name
parameter_list|,
name|SipHash
name|hash
parameter_list|)
block|{
return|return
operator|new
name|NodeCounterEditorOld
argument_list|(
name|root
argument_list|,
name|this
argument_list|,
name|name
argument_list|,
name|hash
argument_list|)
return|;
block|}
specifier|public
specifier|static
class|class
name|NodeCounterRoot
block|{
specifier|final
name|int
name|resolution
decl_stmt|;
specifier|final
name|long
name|seed
decl_stmt|;
specifier|final
name|int
name|bitMask
decl_stmt|;
specifier|final
name|NodeBuilder
name|definition
decl_stmt|;
specifier|final
name|NodeState
name|root
decl_stmt|;
specifier|final
name|IndexUpdateCallback
name|callback
decl_stmt|;
name|NodeCounterRoot
parameter_list|(
name|int
name|resolution
parameter_list|,
name|long
name|seed
parameter_list|,
name|NodeBuilder
name|definition
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|IndexUpdateCallback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|resolution
operator|=
name|resolution
expr_stmt|;
name|this
operator|.
name|seed
operator|=
name|seed
expr_stmt|;
comment|// if resolution is 1000, then the bitMask is 1023 (bits 0..9 set)
name|this
operator|.
name|bitMask
operator|=
operator|(
name|Integer
operator|.
name|highestOneBit
argument_list|(
name|resolution
argument_list|)
operator|*
literal|2
operator|)
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|definition
operator|=
name|definition
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

