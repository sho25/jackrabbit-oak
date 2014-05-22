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
name|benchmark
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Lists
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
name|Sets
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionParser
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSet
import|;
end_import

begin_import
import|import
name|joptsimple
operator|.
name|OptionSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|benchmark
operator|.
name|wikipedia
operator|.
name|WikipediaImport
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
name|fixture
operator|.
name|JackrabbitRepositoryFixture
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
name|fixture
operator|.
name|OakRepositoryFixture
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
name|fixture
operator|.
name|RepositoryFixture
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_class
specifier|public
class|class
name|BenchmarkRunner
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionParser
name|parser
init|=
operator|new
name|OptionParser
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|base
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"base"
argument_list|,
literal|"Base directory"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|host
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"host"
argument_list|,
literal|"MongoDB host"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|"localhost"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|port
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"port"
argument_list|,
literal|"MongoDB port"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|27017
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|dbName
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"db"
argument_list|,
literal|"MongoDB database"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|dropDBAfterTest
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"dropDBAfterTest"
argument_list|,
literal|"Whether to drop the MongoDB database after the test"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|mmap
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"mmap"
argument_list|,
literal|"TarMK memory mapping"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|"64"
operator|.
name|equals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|cache
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"cache"
argument_list|,
literal|"cache size (MB)"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|fdsCache
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"blobCache"
argument_list|,
literal|"cache size (MB)"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|32
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|wikipedia
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"wikipedia"
argument_list|,
literal|"Wikipedia dump"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|withStorage
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"storage"
argument_list|,
literal|"Index storage enabled"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|runAsAdmin
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"runAsAdmin"
argument_list|,
literal|"Run test using admin session"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|itemsToRead
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"itemsToRead"
argument_list|,
literal|"Number of items to read"
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|concurrency
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"concurrency"
argument_list|,
literal|"Number of test threads."
argument_list|)
operator|.
name|withRequiredArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|withValuesSeparatedBy
argument_list|(
literal|','
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|report
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"report"
argument_list|,
literal|"Whether to output intermediate results"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|randomUser
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"randomUser"
argument_list|,
literal|"Whether to use a random user to read."
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|File
argument_list|>
name|csvFile
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"csvFile"
argument_list|,
literal|"File to write a CSV version of the benchmark data."
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|File
operator|.
name|class
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Boolean
argument_list|>
name|flatStructure
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"flatStructure"
argument_list|,
literal|"Whether the test should use a flat structure or not."
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|numberOfUsers
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"numberOfUsers"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|ofType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
operator|.
name|defaultsTo
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|nonOption
init|=
name|parser
operator|.
name|nonOptions
argument_list|()
decl_stmt|;
name|OptionSpec
name|help
init|=
name|parser
operator|.
name|acceptsAll
argument_list|(
name|asList
argument_list|(
literal|"h"
argument_list|,
literal|"?"
argument_list|,
literal|"help"
argument_list|)
argument_list|,
literal|"show help"
argument_list|)
operator|.
name|forHelp
argument_list|()
decl_stmt|;
name|OptionSet
name|options
init|=
name|parser
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|help
argument_list|)
condition|)
block|{
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|out
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|int
name|cacheSize
init|=
name|cache
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
name|RepositoryFixture
index|[]
name|allFixtures
init|=
operator|new
name|RepositoryFixture
index|[]
block|{
operator|new
name|JackrabbitRepositoryFixture
argument_list|(
name|base
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMemory
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMemoryNS
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMemoryMK
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getH2MK
argument_list|(
name|base
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMongo
argument_list|(
name|host
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|port
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dbName
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dropDBAfterTest
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMongoWithFDS
argument_list|(
name|host
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|port
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dbName
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dropDBAfterTest
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
operator|*
name|MB
argument_list|,
name|base
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|fdsCache
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMongoNS
argument_list|(
name|host
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|port
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dbName
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dropDBAfterTest
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getMongoMK
argument_list|(
name|host
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|port
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dbName
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|dropDBAfterTest
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|cacheSize
operator|*
name|MB
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getTar
argument_list|(
name|base
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
literal|256
argument_list|,
name|cacheSize
argument_list|,
name|mmap
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
name|OakRepositoryFixture
operator|.
name|getTarWithBlobStore
argument_list|(
name|base
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
literal|256
argument_list|,
name|cacheSize
argument_list|,
name|mmap
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|Benchmark
index|[]
name|allBenchmarks
init|=
operator|new
name|Benchmark
index|[]
block|{
operator|new
name|OrderedIndexQueryOrderedIndexTest
argument_list|()
block|,
operator|new
name|OrderedIndexQueryStandardIndexTest
argument_list|()
block|,
operator|new
name|OrderedIndexQueryNoIndexTest
argument_list|()
block|,
operator|new
name|OrderedIndexInsertOrderedPropertyTest
argument_list|()
block|,
operator|new
name|OrderedIndexInsertStandardPropertyTest
argument_list|()
block|,
operator|new
name|OrderedIndexInsertNoIndexTest
argument_list|()
block|,
operator|new
name|LoginTest
argument_list|()
block|,
operator|new
name|LoginLogoutTest
argument_list|()
block|,
operator|new
name|LoginUserTest
argument_list|()
block|,
operator|new
name|LoginLogoutUserTest
argument_list|()
block|,
operator|new
name|NamespaceTest
argument_list|()
block|,
operator|new
name|NamespaceRegistryTest
argument_list|()
block|,
operator|new
name|ReadPropertyTest
argument_list|()
block|,
name|GetNodeTest
operator|.
name|withAdmin
argument_list|()
block|,
name|GetNodeTest
operator|.
name|withAnonymous
argument_list|()
block|,
operator|new
name|GetDeepNodeTest
argument_list|()
block|,
operator|new
name|SetPropertyTest
argument_list|()
block|,
operator|new
name|SetMultiPropertyTest
argument_list|()
block|,
operator|new
name|SmallFileReadTest
argument_list|()
block|,
operator|new
name|SmallFileWriteTest
argument_list|()
block|,
operator|new
name|ConcurrentReadTest
argument_list|()
block|,
operator|new
name|ConcurrentReadWriteTest
argument_list|()
block|,
operator|new
name|ConcurrentWriteReadTest
argument_list|()
block|,
operator|new
name|ConcurrentWriteTest
argument_list|()
block|,
operator|new
name|SimpleSearchTest
argument_list|()
block|,
operator|new
name|SQL2SearchTest
argument_list|()
block|,
operator|new
name|DescendantSearchTest
argument_list|()
block|,
operator|new
name|SQL2DescendantSearchTest
argument_list|()
block|,
operator|new
name|CreateManyChildNodesTest
argument_list|()
block|,
operator|new
name|CreateManyNodesTest
argument_list|()
block|,
operator|new
name|UpdateManyChildNodesTest
argument_list|()
block|,
operator|new
name|TransientManyChildNodesTest
argument_list|()
block|,
operator|new
name|WikipediaImport
argument_list|(
name|wikipedia
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|flatStructure
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|CreateNodesBenchmark
argument_list|()
block|,
operator|new
name|ManyNodes
argument_list|()
block|,
operator|new
name|ObservationTest
argument_list|()
block|,
operator|new
name|XmlImportTest
argument_list|()
block|,
operator|new
name|FlatTreeWithAceForSamePrincipalTest
argument_list|()
block|,
operator|new
name|ReadDeepTreeTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentReadDeepTreeTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentReadSinglePolicyTreeTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentReadAccessControlledTreeTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentReadAccessControlledTreeTest2
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentReadRandomNodeAndItsPropertiesTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentHasPermissionTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentHasPermissionTest2
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ManyUserReadTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|randomUser
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentTraversalTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|randomUser
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentWriteACLTest
argument_list|(
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|ConcurrentEveryoneACLTest
argument_list|(
name|runAsAdmin
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|itemsToRead
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
name|ReadManyTest
operator|.
name|linear
argument_list|(
literal|"LinearReadEmpty"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|EMPTY
argument_list|)
block|,
name|ReadManyTest
operator|.
name|linear
argument_list|(
literal|"LinearReadFiles"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|FILES
argument_list|)
block|,
name|ReadManyTest
operator|.
name|linear
argument_list|(
literal|"LinearReadNodes"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|NODES
argument_list|)
block|,
name|ReadManyTest
operator|.
name|uniform
argument_list|(
literal|"UniformReadEmpty"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|EMPTY
argument_list|)
block|,
name|ReadManyTest
operator|.
name|uniform
argument_list|(
literal|"UniformReadFiles"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|FILES
argument_list|)
block|,
name|ReadManyTest
operator|.
name|uniform
argument_list|(
literal|"UniformReadNodes"
argument_list|,
literal|1
argument_list|,
name|ReadManyTest
operator|.
name|NODES
argument_list|)
block|,
operator|new
name|ConcurrentCreateNodesTest
argument_list|()
block|,
operator|new
name|SequentialCreateNodesTest
argument_list|()
block|,
operator|new
name|CreateManyIndexedNodesTest
argument_list|()
block|,
operator|new
name|GetPoliciesTest
argument_list|()
block|,
operator|new
name|ConcurrentFileWriteTest
argument_list|()
block|,
operator|new
name|GetAuthorizableByIdTest
argument_list|(
name|numberOfUsers
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|flatStructure
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|GetAuthorizableByPrincipalTest
argument_list|(
name|numberOfUsers
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|flatStructure
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|GetPrincipalTest
argument_list|(
name|numberOfUsers
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|flatStructure
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|,
operator|new
name|FullTextSearchTest
argument_list|(
name|wikipedia
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|flatStructure
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|report
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|withStorage
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
block|}
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|argset
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|nonOption
operator|.
name|values
argument_list|(
name|options
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RepositoryFixture
argument_list|>
name|fixtures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RepositoryFixture
name|fixture
range|:
name|allFixtures
control|)
block|{
if|if
condition|(
name|argset
operator|.
name|remove
argument_list|(
name|fixture
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|fixtures
operator|.
name|add
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Benchmark
argument_list|>
name|benchmarks
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Benchmark
name|benchmark
range|:
name|allBenchmarks
control|)
block|{
if|if
condition|(
name|argset
operator|.
name|remove
argument_list|(
name|benchmark
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|benchmarks
operator|.
name|add
argument_list|(
name|benchmark
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|argset
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|PrintStream
name|out
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|csvFile
argument_list|)
condition|)
block|{
name|out
operator|=
operator|new
name|PrintStream
argument_list|(
name|FileUtils
operator|.
name|openOutputStream
argument_list|(
name|csvFile
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Benchmark
name|benchmark
range|:
name|benchmarks
control|)
block|{
if|if
condition|(
name|benchmark
operator|instanceof
name|CSVResultGenerator
condition|)
block|{
operator|(
operator|(
name|CSVResultGenerator
operator|)
name|benchmark
operator|)
operator|.
name|setPrintStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|benchmark
operator|.
name|run
argument_list|(
name|fixtures
argument_list|,
name|options
operator|.
name|valuesOf
argument_list|(
name|concurrency
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unknown arguments: "
operator|+
name|argset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

