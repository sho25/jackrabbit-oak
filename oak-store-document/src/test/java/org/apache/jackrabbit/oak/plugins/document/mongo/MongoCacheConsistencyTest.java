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
operator|.
name|mongo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|fakemongo
operator|.
name|Fongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BulkWriteResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|OakFongo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
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
name|CacheConsistencyTestBase
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
name|DocumentMK
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
name|DocumentMKBuilderProvider
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
name|DocumentStore
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
name|DocumentStoreFixture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_class
specifier|public
class|class
name|MongoCacheConsistencyTest
extends|extends
name|CacheConsistencyTestBase
block|{
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|provider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|String
name|exceptionMsg
decl_stmt|;
annotation|@
name|Override
specifier|public
name|DocumentStoreFixture
name|getFixture
parameter_list|()
throws|throws
name|Exception
block|{
name|Fongo
name|fongo
init|=
operator|new
name|OakFongo
argument_list|(
literal|"fongo"
argument_list|)
block|{
specifier|private
name|String
name|suppressedEx
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|afterInsert
parameter_list|(
name|WriteResult
name|result
parameter_list|)
block|{
name|maybeThrow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterFindAndModify
parameter_list|(
name|DBObject
name|result
parameter_list|)
block|{
name|maybeThrow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterUpdate
parameter_list|(
name|WriteResult
name|result
parameter_list|)
block|{
name|maybeThrow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterRemove
parameter_list|(
name|WriteResult
name|result
parameter_list|)
block|{
name|maybeThrow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beforeExecuteBulkWriteOperation
parameter_list|(
name|boolean
name|ordered
parameter_list|,
name|Boolean
name|bypassDocumentValidation
parameter_list|,
name|List
argument_list|<
name|?
argument_list|>
name|writeRequests
parameter_list|,
name|WriteConcern
name|aWriteConcern
parameter_list|)
block|{
comment|// suppress potentially set exception message because
comment|// fongo bulk writes call other update methods
name|suppressedEx
operator|=
name|exceptionMsg
expr_stmt|;
name|exceptionMsg
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterExecuteBulkWriteOperation
parameter_list|(
name|BulkWriteResult
name|result
parameter_list|)
block|{
name|exceptionMsg
operator|=
name|suppressedEx
expr_stmt|;
name|suppressedEx
operator|=
literal|null
expr_stmt|;
name|maybeThrow
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|maybeThrow
parameter_list|()
block|{
if|if
condition|(
name|exceptionMsg
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|MongoException
argument_list|(
name|exceptionMsg
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
name|provider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DocumentStore
name|store
init|=
operator|new
name|MongoDocumentStore
argument_list|(
name|fongo
operator|.
name|getDB
argument_list|(
literal|"oak"
argument_list|)
argument_list|,
name|builder
argument_list|)
decl_stmt|;
return|return
operator|new
name|DocumentStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"MongoDB"
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentStore
name|createDocumentStore
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
return|return
name|store
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTemporaryUpdateException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|exceptionMsg
operator|=
name|msg
expr_stmt|;
block|}
block|}
end_class

end_unit

