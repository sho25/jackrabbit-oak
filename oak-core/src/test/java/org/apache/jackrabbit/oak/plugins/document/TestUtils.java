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
name|document
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|base
operator|.
name|Functions
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|stats
operator|.
name|Clock
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
name|Functions
operator|.
name|compose
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
name|Functions
operator|.
name|constant
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|TestUtils
block|{
specifier|public
specifier|static
specifier|final
name|Predicate
argument_list|<
name|UpdateOp
argument_list|>
name|IS_LAST_REV_UPDATE
init|=
operator|new
name|Predicate
argument_list|<
name|UpdateOp
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
name|UpdateOp
name|input
parameter_list|)
block|{
return|return
name|input
operator|!=
literal|null
operator|&&
name|isLastRevUpdate
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|NO_BINARY
init|=
name|compose
argument_list|(
name|constant
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|,
name|Functions
operator|.
expr|<
name|String
operator|>
name|identity
argument_list|()
argument_list|)
decl_stmt|;
comment|/**      * Returns {@code true} if the given {@code update} performs a      * {@code _lastRev} update.      *      * @param update the update to check.      * @return {@code true} if the operation performs an update on      *          {@code _lastRev}, {@code false} otherwise.      */
specifier|public
specifier|static
name|boolean
name|isLastRevUpdate
parameter_list|(
name|UpdateOp
name|update
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|UpdateOp
operator|.
name|Key
argument_list|,
name|UpdateOp
operator|.
name|Operation
argument_list|>
name|change
range|:
name|update
operator|.
name|getChanges
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|NodeDocument
operator|.
name|isLastRevEntry
argument_list|(
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
operator|!
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
operator|.
name|equals
argument_list|(
name|change
operator|.
name|getKey
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|NodeState
name|merge
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
throws|throws
name|CommitFailedException
block|{
return|return
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|createChild
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
modifier|...
name|paths
parameter_list|)
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|childBuilder
argument_list|(
name|root
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|root
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|childBuilder
parameter_list|(
name|NodeBuilder
name|root
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|nb
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|nodeName
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
specifier|public
specifier|static
name|DocumentNodeState
name|asDocumentState
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|instanceof
name|DocumentNodeState
condition|)
block|{
return|return
operator|(
name|DocumentNodeState
operator|)
name|state
return|;
block|}
name|fail
argument_list|(
literal|"Not of type DocumentNodeState "
operator|+
name|state
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|void
name|setRevisionClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|Revision
operator|.
name|setClock
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|resetRevisionClockToDefault
parameter_list|()
block|{
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

