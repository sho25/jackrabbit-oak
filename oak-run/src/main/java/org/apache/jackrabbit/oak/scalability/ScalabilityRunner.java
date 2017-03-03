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
name|scalability
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
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
name|CSVResultGenerator
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
name|util
operator|.
name|Date
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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|scalability
operator|.
name|benchmarks
operator|.
name|AggregateNodeSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|ConcurrentReader
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
name|scalability
operator|.
name|benchmarks
operator|.
name|ConcurrentWriter
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
name|scalability
operator|.
name|benchmarks
operator|.
name|FacetSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|FormatSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|FullTextSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|LastModifiedSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterOrderByKeysetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterOrderByOffsetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterOrderBySearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterSplitOrderByKeysetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterSplitOrderByOffsetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|MultiFilterSplitOrderBySearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|NodeTypeSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|OrderByDate
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
name|scalability
operator|.
name|benchmarks
operator|.
name|OrderByKeysetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|OrderByOffsetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|OrderBySearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|SplitOrderByKeysetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|SplitOrderByOffsetPageSearcher
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
name|scalability
operator|.
name|benchmarks
operator|.
name|SplitOrderBySearcher
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityBlobSearchSuite
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityNodeRelationshipSuite
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityNodeSuite
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
name|Charsets
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
name|Splitter
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
name|Maps
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

begin_comment
comment|/**  * Main class for running scalability/longevity tests.  *   */
end_comment

begin_class
specifier|public
class|class
name|ScalabilityRunner
block|{
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024L
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
name|String
argument_list|>
name|rdbjdbcuri
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbcuri"
argument_list|,
literal|"RDB JDBC URI"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|"jdbc:h2:./target/benchmark"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|rdbjdbcuser
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbcuser"
argument_list|,
literal|"RDB JDBC user"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|rdbjdbcpasswd
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbcpasswd"
argument_list|,
literal|"RDB JDBC password"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|rdbjdbctableprefix
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"rdbjdbctableprefix"
argument_list|,
literal|"RDB JDBC table prefix"
argument_list|)
operator|.
name|withOptionalArg
argument_list|()
operator|.
name|defaultsTo
argument_list|(
literal|""
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
name|getMemoryNS
argument_list|(
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
name|getMongoWithDS
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
name|getSegmentTar
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
name|getSegmentTarWithBlobStore
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
name|getRDB
argument_list|(
name|rdbjdbcuri
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbcuser
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbcpasswd
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbctableprefix
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
name|getRDBWithDS
argument_list|(
name|rdbjdbcuri
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbcuser
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbcpasswd
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|,
name|rdbjdbctableprefix
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
block|}
decl_stmt|;
name|ScalabilitySuite
index|[]
name|allSuites
init|=
operator|new
name|ScalabilitySuite
index|[]
block|{
operator|new
name|ScalabilityBlobSearchSuite
argument_list|(
name|withStorage
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|addBenchmarks
argument_list|(
operator|new
name|FullTextSearcher
argument_list|()
argument_list|,
operator|new
name|NodeTypeSearcher
argument_list|()
argument_list|,
operator|new
name|FormatSearcher
argument_list|()
argument_list|,
operator|new
name|FacetSearcher
argument_list|()
argument_list|,
operator|new
name|LastModifiedSearcher
argument_list|(
name|Date
operator|.
name|LAST_2_HRS
argument_list|)
argument_list|,
operator|new
name|LastModifiedSearcher
argument_list|(
name|Date
operator|.
name|LAST_24_HRS
argument_list|)
argument_list|,
operator|new
name|LastModifiedSearcher
argument_list|(
name|Date
operator|.
name|LAST_7_DAYS
argument_list|)
argument_list|,
operator|new
name|LastModifiedSearcher
argument_list|(
name|Date
operator|.
name|LAST_MONTH
argument_list|)
argument_list|,
operator|new
name|LastModifiedSearcher
argument_list|(
name|Date
operator|.
name|LAST_YEAR
argument_list|)
argument_list|,
operator|new
name|OrderByDate
argument_list|()
argument_list|)
block|,
operator|new
name|ScalabilityNodeSuite
argument_list|(
name|withStorage
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|addBenchmarks
argument_list|(
operator|new
name|OrderBySearcher
argument_list|()
argument_list|,
operator|new
name|SplitOrderBySearcher
argument_list|()
argument_list|,
operator|new
name|OrderByOffsetPageSearcher
argument_list|()
argument_list|,
operator|new
name|SplitOrderByOffsetPageSearcher
argument_list|()
argument_list|,
operator|new
name|OrderByKeysetPageSearcher
argument_list|()
argument_list|,
operator|new
name|SplitOrderByKeysetPageSearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterOrderBySearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterSplitOrderBySearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterOrderByOffsetPageSearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterSplitOrderByOffsetPageSearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterOrderByKeysetPageSearcher
argument_list|()
argument_list|,
operator|new
name|MultiFilterSplitOrderByKeysetPageSearcher
argument_list|()
argument_list|,
operator|new
name|ConcurrentReader
argument_list|()
argument_list|,
operator|new
name|ConcurrentWriter
argument_list|()
argument_list|)
block|,
operator|new
name|ScalabilityNodeRelationshipSuite
argument_list|(
name|withStorage
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
operator|.
name|addBenchmarks
argument_list|(
operator|new
name|AggregateNodeSearcher
argument_list|()
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
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|argmap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
comment|// Split the args to get suites and benchmarks (i.e. suite:benchmark1,benchmark2)
for|for
control|(
name|String
name|arg
range|:
name|argset
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|tokens
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|":"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|splitToList
argument_list|(
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|argmap
operator|.
name|put
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Splitter
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|trimResults
argument_list|()
operator|.
name|splitToList
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|argmap
operator|.
name|put
argument_list|(
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|argset
operator|.
name|remove
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|argmap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Warning: no scalability suites specified, "
operator|+
literal|"supported  are: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|allSuites
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ScalabilitySuite
argument_list|>
name|suites
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ScalabilitySuite
name|suite
range|:
name|allSuites
control|)
block|{
if|if
condition|(
name|argmap
operator|.
name|containsKey
argument_list|(
name|suite
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|benchmarks
init|=
name|argmap
operator|.
name|get
argument_list|(
name|suite
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// Only keep requested benchmarks
if|if
condition|(
name|benchmarks
operator|!=
literal|null
condition|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|iter
init|=
name|suite
operator|.
name|getBenchmarks
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|availBenchmark
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|benchmarks
operator|.
name|contains
argument_list|(
name|availBenchmark
argument_list|)
condition|)
block|{
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|suites
operator|.
name|add
argument_list|(
name|suite
argument_list|)
expr_stmt|;
name|argmap
operator|.
name|remove
argument_list|(
name|suite
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|argmap
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
argument_list|,
literal|false
argument_list|,
name|Charsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ScalabilitySuite
name|suite
range|:
name|suites
control|)
block|{
if|if
condition|(
name|suite
operator|instanceof
name|CSVResultGenerator
condition|)
block|{
operator|(
operator|(
name|CSVResultGenerator
operator|)
name|suite
operator|)
operator|.
name|setPrintStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|suite
operator|.
name|run
argument_list|(
name|fixtures
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

