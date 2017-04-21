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
name|run
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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
name|TimeUnit
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
name|Collection
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
name|Document
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
name|DocumentNodeStore
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
name|MongoConnectionFactory
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
name|MongoUtils
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_class
specifier|public
class|class
name|RevisionsCommandTest
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
annotation|@
name|Rule
specifier|public
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|assumeMongoDB
parameter_list|()
block|{
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
name|before
parameter_list|()
block|{
name|ns
operator|=
name|createDocumentNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|info
parameter_list|()
throws|throws
name|Exception
block|{
name|ns
operator|.
name|getVersionGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|captureSystemOut
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
operator|new
name|RevisionsCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|,
literal|"info"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"Last Successful Run"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|Exception
block|{
name|ns
operator|.
name|getVersionGarbageCollector
argument_list|()
operator|.
name|gc
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
literal|"versionGC"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|captureSystemOut
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
operator|new
name|RevisionsCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|,
literal|"reset"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"resetting recommendations and statistics"
argument_list|)
argument_list|)
expr_stmt|;
name|MongoConnection
name|c
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|ns
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|doc
operator|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
literal|"versionGC"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|collect
parameter_list|()
throws|throws
name|Exception
block|{
name|ns
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|String
name|output
init|=
name|captureSystemOut
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
operator|new
name|RevisionsCommand
argument_list|()
operator|.
name|execute
argument_list|(
name|MongoUtils
operator|.
name|URL
argument_list|,
literal|"collect"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"starting gc collect"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DocumentNodeStore
name|createDocumentNodeStore
parameter_list|()
block|{
name|MongoConnection
name|c
init|=
name|connectionFactory
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|MongoUtils
operator|.
name|dropCollections
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|setMongoDB
argument_list|(
name|c
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|getNodeStore
argument_list|()
return|;
block|}
specifier|private
name|String
name|captureSystemOut
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|PrintStream
name|old
init|=
name|System
operator|.
name|out
decl_stmt|;
try|try
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|ps
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|r
operator|.
name|run
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toString
argument_list|()
return|;
block|}
finally|finally
block|{
name|System
operator|.
name|setOut
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
