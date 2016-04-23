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
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
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
name|segment
operator|.
name|SegmentNodeStore
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
name|segment
operator|.
name|SegmentStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|groovy
operator|.
name|tools
operator|.
name|shell
operator|.
name|IO
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

begin_comment
comment|/**  * A command line console.  */
end_comment

begin_class
specifier|public
class|class
name|Console
block|{
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
name|quiet
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"quiet"
argument_list|,
literal|"be less chatty"
argument_list|)
decl_stmt|;
name|OptionSpec
name|shell
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"shell"
argument_list|,
literal|"run the shell after executing files"
argument_list|)
decl_stmt|;
name|OptionSpec
name|readOnly
init|=
name|parser
operator|.
name|accepts
argument_list|(
literal|"read-only"
argument_list|,
literal|"connect to repository in read-only mode"
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
literal|"console {<path-to-repository> |<mongodb-uri>}"
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
name|parser
operator|.
name|printHelpOn
argument_list|(
name|System
operator|.
name|err
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
name|NodeStoreFixture
name|fixture
decl_stmt|;
if|if
condition|(
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|builder
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
expr_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|readOnly
argument_list|)
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
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
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
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|setBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|builder
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
expr_stmt|;
name|DocumentNodeStore
name|store
init|=
name|builder
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
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
name|FileStore
operator|.
name|Builder
name|fsBuilder
init|=
name|FileStore
operator|.
name|builder
argument_list|(
operator|new
name|File
argument_list|(
name|nonOptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withMaxFileSize
argument_list|(
literal|256
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|!=
literal|null
condition|)
block|{
name|fsBuilder
operator|.
name|withBlobStore
argument_list|(
name|blobStore
argument_list|)
expr_stmt|;
block|}
name|fixture
operator|=
operator|new
name|SegmentFixture
argument_list|(
name|fsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|scriptArgs
init|=
name|nonOptions
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
name|nonOptions
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|nonOptions
operator|.
name|size
argument_list|()
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
decl_stmt|;
name|IO
name|io
init|=
operator|new
name|IO
argument_list|()
decl_stmt|;
if|if
condition|(
name|options
operator|.
name|has
argument_list|(
name|quiet
argument_list|)
condition|)
block|{
name|io
operator|.
name|setVerbosity
argument_list|(
name|IO
operator|.
name|Verbosity
operator|.
name|QUIET
argument_list|)
expr_stmt|;
block|}
name|GroovyConsole
name|console
init|=
operator|new
name|GroovyConsole
argument_list|(
name|ConsoleSession
operator|.
name|create
argument_list|(
name|fixture
operator|.
name|getStore
argument_list|()
argument_list|)
argument_list|,
operator|new
name|IO
argument_list|()
argument_list|,
name|fixture
argument_list|)
decl_stmt|;
name|int
name|code
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|scriptArgs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|code
operator|=
name|console
operator|.
name|execute
argument_list|(
name|scriptArgs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scriptArgs
operator|.
name|isEmpty
argument_list|()
operator|||
name|options
operator|.
name|has
argument_list|(
name|shell
argument_list|)
condition|)
block|{
name|code
operator|=
name|console
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
name|code
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
interface|interface
name|NodeStoreFixture
extends|extends
name|Closeable
block|{
name|NodeStore
name|getStore
parameter_list|()
function_decl|;
block|}
specifier|private
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
specifier|private
specifier|static
class|class
name|SegmentFixture
implements|implements
name|NodeStoreFixture
block|{
specifier|private
specifier|final
name|SegmentStore
name|segmentStore
decl_stmt|;
specifier|private
specifier|final
name|SegmentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
name|SegmentFixture
parameter_list|(
name|SegmentStore
name|segmentStore
parameter_list|)
block|{
name|this
operator|.
name|segmentStore
operator|=
name|segmentStore
expr_stmt|;
name|this
operator|.
name|nodeStore
operator|=
name|SegmentNodeStore
operator|.
name|builder
argument_list|(
name|segmentStore
argument_list|)
operator|.
name|build
argument_list|()
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
name|segmentStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

