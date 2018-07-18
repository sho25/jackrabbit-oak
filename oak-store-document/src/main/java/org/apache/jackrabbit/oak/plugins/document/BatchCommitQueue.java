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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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

begin_comment
comment|/**  * Queues updates on a commit root document and batches them into a single  * call to the {@link DocumentStore}.  */
end_comment

begin_class
specifier|final
class|class
name|BatchCommitQueue
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BatchCommitQueue
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The pending batch commits.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BatchCommit
argument_list|>
name|pending
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|/**      * The batch commits in progress.      */
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BatchCommit
argument_list|>
name|inProgress
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
name|BatchCommitQueue
parameter_list|(
annotation|@
name|NotNull
name|DocumentStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
name|updateDocument
parameter_list|(
name|UpdateOp
name|op
parameter_list|)
block|{
name|String
name|id
init|=
name|op
operator|.
name|getId
argument_list|()
decl_stmt|;
comment|// check if there is already a batch commit in progress for
comment|// the document
synchronized|synchronized
init|(
name|this
init|)
block|{
name|BatchCommit
name|commit
init|=
name|inProgress
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Commit with id {} in progress"
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// get or create a pending batch commit
name|commit
operator|=
name|pending
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|commit
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating pending BatchCommit for id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|commit
operator|=
operator|new
name|BatchCommit
argument_list|(
name|id
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pending
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|commit
operator|=
operator|new
name|BatchCommit
argument_list|(
name|id
argument_list|,
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding inProgress BatchCommit for id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|inProgress
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Enqueueing operation with id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return
name|commit
operator|.
name|enqueue
argument_list|(
name|op
argument_list|)
return|;
block|}
block|}
name|void
name|finished
parameter_list|(
name|BatchCommit
name|commit
parameter_list|)
block|{
name|String
name|id
init|=
name|commit
operator|.
name|getId
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"BatchCommit finished with id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|inProgress
operator|.
name|remove
argument_list|(
name|id
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"BatchCommit for "
operator|+
name|id
operator|+
literal|" is not in progress"
argument_list|)
throw|;
block|}
name|commit
operator|=
name|pending
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Moving pending BatchCommit to inProgress with id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|inProgress
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|commit
operator|.
name|release
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"BatchCommit released with id {}"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|DocumentStore
name|getStore
parameter_list|()
block|{
return|return
name|store
return|;
block|}
block|}
end_class

end_unit

