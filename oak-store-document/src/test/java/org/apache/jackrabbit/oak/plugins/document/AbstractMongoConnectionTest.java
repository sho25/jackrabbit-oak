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
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
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
name|util
operator|.
name|MongoConnection
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
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * Base class for test cases that need a {@link MongoConnection}  * to a clean test database. Tests in subclasses are automatically  * skipped if the configured MongoDB connection can not be created.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMongoConnectionTest
extends|extends
name|DocumentMKTestBase
block|{
annotation|@
name|Rule
specifier|public
name|MongoConnectionFactory
name|connectionFactory
init|=
operator|new
name|MongoConnectionFactory
argument_list|()
decl_stmt|;
specifier|protected
name|MongoConnection
name|mongoConnection
decl_stmt|;
specifier|protected
name|DocumentMK
name|mk
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|checkMongoDbAvailable
parameter_list|()
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|MongoUtils
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUpConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection
operator|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
expr_stmt|;
name|setRevisionClock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
expr_stmt|;
name|setClusterNodeInfoClock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
name|newBuilder
argument_list|(
name|mongoConnection
operator|.
name|getMongoClient
argument_list|()
argument_list|,
name|mongoConnection
operator|.
name|getDBName
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
specifier|protected
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
specifier|protected
name|void
name|setClusterNodeInfoClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|ClusterNodeInfo
operator|.
name|setClock
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|newBuilder
parameter_list|(
name|MongoClient
name|client
parameter_list|,
name|String
name|dbName
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|addToBuilder
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
argument_list|)
operator|.
name|clock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|client
argument_list|,
name|dbName
argument_list|)
return|;
block|}
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|addToBuilder
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|mk
parameter_list|)
block|{
return|return
name|mk
return|;
block|}
specifier|protected
name|Clock
name|getTestClock
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|Clock
operator|.
name|SIMPLE
return|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDownConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dbName
init|=
name|mongoConnection
operator|.
name|getDBName
argument_list|()
decl_stmt|;
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|dbName
argument_list|)
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
name|ClusterNodeInfo
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|DocumentMK
name|getDocumentMK
parameter_list|()
block|{
return|return
name|mk
return|;
block|}
specifier|protected
specifier|static
name|byte
index|[]
name|readFully
parameter_list|(
name|DocumentMK
name|mk
parameter_list|,
name|String
name|blobId
parameter_list|)
block|{
name|int
name|remaining
init|=
operator|(
name|int
operator|)
name|mk
operator|.
name|getLength
argument_list|(
name|blobId
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|int
name|count
init|=
name|mk
operator|.
name|read
argument_list|(
name|blobId
argument_list|,
name|offset
argument_list|,
name|bytes
argument_list|,
name|offset
argument_list|,
name|remaining
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
break|break;
block|}
name|offset
operator|+=
name|count
expr_stmt|;
name|remaining
operator|-=
name|count
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
block|}
end_class

end_unit

