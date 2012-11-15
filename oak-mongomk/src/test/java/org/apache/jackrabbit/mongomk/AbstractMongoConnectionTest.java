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
name|MongoConnection
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
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_comment
comment|/**  * Base class for test cases that need a {@link MongoConnection}  * to a clean test database. Tests in subclasses are automatically  * skipped if the configured MongoDB connection can not be created.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractMongoConnectionTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|HOST
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.host"
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|PORT
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"mongo.port"
argument_list|,
literal|27017
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
name|DB
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.db"
argument_list|,
literal|"MongoMKDB"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|MongoConnection
name|mongoConnection
decl_stmt|;
specifier|private
specifier|static
name|Exception
name|mongoException
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|mongoConnection
operator|==
literal|null
condition|)
block|{
name|mongoConnection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|HOST
argument_list|,
name|PORT
argument_list|,
name|DB
argument_list|)
expr_stmt|;
try|try
block|{
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|command
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"ping"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|mongoException
operator|=
name|e
expr_stmt|;
block|}
block|}
name|Assume
operator|.
name|assumeNoException
argument_list|(
name|mongoException
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
comment|// the database will get automatically recreated
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
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
name|mongoConnection
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

