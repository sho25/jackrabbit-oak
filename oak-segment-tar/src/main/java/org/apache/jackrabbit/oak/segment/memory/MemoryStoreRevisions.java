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
name|segment
operator|.
name|memory
package|;
end_package

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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|RecordId
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
name|segment
operator|.
name|Revisions
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

begin_comment
comment|/**  * This is a simple in memory {@code Revisions} implementation.  * It is non blocking and does not support any {@link Option}s.  */
end_comment

begin_class
specifier|public
class|class
name|MemoryStoreRevisions
implements|implements
name|Revisions
block|{
specifier|private
name|RecordId
name|head
decl_stmt|;
comment|/**      * Bind this instance to a {@code store}.      */
specifier|public
name|void
name|bind
parameter_list|(
name|MemoryStore
name|store
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|head
operator|==
literal|null
condition|)
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setChildNode
argument_list|(
literal|"root"
argument_list|,
name|EMPTY_NODE
argument_list|)
expr_stmt|;
name|head
operator|=
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|writeNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|getWriter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkBound
parameter_list|()
block|{
name|checkState
argument_list|(
name|head
operator|!=
literal|null
argument_list|,
literal|"Revisions not bound to a store"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
specifier|synchronized
name|RecordId
name|getHead
parameter_list|()
block|{
name|checkBound
argument_list|()
expr_stmt|;
return|return
name|head
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RecordId
name|getPersistedHead
parameter_list|()
block|{
return|return
name|getHead
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|setHead
parameter_list|(
annotation|@
name|Nonnull
name|RecordId
name|expected
parameter_list|,
annotation|@
name|Nonnull
name|RecordId
name|head
parameter_list|,
annotation|@
name|Nonnull
name|Option
modifier|...
name|options
parameter_list|)
block|{
name|checkBound
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|head
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
condition|)
block|{
name|this
operator|.
name|head
operator|=
name|head
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Not supported: throws {@code UnsupportedOperationException}      * @throws UnsupportedOperationException always      */
annotation|@
name|Override
specifier|public
name|RecordId
name|setHead
parameter_list|(
annotation|@
name|Nonnull
name|Function
argument_list|<
name|RecordId
argument_list|,
name|RecordId
argument_list|>
name|newHead
parameter_list|,
annotation|@
name|Nonnull
name|Option
modifier|...
name|options
parameter_list|)
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

