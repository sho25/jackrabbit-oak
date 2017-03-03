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
name|console
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|Executors
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
name|ScheduledExecutorService
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
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|core
operator|.
name|data
operator|.
name|FileDataStore
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
name|Oak
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
name|jcr
operator|.
name|Jcr
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
name|blob
operator|.
name|datastore
operator|.
name|DataStoreBlobStore
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
name|rdb
operator|.
name|RDBDataSourceFactory
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|IndexTracker
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexEditorProvider
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
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexProvider
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
name|index
operator|.
name|lucene
operator|.
name|hybrid
operator|.
name|DocumentQueue
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
name|spi
operator|.
name|blob
operator|.
name|BlobStore
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
name|spi
operator|.
name|commit
operator|.
name|Observer
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
name|spi
operator|.
name|query
operator|.
name|QueryIndexProvider
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
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
name|StatisticsProvider
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
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientURI
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoURI
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
comment|/**  * A tool to open a node store from command line options  */
end_comment

begin_class
specifier|public
class|class
name|NodeStoreOpener
block|{
specifier|private
specifier|static
specifier|final
name|long
name|MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
specifier|public
specifier|static
name|NodeStoreFixture
name|open
parameter_list|(
name|OptionParser
name|parser
parameter_list|,
name|boolean
name|writeMode
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|clusterId
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"clusterId"
argument_list|,
literal|"MongoMK clusterId"
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
literal|0
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|readWriteOption
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"read-write"
argument_list|,
literal|"connect to repository in read-write mode"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|String
argument_list|>
name|fdsPathSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"fds-path"
argument_list|,
literal|"Path to FDS store"
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
name|Void
argument_list|>
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
comment|// RDB specific options
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
name|nonOption
init|=
name|parser
operator|.
name|nonOptions
argument_list|(
literal|"{<path-to-repository> |<mongodb-uri>}"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Void
argument_list|>
name|disableBranchesSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"disableBranches"
argument_list|,
literal|"disable branches"
argument_list|)
decl_stmt|;
name|OptionSpec
argument_list|<
name|Integer
argument_list|>
name|cacheSizeSpec
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"cacheSize"
argument_list|,
literal|"cache size"
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
literal|0
argument_list|)
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
name|List
argument_list|<
name|String
argument_list|>
name|nonOptions
init|=
name|nonOption
operator|.
name|values
argument_list|(
name|options
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
if|if
condition|(
name|nonOptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|NodeStoreFixture
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// ignore
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
name|BlobStore
name|blobStore
init|=
literal|null
decl_stmt|;
name|String
name|fdsPath
init|=
name|fdsPathSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|fdsPath
argument_list|)
condition|)
block|{
name|File
name|fdsDir
init|=
operator|new
name|File
argument_list|(
name|fdsPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|fdsDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileDataStore
name|fds
init|=
operator|new
name|FileDataStore
argument_list|()
decl_stmt|;
name|fds
operator|.
name|setPath
argument_list|(
name|fdsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|fds
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|blobStore
operator|=
operator|new
name|DataStoreBlobStore
argument_list|(
name|fds
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|readOnly
init|=
operator|!
name|writeMode
operator|&&
operator|!
name|options
operator|.
name|has
argument_list|(
name|readWriteOption
argument_list|)
decl_stmt|;
name|NodeStoreFixture
name|fixture
decl_stmt|;
name|String
name|nodeStore
init|=
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeStore
operator|.
name|startsWith
argument_list|(
name|MongoURI
operator|.
name|MONGODB_PREFIX
argument_list|)
condition|)
block|{
name|MongoClientURI
name|uri
init|=
operator|new
name|MongoClientURI
argument_list|(
name|nodeStore
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|getDatabase
argument_list|()
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Database missing in MongoDB URI: "
operator|+
name|uri
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|MongoConnection
name|mongo
init|=
operator|new
name|MongoConnection
argument_list|(
name|uri
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|mongo
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|readOnly
condition|)
block|{
name|builder
operator|.
name|setReadOnlyMode
argument_list|()
expr_stmt|;
block|}
name|DocumentNodeStore
name|store
init|=
name|builder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|disableBranchesSpec
argument_list|)
condition|)
block|{
name|builder
operator|.
name|disableBranches
argument_list|()
expr_stmt|;
block|}
name|int
name|cacheSize
init|=
name|cacheSizeSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheSize
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
expr_stmt|;
block|}
name|fixture
operator|=
operator|new
name|MongoFixture
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeStore
operator|.
name|startsWith
argument_list|(
literal|"jdbc"
argument_list|)
condition|)
block|{
name|DataSource
name|ds
init|=
name|RDBDataSourceFactory
operator|.
name|forJdbcUrl
argument_list|(
name|nodeStore
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
argument_list|)
decl_stmt|;
name|DocumentMK
operator|.
name|Builder
name|builder
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
operator|.
name|setRDBConnection
argument_list|(
name|ds
argument_list|)
operator|.
name|setClusterId
argument_list|(
name|clusterId
operator|.
name|value
argument_list|(
name|options
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|readOnly
condition|)
block|{
name|builder
operator|.
name|setReadOnlyMode
argument_list|()
expr_stmt|;
block|}
name|DocumentNodeStore
name|store
init|=
name|builder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|disableBranchesSpec
argument_list|)
condition|)
block|{
name|builder
operator|.
name|disableBranches
argument_list|()
expr_stmt|;
block|}
name|int
name|cacheSize
init|=
name|cacheSizeSpec
operator|.
name|value
argument_list|(
name|options
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheSize
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|memoryCacheSize
argument_list|(
name|cacheSize
operator|*
name|MB
argument_list|)
expr_stmt|;
block|}
name|fixture
operator|=
operator|new
name|MongoFixture
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fixture
operator|=
name|SegmentTarFixture
operator|.
name|create
argument_list|(
operator|new
name|File
argument_list|(
name|nodeStore
argument_list|)
argument_list|,
name|readOnly
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
block|}
return|return
name|fixture
return|;
block|}
specifier|public
specifier|static
class|class
name|MongoFixture
implements|implements
name|NodeStoreFixture
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|MongoFixture
parameter_list|(
name|DocumentNodeStore
name|nodeStore
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeStore
name|getStore
parameter_list|()
block|{
return|return
name|nodeStore
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|nodeStore
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Session
name|openSession
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|nodeStore
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StatisticsProvider
name|statisticsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
name|ManagementFactory
operator|.
name|getPlatformMBeanServer
argument_list|()
argument_list|)
decl_stmt|;
name|oak
operator|.
name|getWhiteboard
argument_list|()
operator|.
name|register
argument_list|(
name|StatisticsProvider
operator|.
name|class
argument_list|,
name|statisticsProvider
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|LuceneIndexProvider
name|provider
init|=
name|NodeStoreOpener
operator|.
name|createLuceneIndexProvider
argument_list|()
decl_stmt|;
name|oak
operator|.
name|with
argument_list|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
name|NodeStoreOpener
operator|.
name|createLuceneIndexEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
name|Jcr
name|jcr
init|=
operator|new
name|Jcr
argument_list|(
name|oak
argument_list|)
decl_stmt|;
name|Repository
name|repository
init|=
name|jcr
operator|.
name|createRepository
argument_list|()
decl_stmt|;
return|return
name|repository
operator|.
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|LuceneIndexEditorProvider
name|createLuceneIndexEditorProvider
parameter_list|()
block|{
name|LuceneIndexEditorProvider
name|ep
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|()
decl_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|MoreExecutors
operator|.
name|getExitingScheduledExecutorService
argument_list|(
operator|(
name|ScheduledThreadPoolExecutor
operator|)
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|StatisticsProvider
name|statsProvider
init|=
name|StatisticsProvider
operator|.
name|NOOP
decl_stmt|;
name|int
name|queueSize
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"queueSize"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
name|DocumentQueue
name|queue
init|=
operator|new
name|DocumentQueue
argument_list|(
name|queueSize
argument_list|,
name|tracker
argument_list|,
name|executorService
argument_list|,
name|statsProvider
argument_list|)
decl_stmt|;
name|ep
operator|.
name|setIndexingQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
return|return
name|ep
return|;
block|}
specifier|private
specifier|static
name|LuceneIndexProvider
name|createLuceneIndexProvider
parameter_list|()
block|{
return|return
operator|new
name|LuceneIndexProvider
argument_list|()
return|;
block|}
block|}
end_class

end_unit

