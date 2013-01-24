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
name|mongomk
operator|.
name|impl
operator|.
name|command
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoNodeStore
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
name|mongomk
operator|.
name|impl
operator|.
name|action
operator|.
name|FetchHeadRevisionIdAction
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
name|mongomk
operator|.
name|util
operator|.
name|MongoUtil
import|;
end_import

begin_comment
comment|/**  * A {@code Command} for {@code MongoMicroKernel#waitForCommit(String, long)}  */
end_comment

begin_class
specifier|public
class|class
name|WaitForCommitCommand
extends|extends
name|BaseCommand
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|WAIT_FOR_COMMIT_POLL_MILLIS
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|String
name|oldHeadRevisionId
decl_stmt|;
specifier|private
specifier|final
name|long
name|timeout
decl_stmt|;
comment|/**      * Constructs a {@code WaitForCommitCommandMongo}      *      * @param nodeStore Node store.      * @param oldHeadRevisionId Id of earlier head revision      * @param timeout The maximum time to wait in milliseconds      */
specifier|public
name|WaitForCommitCommand
parameter_list|(
name|MongoNodeStore
name|nodeStore
parameter_list|,
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|super
argument_list|(
name|nodeStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|oldHeadRevisionId
operator|=
name|oldHeadRevisionId
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Long
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|startTimestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Long
name|initialHeadRevisionId
init|=
name|getHeadRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
return|return
name|initialHeadRevisionId
return|;
block|}
name|Long
name|oldHeadRevision
init|=
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|oldHeadRevisionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldHeadRevision
operator|!=
name|initialHeadRevisionId
condition|)
block|{
return|return
name|initialHeadRevisionId
return|;
block|}
name|long
name|waitForCommitPollMillis
init|=
name|Math
operator|.
name|min
argument_list|(
name|WAIT_FOR_COMMIT_POLL_MILLIS
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|long
name|headRevisionId
init|=
name|getHeadRevision
argument_list|()
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|headRevisionId
operator|!=
name|initialHeadRevisionId
operator|||
name|now
operator|-
name|startTimestamp
operator|>=
name|timeout
condition|)
block|{
return|return
name|headRevisionId
return|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|waitForCommitPollMillis
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|getHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|FetchHeadRevisionIdAction
name|query
init|=
operator|new
name|FetchHeadRevisionIdAction
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
return|return
name|query
operator|.
name|execute
argument_list|()
return|;
block|}
block|}
end_class

end_unit

