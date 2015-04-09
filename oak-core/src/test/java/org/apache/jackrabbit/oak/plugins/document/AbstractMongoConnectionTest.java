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
name|DB
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|assumeNotNull
argument_list|(
name|MongoUtils
operator|.
name|getConnection
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
name|MongoUtils
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
name|Revision
operator|.
name|setClock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
expr_stmt|;
name|mk
operator|=
name|prepare
argument_list|(
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|getTestClock
argument_list|()
argument_list|)
argument_list|,
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
expr_stmt|;
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
specifier|protected
name|DocumentMK
operator|.
name|Builder
name|prepare
parameter_list|(
name|DocumentMK
operator|.
name|Builder
name|builder
parameter_list|,
name|DB
name|db
parameter_list|)
block|{
return|return
name|builder
operator|.
name|setMongoDB
argument_list|(
name|db
argument_list|)
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
name|mk
operator|.
name|dispose
argument_list|()
expr_stmt|;
comment|// the db might already be closed
name|mongoConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|mongoConnection
operator|=
name|MongoUtils
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
name|mongoConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|Revision
operator|.
name|resetClockToDefault
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|MicroKernel
name|getMicroKernel
parameter_list|()
block|{
return|return
name|mk
return|;
block|}
block|}
end_class

end_unit

