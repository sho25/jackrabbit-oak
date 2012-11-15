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
name|multitenancy
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertFalse
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
name|assertTrue
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
name|mongomk
operator|.
name|AbstractMongoConnectionTest
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoMicroKernel
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
name|blob
operator|.
name|MongoGridFSBlobStore
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_comment
comment|/**  * Tests for multi-tenancy.  */
end_comment

begin_class
specifier|public
class|class
name|MultiTenancyTest
extends|extends
name|AbstractMongoConnectionTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DB2
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mongo.db"
argument_list|,
literal|"MongoMKDB2"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|MongoConnection
name|mongoConnection2
decl_stmt|;
specifier|private
specifier|static
name|MongoConnection
name|mongoConnection3
decl_stmt|;
specifier|private
specifier|static
name|MicroKernel
name|mk1
decl_stmt|;
specifier|private
specifier|static
name|MicroKernel
name|mk2
decl_stmt|;
specifier|private
specifier|static
name|MicroKernel
name|mk3
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|createMongoConnections
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection2
operator|=
operator|new
name|MongoConnection
argument_list|(
name|HOST
argument_list|,
name|PORT
argument_list|,
name|DB2
argument_list|)
expr_stmt|;
name|mongoConnection3
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
block|}
annotation|@
name|Before
specifier|public
name|void
name|setupMicroKernels
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection2
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
comment|// DB1 handled by the AbstractMongoConnectionTest
name|DB
name|db
init|=
name|mongoConnection
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|mk1
operator|=
operator|new
name|MongoMicroKernel
argument_list|(
name|mongoConnection
argument_list|,
operator|new
name|MongoNodeStore
argument_list|(
name|db
argument_list|)
argument_list|,
operator|new
name|MongoGridFSBlobStore
argument_list|(
name|db
argument_list|)
argument_list|)
expr_stmt|;
name|DB
name|db2
init|=
name|mongoConnection2
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|mk2
operator|=
operator|new
name|MongoMicroKernel
argument_list|(
name|mongoConnection2
argument_list|,
operator|new
name|MongoNodeStore
argument_list|(
name|db2
argument_list|)
argument_list|,
operator|new
name|MongoGridFSBlobStore
argument_list|(
name|db2
argument_list|)
argument_list|)
expr_stmt|;
name|DB
name|db3
init|=
name|mongoConnection3
operator|.
name|getDB
argument_list|()
decl_stmt|;
name|mk3
operator|=
operator|new
name|MongoMicroKernel
argument_list|(
name|mongoConnection3
argument_list|,
operator|new
name|MongoNodeStore
argument_list|(
name|db3
argument_list|)
argument_list|,
operator|new
name|MongoGridFSBlobStore
argument_list|(
name|db3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|dropDatabases
parameter_list|()
throws|throws
name|Exception
block|{
name|mongoConnection2
operator|.
name|getDB
argument_list|()
operator|.
name|dropDatabase
argument_list|()
expr_stmt|;
comment|// DB1 handled by the AbstractMongoConnectionTest
block|}
comment|/**      * Scenario: 3 MKs total, 2 MKs point to DB1, 1 points to DB2.      */
annotation|@
name|Test
specifier|public
name|void
name|basicMultiTenancy
parameter_list|()
block|{
name|mk1
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk1
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|mk2
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk3
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"c\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk1
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|mk2
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|mk3
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk1
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk1
operator|.
name|nodeExists
argument_list|(
literal|"/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk1
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk2
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk2
operator|.
name|nodeExists
argument_list|(
literal|"/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk2
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|mk3
operator|.
name|nodeExists
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk3
operator|.
name|nodeExists
argument_list|(
literal|"/b"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mk3
operator|.
name|nodeExists
argument_list|(
literal|"/c"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

